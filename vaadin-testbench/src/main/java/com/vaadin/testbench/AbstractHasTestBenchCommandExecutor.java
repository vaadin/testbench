package com.vaadin.testbench;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.AbstractElement;

/**
 * Abstract class that provides useful Element finding functions. Extended in
 * {@link TestBenchElement} and {@link TestBenchTestCase}. These classes have
 * their own search contexts to manage scoping element searches where needed.
 */
public abstract class AbstractHasTestBenchCommandExecutor implements
        HasTestBenchCommandExecutor {

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. Type of element is supplied later. If this
     * function gets called through an element, it uses the element as its
     * search context. Otherwise the search context is the driver.
     * 
     * @return an appropriate {@link ElementQuery} instance
     */
    public <T extends AbstractElement> ElementQuery<T> $(Class<T> clazz) {
        return new ElementQuery<T>(clazz).context(getContext());
    }

    /**
     * Retrieve the first matching component starting from the document root.
     * 
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
     * @return List of found components wrapped in given Element class
     */
    public <T extends AbstractElement> List<T> findElements(Class<T> clazz) {
        return $(clazz).all();
    }

    /**
     * Retrieve a list of matching components with a specific caption.
     * 
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
     * @param caption
     *            The caption string of the component
     * @return List of found components wrapped in given Element class
     */
    public <T extends AbstractElement> List<T> findElementsByCaption(
            Class<T> clazz, String caption) {
        return $(clazz).caption(caption).all();
    }

    /**
     * Retrieve a list of matching components by a path using the Vaadin
     * selector syntax. This feature is provided for advanced use.
     * 
     * @param vaadinPath
     *            a Vaadin object selector string
     * @return List of {@link TestBenchElement} instances
     */
    public List<TestBenchElement> findElementsByPath(String vaadinPath) {
        return TestBenchElement.wrapElements(
                getContext().findElements(By.vaadin(vaadinPath)),
                getTestBenchCommandExecutor());
    }

    /**
     * Retrieve the first matching component.
     * 
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
     * @return a {@link WebElement} instance wrapped in given Element class
     */
    public <T extends AbstractElement> T findElement(Class<T> clazz) {
        return $(clazz).first();
    }

    /**
     * Retrieve the Nth matching Component.
     * 
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
     * @param index
     *            zero-based index of the desired component
     * @return a {@link WebElement} instance wrapped in given Element class
     */
    public <T extends AbstractElement> T findElementByIndex(Class<T> clazz,
            int index) {
        return $(clazz).index(index).first();
    }

    /**
     * Retrieve a component identified by id. id refers to Vaadin server side
     * component id that is set with .setId().
     * 
     * @param id
     *            id of the component to find
     * @return a {@link TestBenchElement} instance
     */
    public <T extends AbstractElement> T findElementById(Class<T> clazz,
            String id) {
        return $(clazz).id(id).first();
    }

    /**
     * Retrieve first matching component with a specific caption.
     * 
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
     * @param caption
     *            The caption string of the element
     * @return a {@link WebElement} instance wrapped in given Element class
     */
    public <T extends AbstractElement> T findElementByCaption(Class<T> clazz,
            String caption) {
        return $(clazz).caption(caption).first();
    }

    /**
     * Retrieve first matching component by a path using the Vaadin selector
     * syntax. This feature is provided for advanced use.
     * 
     * @param vaadinPath
     *            a Vaadin component selector string
     * @return a {@link TestBenchElement} instance
     */
    public TestBenchElement findElementByPath(String vaadinPath) {
        return TestBenchElement.wrapElement(
                By.vaadin(vaadinPath).findElement(getContext()),
                getTestBenchCommandExecutor());
    }

    /**
     * Returns true if a component can be found.
     * 
     * @param clazz
     *            AbstractElement subclass representing a Vaadin component
     * @return true if component of given class can be found
     */
    public boolean isElementPresent(Class<? extends AbstractElement> clazz) {
        return !$(clazz).all().isEmpty();
    }

    /**
     * Returns true if a component can be found with given By selector.
     * 
     * @param by
     *            the selector used to find element
     * @return true if the component can be found
     */
    public boolean isElementPresent(org.openqa.selenium.By by) {
        return !getContext().findElements(by).isEmpty();
    }

}
