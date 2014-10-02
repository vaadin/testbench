/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.testbench.elementsbase.ServerClass;

/**
 * Descriptor class used for locating components.
 * 
 * Different Element classes that extend AbstractElement class and have
 * ServerClass annotation describing the server side class name of a Vaadin
 * component are used by ElementQuery.
 * 
 * <p>
 * The typical end-user use case for a ElementQuery is as follows - in this
 * example, we're looking for the fifth Vaadin Button from inside a
 * {@link TestBenchTestCase}:
 * </p>
 * 
 * {@code ButtonElement e = $(ButtonElement.class).index(4).first(); } <br>
 * or<br>
 * {@code ButtonElement e = $(ButtonElement.class).all().get(4); }<br>
 * 
 * <p>
 * Second alternative results in slightly more network traffic. These two are
 * not identical when you use search hierarchy. See the documentation for
 * {@link #index(int)}.
 * </p>
 * 
 * <p>
 * For a more advanced example, we'll find the second Label inside a
 * VerticalLayout inside a GridLayout:
 * </p>
 * 
 * {@code LabelElement e = $(LabelElement.class).index(1).in(VerticalLayoutElement.class) }
 * {@code .in(GridLayoutElement.class).first(); }
 * 
 * <p>
 * To find your own component, extend AbstractElement class and use ServerClass
 * annotation to define it's full class name e.g. com.vaadin.example.MyWidget
 * </p>
 */
public class ElementQuery<T extends AbstractElement> {

    private static Logger logger = Logger.getLogger(ElementQuery.class
            .getName());

    private Map<String, Set<String>> statevars = new LinkedHashMap<String, Set<String>>();
    private int idx = -1;
    private SearchContext searchContext;
    private boolean recursive = true;
    Class<T> elementClass;
    private Stack<ElementQuery<?>> queryStack = new Stack<ElementQuery<?>>();

    /**
     * Instantiate a new ElementQuery for given type.
     * 
     * @param elementClass
     *            AbstractElement subclass
     */
    public ElementQuery(Class<T> elementClass) {
        this.elementClass = elementClass;
    }

    /**
     * For advanced use. Set ElementQuery to either recursive search or a search
     * starting directly from he context.
     * 
     * @param recursion
     *            Boolean value. false for direct child search
     * @return a reference to self
     */
    public ElementQuery<T> recursive(boolean recursion) {
        this.recursive = recursion;
        return this;
    }

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. If this function gets called through an element,
     * it uses the element as its search context. Otherwise the search context
     * is the driver.
     * 
     * @return an appropriate {@link ElementQuery} instance
     */
    public <E extends AbstractElement> ElementQuery<E> $(Class<E> cls) {
        ElementQuery<E> newQuery = new ElementQuery<E>(cls)
                .context(getContext());
        newQuery.queryStack.push(this);
        return newQuery;
    }

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. If this function gets called through an element,
     * it uses the element as its search context. Otherwise the search context
     * is the driver.
     * 
     * This search is not recursive and can find the given hierarchy only if it
     * can be found as direct children of given context. The same can be done
     * with {@code $(Foo.class).recursive(false) }
     * 
     * @return an appropriate {@link ElementQuery} instance
     */
    public <E extends AbstractElement> ElementQuery<E> $$(Class<E> cls) {
        ElementQuery<E> newQuery = new ElementQuery<E>(cls)
                .context(getContext());
        newQuery.recursive(false);
        newQuery.queryStack.push(this);
        return newQuery;
    }

    /**
     * Adds another query to the search hierarchy of this ElementQuery. This
     * search is recursive.
     * 
     * @param query
     *            ElementQuery instance to be used as part of search hierarchy.
     * @return a reference to self
     */
    public <E extends AbstractElement> ElementQuery<T> in(ElementQuery<E> query) {
        queryStack.push(query);
        return this;
    }

    /**
     * Adds another query to the search hierarchy of this ElementQuery. This
     * search is non-recursive. Only direct children are found.
     * 
     * @param query
     *            ElementQuery instance to be used as part of search hierarchy.
     * @return a reference to self
     */
    public <E extends AbstractElement> ElementQuery<T> child(
            ElementQuery<E> query) {
        if (queryStack.isEmpty()) {
            recursive(false);
        } else {
            queryStack.peek().recursive(false);
        }
        return in(query);
    }

    /**
     * Executes a search for element with given ID.
     * 
     * @param id
     *            a String value
     * @return found element
     */
    public T id(String id) {
        return state("id", id).first();
    }

    /**
     * Add a requirement to only accept components with a specified caption
     * value. If query has a search hierarchy set with .in() or .childOf() the
     * requirement is applied to the latest referenced Element type.
     * 
     * @param caption
     *            a String value
     * @return a reference to self
     */
    public ElementQuery<T> caption(String caption) {
        return state("caption", caption);
    }

