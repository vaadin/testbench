package com.vaadin.testbench;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import org.openqa.selenium.WebElement;

import java.util.List;

public interface HasPropertySettersGetters extends WebElement {
    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name  the name of the property
     * @param value the value to set
     */
    void setProperty(String name, String value);

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name  the name of the property
     * @param value the value to set
     */
    void setProperty(String name, Boolean value);

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name  the name of the property
     * @param value the value to set
     */
    void setProperty(String name, Double value);

    /**
     * Sets a JavaScript property of the given element.
     *
     * @param name  the name of the property
     * @param value the value to set
     */
    void setProperty(String name, Integer value);

    /**
     * Gets a JavaScript property of the given element as a string.
     *
     * @param propertyNames the name of on or more properties, forming a property chain of
     *                      type <code>property1.property2.property3</code>
     */
    String getPropertyString(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as a boolean.
     *
     * @param propertyNames the name of on or more properties, forming a property chain of
     *                      type <code>property1.property2.property3</code>
     */
    Boolean getPropertyBoolean(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as a DOM element.
     *
     * @param propertyNames the name of on or more properties, forming a property chain of
     *                      type <code>property1.property2.property3</code>
     */
    TestBenchElement getPropertyElement(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as a list of DOM
     * elements.
     *
     * @param propertyNames the name of on or more properties, forming a property chain of
     *                      type <code>property1.property2.property3</code>
     */
    List<TestBenchElement> getPropertyElements(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as a double.
     *
     * @param propertyNames the name of on or more properties, forming a property chain of
     *                      type <code>property1.property2.property3</code>
     */
    Double getPropertyDouble(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element as an integer.
     *
     * @param propertyNames the name of on or more properties, forming a property chain of
     *                      type <code>property1.property2.property3</code>
     */
    Integer getPropertyInteger(String... propertyNames);

    /**
     * Gets a JavaScript property of the given element.
     * <p>
     * The return value needs to be cast manually to the correct type.
     *
     * @param propertyNames the name of on or more properties, forming a property chain of
     *                      type <code>property1.property2.property3</code>
     */
    Object getProperty(String... propertyNames);
}
