package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.elements.AbstractElement;
import com.vaadin.testbench.elements.ServerClass;

/**
 * Descriptor class used for locating components.
 * 
 * Different Element classes that extend AbstractElement class and have
 * ServerClass annotation describing the server side class name of a Vaadin
 * component are used by ComponentFinder.
 * 
 * <p>
 * The typical end-user use case for a ComponentFinder is as follows - in this
 * example, we're looking for a Vaadin Button from inside a
 * {@link TestBenchTestCase}:
 * <ul>
 * <li>Get TestBench to initialize a ComponentFinder for you:
 * 
 * <pre>
 * ComponentFinder&lt;ButtonElement&gt; bf = find(ButtonElement.class);
 * </pre>
 * 
 * </li>
 * <li>Tell the ComponentFinder we want the 5th element of later given type in
 * the page:
 * 
 * <pre>
 * bf.atIndex(4);
 * </pre>
 * 
 * </li>
 * <li>Have the ComponentFinder search for a ButtonElement specified by its
 * parameters:
 * 
 * <pre>
 * bf.getElement();
 * </pre>
 * 
 * </li>
 * <li>As a one-liner using shortcut function:
 * 
 * <pre>
 * ButtonElement e = find(ButtonElement.class).getElementAtIndex(4);
 * </pre>
 * 
 * </li>
 * </ul>
 * <p>
 * For a more advanced example, we'll find the second Label inside a
 * VerticalLayout inside a GridLayout:
 * 
 * <pre>
 * LabelElement e = find(GridLayoutElement.class)
 *         .find(VerticalLayoutElement.class).find(LabelElement.class)
 *         .getElementAtIndex(1);
 * </pre>
 * <p>
 * The {@link TestBenchTestCase} class and all Element classes have
 * shorthand/helper functions to minimize the need to interact with
 * ComponentFinders directly.
 * <p>
 * 
 * To find your own component, extend AbstractElement class and use ServerClass
 * annotation to define it's full class name e.g. com.vaadin.example.MyWidget
 */
public class ComponentFinder<T extends AbstractElement> {

    private static Logger logger = Logger.getLogger(ComponentFinder.class
            .getName());

    private Map<String, Set<String>> statevars;
    private int index = -1;
    private SearchContext searchContext;
    private Class<T> clazz;

    /**
     * Instantiate a new ComponentFinder for given type
     * 
     * @param clazz
     *            AbstractElement subclass
     */
    public ComponentFinder(Class<T> clazz) {
        this.clazz = clazz;
        statevars = new LinkedHashMap<String, Set<String>>();
    }

    /**
     * Create new ComponentFinder instance for given class. New finder will use
     * the first result from current finder as a search context
     * 
     * @param clazz
     *            AbstractElement subclass
     * @return ComponentFinder for given type with search context set
     */
    public <C extends AbstractElement> ComponentFinder<C> find(Class<C> clazz) {
        return new ComponentFinder<C>(clazz).inContext(getElement());
    }

    /**
     * Add a requirement to accept components with the special id state set to a
     * certain value. This refers to Vaadin server side id that can be set for
     * components with .setId().
     * 
     * @param id
     *            a String value or null
     * @return a reference to self
     */
    public ComponentFinder<T> withId(String id) {
        return withStateVariable("id", id);
    }

    /**
     * Add a requirement to only accept components with a specified caption
     * value.
     * 
     * @param caption
     *            a String value
     * @return a reference to self
     */
    public ComponentFinder<T> withCaption(String caption) {
        return withStateVariable("caption", caption);
    }

    /**
     * Add a requirement to only accept components that have a certain state
     * variable set. The value of said variable does not matter. For advanced
     * use. Please note that names of state variables are component specific and
     * are subject to change
     * 
     * @param varname
     *            name of the desired Vaadin object state variable
     * 
     * @return a reference to self
     */
    public ComponentFinder<T> withStateVariable(String varname) {
        return withStateVariable(varname, null);
    }

    /**
     * Add a requirement to only accept components that have a certain state
     * variable set to a specific value. For advanced use. Please note that
     * names of state variables are component specific and are subject to change
     * 
     * @param varname
     *            name of the desired Vaadin object state variable
     * @param value
     *            the state variable value as a {@link String}
     * @return a reference to self or null if an invalid variable name was
     *         entered
     */
    public ComponentFinder<T> withStateVariable(String varname, String value) {

        // NOTE: this should be changed to a regexp if someone has the time
        try {
            Integer.parseInt(varname);
        } catch (NumberFormatException e) {
            if (!statevars.containsKey(varname)) {
                statevars.put(varname, new LinkedHashSet<String>());
            }
            statevars.get(varname).add(value);
            return this;
        }

        logger.warning("State variable name cannot be an integer!");
        return null;

    }

