package com.vaadin.testbench;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.finder.ComponentFinder;
import com.vaadin.testbench.finder.Vaadin;

/**
 * A superclass with some helpers to aid TestBench developers. This superclass
 * is also used by tests created by the Recorder.
 */
public abstract class TestBenchTestCase implements HasDriver {

    protected WebDriver driver;

    /**
     * Convenience method that casts the specified {@link WebDriver} instance to
     * an instance of {@link TestBenchCommands}, making it easy to access the
     * special TestBench commands.
     * 
     * @param webDriver
     *            The WebDriver instance to cast.
     * @return a WebDriver cast to TestBenchCommands
     */
    public static TestBenchCommands testBench(WebDriver webDriver) {
        return (TestBenchCommands) webDriver;
    }

    /**
     * Convenience method the return {@link TestBenchCommands} for the default
     * {@link WebDriver} instance.
     * 
     * @return The driver cast to a TestBenchCommands instance.
     */
    public TestBenchCommands testBench() {
        return (TestBenchCommands) getDriver();
    }

    /**
     * Convenience method that casts the specified {@link WebElement} instance
     * to an instance of {@link TestBenchElementCommands}, making it easy to
     * access the special TestBench commands.
     * 
     * @param webElement
     *            The WebElement to cast.
     * @return The WebElement cast to a TestBenchElementCommands instance.
     */
    public TestBenchElementCommands testBenchElement(WebElement webElement) {
        return (TestBenchElementCommands) webElement;
    }

    /**
     * Combines a base URL with an URI to create a final URL. This removes
     * possible double slashes if the base URL ends with a slash and the URI
     * begins with a slash.
     * 
     * @param baseUrl
     *            the base URL
     * @param uri
     *            the URI
     * @return the URL resulting from the combination of base URL and URI
     */
    protected String concatUrl(String baseUrl, String uri) {
        if (baseUrl.endsWith("/") && uri.startsWith("/")) {
            return baseUrl + uri.substring(1);
        }
        return baseUrl + uri;
    }

    /**
     * Returns true if an element can be found from the driver with given
     * selector.
     * 
     * @param by
     *            the selector used to find element
     * @return true if the element can be found
     */
    public boolean isElementPresent(By by) {
        return !getDriver().findElements(by).isEmpty();
    }

    /**
     * Returns the {@link WebDriver} instance previously specified by {@link
     * setDriver()}, or (if the previously provided WebDriver instance was not
     * already a {@link TestBenchDriverProxy} instance) a
     * {@link TestBenchDriverProxy} that wraps that driver.
     * 
     * @return the active WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Sets the active {@link WebDriver} that is used by this this case
     * 
     * @param driver
     *            The WebDriver instance to set.
     */
    public void setDriver(WebDriver driver) {
        if (!(driver instanceof TestBenchDriverProxy)) {
            driver = TestBench.createDriver(driver);
        }
        this.driver = driver;
    }

    /**
     * Prepare a {@link ComponentFinder} instance to use for locating a specific
     * {@link WebElement} on the client, relative to the document root. The
     * returned object can be manipulated to uniquely identify the sought-after
     * object.
     * 
     * @param finder
     *            Either a {@link ComponentFinder} class, or a com.vaadin.ui.*
     *            class supported by the automatic component->finder mapping.
     * @return an appropriate {@link ComponentFinder} instance
     */
    public <T extends ComponentFinder> T find(Class<?> finder) {
        return find(finder, driver);
    }

    /**
     * Prepare a {@link ComponentFinder} instance to use for locating a specific
     * {@link WebElement} on the client, relative to a user-specified context.
     * The returned object can be manipulated to uniquely identify the
     * sought-after object.
     * 
     * @param finder
     *            Either a {@link ComponentFinder} class, or a com.vaadin.ui.*
     *            class supported by the automatic component->finder mapping.
     * @param context
     *            any instance of a class implementing {@link SearchContext}
     * @return an appropriate {@link ComponentFinder} instance
     */
    public <T extends ComponentFinder> T find(Class<?> finder,
            SearchContext context) {
        return Vaadin.find(finder, context);
    }