    /**
     * For advanced use. Add a requirement to only accept components that have a
     * certain state variable set to a specific value. If query has a search
     * hierarchy set with .in() or .childOf() the requirement is applied to the
     * latest referenced Element type.
     * 
     * Using null as a value checks for existence of the given variable, and
     * accepts any value. This is usually not what you want to do.
     * 
     * Please note that names of state variables are component specific and are
     * subject to change.
     * 
     * 
     * @param varname
     *            name of the desired Vaadin object state variable
     * @param value
     *            the state variable value as a {@link String}
     * @return a reference to self or null if an invalid variable name was
     *         entered
     */
    public ElementQuery<T> state(String varname, String value) {
        Pattern p = Pattern.compile("[0-9]+");
        Matcher m = p.matcher(varname);
        boolean isValid = !m.matches();
        if (isValid) {
            if (!statevars.containsKey(varname)) {
                statevars.put(varname, new LinkedHashSet<String>());
            }
            statevars.get(varname).add(value);
            return this;
        } else {
            logger.warning("State variable is invalid! State variable name cannot be an integer.");
            return null;
        }

    }

    /**
     * Set an index requirement for the current ElementQuery. If query has a
     * context hierarchy set using {@code .in()} or {@code .childOf()} the index
     * requirement is set for the latest referenced Element type. For example
     * the {@code .index()} call in {@code $(Foo.class).index(4)} would apply to
     * the {@code Foo} type (referring to the fifth instance of {@code Foo} that
     * can be found in the current search context) in the hierarchy, while in
     * {@code $(Foo.class).in(Bar.class).index(3)} it would apply to the Bar
     * type, and the search would refer to <b>all {@code Foo} instances</b>
     * found in every Bar instance which is the <b>fourth {@code Bar}
     * instance</b> within the search context and any and all sub-contexts.
     * 
     * When searching for one Element without search hierarchy this is
     * equivalent to {@code .all().get(index)}.
     * <p>
     * Note the difference between {@code .in()} and {@code .childOf()}:<br>
     * {@code $(LabelElement.class).index(1).in(HorizontalLayoutElement.class)}
     * does a depth first search in all {@code HorizontalLayout} instances and
     * returns the second {@code Label} instance from each result.<br>
     * 
     * {@code $(LabelElement.class).index(1).childOf(HorizontalLayoutElement.class)}
     * only finds {@code Labels} the second <b>direct children</b> of all
     * {@code HorizontalLayout} instances.
     * 
     * @param index
     *            zero-based index of the desired element
     * 
     * @return a reference to self
     */
    public ElementQuery<T> index(int index) {
        idx = index;
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
    public ElementQuery<T> context(SearchContext searchContext) {
        this.searchContext = searchContext;
        return this;
    }

    /**
     * Return the {@link SearchContext} previously set with the {@link #in}
     * call.
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
    public T first() {
        return get(0);
    }

    /**
     * Search the open Vaadin application for a matching component relative to
     * given context. Elements are post filtered with given index.
     * 
     * @param index
     *            Post filtering index
     * @return Component as a corresponding element
     */
    public T get(int index) {
        String query;
        try {
            query = "(" + generateQuery() + ")[" + index + "]";
        } catch (Exception e) {
            throw new RuntimeException("Unable to create locator query", e);
        }

        List<T> elements = executeSearch(query);
        if (elements.isEmpty()) {
            final String errorString = "Vaadin could not find elements with selector "
                    + query;
            throw new NoSuchElementException(errorString);
        }
        return elements.get(0);
    }

    /**
     * Checks if this ElementQuery describes existing elements. Same as
     * .all().isEmpty().
     * 
     * @return true if elements exists. false if not
     */
    public boolean exists() {
        return !all().isEmpty();
    }

    /**
     * Search the open Vaadin application for a list of matching components
     * relative to given context.
     * 
     * @return Components as a list of corresponding elements
     */
    public List<T> all() {
        String query;
        try {
            query = generateQuery();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create locator query", e);
        }

        return executeSearch(query);
    }

    /**
     * Execute the actual search with Vaadin.
     * 
     * @param query
     *            Generated selector path
     * @return List of elements. List is empty if no elements are found.
     */
    private List<T> executeSearch(String query) {
        List<WebElement> results = null;
        List<T> elements = new ArrayList<T>();
        results = By.vaadin(query).findElements(getContext());

        TestBenchCommandExecutor tbCommandExecutor = ((HasTestBenchCommandExecutor) getContext())
                .getTestBenchCommandExecutor();
        for (WebElement webElement : results) {
            T element = TestBench.createElement(elementClass, webElement,
                    tbCommandExecutor);

            if (null != element) {
                elements.add(element);
            }
        }

        return elements;
    }

    /**
     * Generates a vaadin locator path.
     * 
     * @return String in form of
     *         //fullServerSideClassName([(StateVar=StateVal,?)*(index)?])?
     */
    @SuppressWarnings("unchecked")
    protected String generateQuery() {
        String output = "";
        Stack<ElementQuery<?>> tmpStack = (Stack<ElementQuery<?>>) queryStack
                .clone();

        // Chain all the queries from stack.
        while (!tmpStack.isEmpty()) {
            ElementQuery<?> current = tmpStack.pop();
            output += current.generateQuery();
        }

        output += (recursive ? "//" : "/");
        output += elementClass.getAnnotation(ServerClass.class).value();

        if (statevars.size() > 0 || idx >= 0) {
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

            if (idx >= 0) {
                if (statevars.size() > 0) {
                    output += ",";
                }
                output += idx;
            }

            output += "]";
        }

        return output;
    }
}
