package com.vaadin.testbench.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.HasTestBenchCommandExecutor;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.TestBenchTestCase;

/**
 * Descriptor class used for locating a component of a specific type.
 * 
 * Standard Vaadin UI component finders can be accessed through static methods
 * in the {@link Vaadin} class. A standard set of com.vaadin.ui.* classes are
 * also accepted as input to most functions expecting ComponentFinder class
 * references; for more information, see the JavaDoc for the {@link Vaadin}
 * class.
 * 
 * <p>
 * The typical end-user use case for a ComponentFinder is as follows - in this
 * example, we're looking for a com.vaadin.ui.Button from inside a
 * {@link TestBenchTestCase}:
 * <ul>
 * <li>Get TestBench to initialize a ComponentFinder for you:
 * 
 * <pre>
 * ButtonFinder bf = (ButtonFinder) find(ButtonFinder.class);
 * </pre>
 * 
 * </li>
 * <li>Tell the ButtonFinder we want the 5th button in the page:
 * 
 * <pre>
 * bf.atIndex(4);
 * </pre>
 * 
 * </li>
 * <li>Have the ButtonFinder search for the Button specified by its parameters:
 * 
 * <pre>
 * bf.done();
 * </pre>
 * 
 * </li>
 * <li>As a one-liner:
 * 
 * <pre>
 * WebElement e = find(ButtonFinder.class).atIndex(4).done();
 * </pre>
 * 
 * </li>
 * </ul>
 * <p>
 * For a more advanced example, we'll find the second Label inside a
 * VerticalLayout inside a GridLayout:
 * 
 * <pre>
 * WebElement e = find(GridLayoutFinder.class).find(VerticalLayoutFinder.class)
 *         .find(LabelFinder.class).atIndex(1).done();
 * </pre>
 * <p>
 * The {@link TestBenchTestCase} class has shorthand/helper functions to
 * minimize the need to interact with ComponentFinders directly.
 * <p>
 * 
 * To find your own component, extend this class and initialize it with the
 * client-side name of your component (i.e., to find a client-side widget named
 * MyWidget, call {@code super("MyWidget");} in the default constructor).
 */
public abstract class ComponentFinder {

    private static Logger logger = Logger.getLogger(ComponentFinder.class
            .getName());

    private String base_expr;
    private String caption_expr;
    private String index_expr;
    private List<String> statevars;
    private List<String> statevals;

    /**
     * Instantiate a new generic ComponentFinder for a given client widget.
     * 
     * @param component_name
     *            the class name of the client widget; for example, the
     *            client-side widget of the com.vaadin.ui.Button class is called
     *            "VButton", so the ButtonFinder constructor calls
     *            {@code super("VButton");}
     */
    public ComponentFinder(String component_name) {
        base_expr = "//" + component_name;
        caption_expr = base_expr + "[caption=\"%s\"]";
        index_expr = base_expr + "[%d]";

        statevars = new ArrayList<String>();
        statevals = new ArrayList<String>();
    }

    private String caption = null;
    private int index = 0;
    private SearchContext searchContext;

    /**
     * Add a requirement to accept widgets with the special id state set to a
     * certain value
     * 
     * @param id
     *            a String value
     * @return a reference to self
     */
    public <C extends ComponentFinder> C withId(String id) {
        return withStateVariable("id", id);
    }

    /**
     * Add a requirement to only accept widgets with a specified caption value
     * 
     * @param caption
     *            a String value
     * @return a reference to self
     */
    @SuppressWarnings("unchecked")
    public <C extends ComponentFinder> C withCaption(String caption) {
        this.caption = caption;
        return (C) this;
    }

    /**
     * Add a requirement to only accept widgets that have a certain state
     * variable set
     * 
     * @param varname
     *            name of the desired Vaadin object state variable
     * 
     * @return a reference to self
     */
    public <C extends ComponentFinder> C withStateVariable(String varname) {
        return withStateVariable(varname, null);
    }