    /**
     * Retrieve the first matching {@link WebElement} specified by a
     * {@link ComponentFinder} class or supported com.vaadin.ui.* class,
     * starting from the document root.
     * 
     * @param finder
     *            Either a {@link ComponentFinder} class, or a com.vaadin.ui.*
     *            class supported by the automatic component->finder mapping.
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElement(Class<?> finder) {
        return getElement(finder, driver);
    }

    /**
     * Retrieve the first matching {@link WebElement} specified by a
     * {@link ComponentFinder} class or supported com.vaadin.ui.* class,
     * relative to a user-specified {@link SearchContext}
     * 
     * @param finder
     *            Either a {@link ComponentFinder} class, or a com.vaadin.ui.*
     *            class supported by the automatic component->finder mapping.
     * @param context
     *            any instance of a class implementing {@link SearchContext}
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElement(Class<?> finder, SearchContext context) {
        return find(finder, context).done();
    }

    /**
     * Return the Nth {@link WebElement} matching the specified
     * {@link ComponentFinder} class or supported com.vaadin.ui.* class,
     * relative to the document root.
     * 
     * @param finder
     *            Either a {@link ComponentFinder} class, or a com.vaadin.ui.*
     *            class supported by the automatic component->finder mapping.
     * @param index
     *            zero-based index of the desired component
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElementByIndex(Class<?> finder, int index) {
        return getElementByIndex(finder, index, driver);
    }

    /**
     * Return the Nth {@link WebElement} matching the specified
     * {@link ComponentFinder} or supported com.vaadin.ui.* class, relative to a
     * user-specified {@link SearchContext}
     * 
     * @param finder
     *            Either a {@link ComponentFinder} class, or a com.vaadin.ui.*
     *            class supported by the automatic component->finder mapping.
     * @param index
     *            zero-based index of the desired component
     * @param context
     *            any instance of a class implementing {@link SearchContext}
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElementByIndex(Class<?> finder, int index,
            SearchContext context) {
        return find(finder, context).atIndex(index).done();
    }

    /**
     * Retrieve a {@link WebElement} by ID using the specified
     * {@link ComponentFinder} class, relative to the document root.
     * 
     * @param id
     *            unique identifier of the element to find
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElementById(String id) {
        return driver.findElement(By.id(id));
    }

    /**
     * Retrieve a {@link WebElement} with a specific caption of type specified
     * by a {@link ComponentFinder} class, relative to the document root.
     * 
     * @param finder
     *            Either a {@link ComponentFinder} class, or a com.vaadin.ui.*
     *            class supported by the automatic component->finder mapping.
     * @param caption
     *            The caption string of the element
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElementByCaption(Class<?> finder, String caption) {
        return getElementByCaption(finder, caption, driver);
    }

    /**
     * Retrieve a {@link WebElement} with a specific caption of type specified
     * by a {@link ComponentFinder} class, starting at a specific context.
     * 
     * @param finder
     *            Either a {@link ComponentFinder} class, or a com.vaadin.ui.*
     *            class supported by the automatic component->finder mapping.
     * @param caption
     *            The caption string of the element
     * @param context
     *            any instance of a class implementing {@link SearchContext}
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElementByCaption(Class<?> finder, String caption,
            SearchContext context) {
        return find(finder, context).withCaption(caption).done();
    }

    /**
     * Retrieve a {@link WebElement} by a path using the Vaadin selector syntax.
     * This feature is provided for advanced use.
     * 
     * @param vaadinPath
     *            a Vaadin object selector string
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElementByPath(String vaadinPath) {
        return driver.findElement(com.vaadin.testbench.By.vaadin(vaadinPath));
    }

    /**
     * Retrieve a {@link WebElement} by a path using the Vaadin selector syntax,
     * starting at a specified SearchContext. This feature is provided for
     * advanced use.
     * 
     * @param vaadinPath
     *            a Vaadin object selector string
     * @param context
     *            any instance of a class implementing {@link SearchContext}
     * @return a {@link WebElement} instance, or null if no matches were found
     */
    public WebElement getElementByPath(String vaadinPath, SearchContext context) {
        return context.findElement(com.vaadin.testbench.By.vaadin(vaadinPath));
    }

}
