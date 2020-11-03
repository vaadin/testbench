/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import java.util.List;

import org.openqa.selenium.WebElement;

public interface HasPropertySettersGetters extends WebElement {
    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, String value);

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, Boolean value);

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, Double value);

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name
     *            the name of the property
     * @param value
     *            the value to set
     */
    public void setProperty(String name, Integer value);

    /**
     * Gets a JavaScript property of the given element as a string.
     *
     * @param propertyNames
     *            the name of on or more properties, forming a property chain of
     *            type <code>property1.property2.property3</code>
     */
    public String getPropertyString(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as a boolean.
     *
     * @param propertyNames
     *            the name of on or more properties, forming a property chain of
     *            type <code>property1.property2.property3</code>
     */
    public Boolean getPropertyBoolean(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as a DOM element.
     *
     * @param propertyNames
     *            the name of on or more properties, forming a property chain of
     *            type <code>property1.property2.property3</code>
     */
    public TestBenchElement getPropertyElement(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as a list of DOM
     * elements.
     *
     * @param propertyNames
     *            the name of on or more properties, forming a property chain of
     *            type <code>property1.property2.property3</code>
     */
    public List<TestBenchElement> getPropertyElements(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as a double.
     *
     * @param propertyNames
     *            the name of on or more properties, forming a property chain of
     *            type <code>property1.property2.property3</code>
     */
    public Double getPropertyDouble(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as an integer.
     *
     * @param propertyNames
     *            the name of on or more properties, forming a property chain of
     *            type <code>property1.property2.property3</code>
     */
    public Integer getPropertyInteger(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element.
     * <p>
     * The return value needs to be cast manually to the correct type.
     *
     * @param propertyNames
     *            the name of on or more properties, forming a property chain of
     *            type <code>property1.property2.property3</code>
     */
    public Object getProperty(String... propertyNames);

}
