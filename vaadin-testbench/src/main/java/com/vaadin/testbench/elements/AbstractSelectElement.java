package com.vaadin.testbench.elements;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.testbench.TestBenchElement;

/**
 * Common API for all different AbstractSelectElements. This API is implemented
 * in all appropriate subclasses. Many of the subclasses expand this API with
 * their own tools and functions.
 */
@ServerClass("com.vaadin.ui.AbstractSelect")
public class AbstractSelectElement extends AbstractFieldElement {
    /**
     * Deselects each and all options
     */
    public void deselectAll() {
    }

    /**
     * Deselects option with given index.
     * 
     * @param index
     *            Same index as wanted option is in getOptions() List
     */
    public void deselectByIndex(int index) {
    }

    /**
     * Deselect option with given value
     * 
     * @param value
     *            Value of option to deselect
     */
    public void deselectByValue(String value) {
    }

    /**
     * Deselect option with given visible text
     * 
     * @param text
     *            Text of option to deselect
     */
    public void deselectByVisibleText(String text) {
    }

    /**
     * Function to get a list of all currently selected options. Note that this
     * list is subject to change when selecting or deselecting options. Ask for
     * a new list after each change to avoid stale element reference exceptions.
     * 
     * @return
     */
    public List<TestBenchElement> getAllSelectedOptions() {
        return new ArrayList<TestBenchElement>();
    }

    /**
     * Return the first selected option in a select element. Note that the
     * element is subject to change when selecting or deselecting options. Ask
     * for a new element after each change to avoid stale element reference
     * exceptions
     * 
     * @return TestBenchElement of first selected option
     */
    public TestBenchElement getFirstSelectedOption() {
        return null;
    }

    /**
     * Function to get the list of all options. Note that this list is subject
     * to constant changes when selecting or deselecting options. Ask for a new
     * list after every change to avoid stale element reference exceptions.
     * 
     * @return TestBenchElement list of all options
     */
    public List<TestBenchElement> getOptions() {
        return new ArrayList<TestBenchElement>();
    }

    /**
     * Finds out if select element allows selecting multiple elements
     * 
     * @return true if can select multiple elements
     */
    public boolean isMultiple() {
        return false;
    }

    /**
     * Selects option with given index.
     * 
     * @param index
     *            Same index as wanted option is in getOptions() List
     */
    public void selectByIndex(int index) {
    }

    /**
     * Select option with given value
     * 
     * @param value
     *            Value of option to select
     */
    public void selectByValue(String value) {
    }

    /**
     * Select option with given visible text
     * 
     * @param text
     *            Text of option to select
     */
    public void selectByVisibleText(String text) {
    }
}