    /**
     * Add a requirement to only accept widgets that have a certain state
     * variable set to a specific value
     * 
     * @param varname
     *            name of the desired Vaadin object state variable
     * @param value
     *            the state variable value as a {@link String}
     * @return a reference to self or null if an invalid variable name was
     *         entered
     */
    @SuppressWarnings("unchecked")
    public <C extends ComponentFinder> C withStateVariable(String varname,
            String value) {

        // NOTE: this should be changed to a regexp if someone has the time
        try {
            Integer.parseInt(varname);
        } catch (NumberFormatException e) {
            statevars.add(varname);
            statevals.add(value);
            return (C) this;
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
    @SuppressWarnings("unchecked")
    public <C extends ComponentFinder> C atIndex(int index) {
        this.index = index;
        return (C) this;
    }

    /**
     * Specify a different {@link SearchContext} to use.
     * 
     * @param searchContext
     *            a {@link SearchContext}; usually a {@link TestBenchElement}
     *            instance.
     * @return a reference to self
     */
    @SuppressWarnings("unchecked")
    public <C extends ComponentFinder> C inContext(SearchContext searchContext) {
        this.searchContext = searchContext;
        return (C) this;
    }

    /**
     * Return the {@link SearchContext} previously set with the
     * {@link inContext} call.
     */
    protected SearchContext getContext() {
        return searchContext;
    }

    /**
     * When this method is called, appropriate calls to the Selenium testing
     * APIs are made to retrieve the first WebElement matching the criteria
     * tracked by this ComponentLocator. This WebElement is further wrapped as a
     * TestBenchElement to allow usage of TestBench specific functionality.
     * 
     * @return a TestBenchElement instance, or null if no elements could be
     *         found matching this {@link ComponentFinder}'s criteria.
     */
    public TestBenchElement done() {
        List<WebElement> results = null;
        String query = null;

        if (getCaption() != null) {
            query = String.format(caption_expr, getCaption());
            logger.fine("Finding Vaadin component by caption query: " + query);
        } else if (getIndex() != 0) {
            query = String.format(index_expr, getIndex());
            logger.fine("Finding Vaadin component by index query: " + query);
        } else if (statevars.size() > 0) {
            query = base_expr + "[";
            for (int i = 0, l = statevars.size(); i < l; ++i) {
                query += statevars.get(i);
                if (statevals.get(i) != null) {
                    query += "=\"" + statevals.get(i) + "\"";
                } else {
                    query += "=?";
                }
                if (i != l - 1) {
                    query += ", ";
                }
            }
            query += "]";
            logger.fine("Finding Vaadin component by state variable query: "
                    + query);
        } else {
            query = base_expr;
            logger.fine("Finding Vaadin component by type query: " + query);
        }

        results = getContext().findElements(By.vaadin(query));

        if (results == null) {
            logger.warning("Query " + query
                    + " returned null! This is an implementation error!");
            return null;
        }

        if (results.isEmpty()) {
            logger.fine("Query " + query + " returned an empty list.");
            return null;
        }

        logger.fine("Query "
                + query
                + " completed successfully. Returning TestBenchElement at index "
                + getIndex());

        // Wrap result into a TestBenchElement if necessary
        WebElement elem = results.get(0);

        if (elem instanceof TestBenchElement) {
            return (TestBenchElement) elem;
        }

        return new TestBenchElement(elem,
                ((HasTestBenchCommandExecutor) elem)
                        .getTestBenchCommandExecutor());
    }

    /**
     * Return the previously set caption string.
     */
    protected String getCaption() {
        return caption;
    }

    /**
     * Return the previously set index value.
     */
    protected int getIndex() {
        return index;
    }

    /**
     * Instantiate a new type of Finder using the {@link TestBenchElement}
     * identified by this finder as context
     * 
     * @param finderType
     *            a {@link ComponentFinder} subclass
     * @return a new {@link ComponentFinder} instance
     */
    public <C extends ComponentFinder> C find(Class<C> finderType) {
        if (finderType == null) {
            throw new IllegalArgumentException("finderType may not be null");
        }
        if (searchContext == null) {
            throw new IllegalArgumentException("searchContext may not be null");
        }

        C finder = newFinderInstance(finderType);
        if (finder != null) {
            TestBenchElement element = done();
            finder.inContext(element);
        }

        return finder;
    }

    /**
     * Internal finder instantiation and error checking logic
     * 
     * @param finderType
     * @return a new {@link ComponentFinder} instance, or null if failed
     */
    private <F extends ComponentFinder> F newFinderInstance(Class<F> finderType) {
        try {
            return finderType.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.WARNING, "Failed to instantiate FinderType "
                    + finderType.getName() + ", reason: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.log(
                    Level.WARNING,
                    "Access violation while instantiating FinderType "
                            + finderType.getName() + ", reason: "
                            + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(
                    Level.WARNING,
                    "Unknown exception thrown while instantiating FinderType "
                            + finderType.getName() + ", reason: "
                            + e.getMessage(), e);
        }
        return null;
    }
}