    /**
     * Return the nth matching element - i.e. a {@code ButtonFinder.atIndex(4)}
     * would return the fifth Vaadin Button instance in the current search
     * context
     * 
     * @param index
     *            zero-based index of the desired element within the search
     *            context
     * 
     * @return a reference to self
     */
    public ComponentFinder<T> atIndex(int index) {
        this.index = index;
        return this;
    }

    /**
     * Specify a different {@link SearchContext} to use.
     * 
     * @param searchContext
     *            a {@link SearchContext}; usually a {@link TestBenchElement}
     *            instance.
     * @return a reference to self
     */
    public ComponentFinder<T> inContext(SearchContext searchContext) {
        this.searchContext = searchContext;
        return this;
    }

    /**
     * Return the {@link SearchContext} previously set with the
     * {@link inContext} call.
     */
    protected SearchContext getContext() {
        return searchContext;
    }

    /**
     * Search the open Vaadin application for a matching component relative to
     * given context.
     * 
     * @return Component as a corresponding element
     */
    public T getElement() {
        return getElements().get(0);
    }

    /**
     * Search the open Vaadin application for a matching component relative to
     * given context.
     * 
     * @param index
     *            0 based index of element
     * @return Component as a corresponding element
     */
    public T getElementAtIndex(int index) {
        return atIndex(index).getElements().get(0);
    }

    /**
     * Search the open Vaadin application for a matching component relative to
     * given context.
     * 
     * @param caption
     *            Caption string for element
     * @return Component as a corresponding element
     */
    public T getElementWithCaption(String caption) {
        return withCaption(caption).getElements().get(0);
    }

    /**
     * Search the open Vaadin application for a matching component relative to
     * given context.
     * 
     * @param id
     *            Id of wanted elements
     * @return Component as a corresponding element
     */
    public T getElementWithId(String id) {
        return withId(id).getElements().get(0);
    }

    /**
     * Search the open Vaadin application for a list of matching components
     * relative to given context.
     * 
     * @param index
     *            0 based index of element
     * @return Components as a list of corresponding elements
     */
    public List<T> getElementsAtIndex(int index) {
        return atIndex(index).getElements();
    }

    /**
     * Search the open Vaadin application for a list of matching components
     * relative to given context.
     * 
     * @param caption
     *            Caption string for element
     * @return Components as a list of corresponding elements
     */
    public List<T> getElementsWithCaption(String caption) {
        return withCaption(caption).getElements();
    }

    /**
     * Search the open Vaadin application for a list of matching components
     * relative to given context.
     * 
     * @param id
     *            Id of wanted elements
     * @return Components as a list of corresponding elements
     */
    public List<T> getElementsWithId(String id) {
        return withId(id).getElements();
    }

    /**
     * Search the open Vaadin application for a list of matching components
     * relative to given context.
     * 
     * @return Components as a list of corresponding elements
     */
    public List<T> getElements() {
        List<WebElement> results = null;
        List<T> elements = new ArrayList<T>();
        String query;
        try {
            query = generateQuery();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create locator query");
        }

        results = By.vaadin(query).findElements(getContext());

        TestBenchCommandExecutor tbCommandExecutor = ((HasTestBenchCommandExecutor) getContext())
                .getTestBenchCommandExecutor();
        for (WebElement elem : results) {
            T element;
            try {
                element = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to create new element instance");
            }
            element.init(elem, tbCommandExecutor);
            elements.add(element);
        }

        if (index >= 0) {
            if (index > elements.size()) {
                elements.clear();
            } else {
                T element = elements.get(index);
                elements.clear();
                elements.add(element);
            }
        }

        return elements;
    }

    /**
     * Generates a vaadin locator path
     * 
     * @return String in form of
     *         //fullServerSideClassName([(StateVar=StateVal,?)*(,index)?])?
     */
    private String generateQuery() {
        String output = "//" + clazz.getAnnotation(ServerClass.class).value();

        if (statevars.size() > 0) {
            output += "[";
            boolean first = true;

            for (Map.Entry<String, Set<String>> entry : statevars.entrySet()) {
                String key = entry.getKey();

                for (String value : entry.getValue()) {
                    if (!first) {
                        output += ",";
                    } else {
                        first = false;
                    }

                    value = (null == value ? "?" : "\"" + value + "\"");
                    output += key + "=" + value;
                }
            }

            output += "]";
        }

        return output;
    }
}
