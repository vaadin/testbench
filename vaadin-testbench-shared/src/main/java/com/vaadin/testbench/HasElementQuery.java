/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

public interface HasElementQuery {

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. Called on an element, it searches inside the
     * element; called on a test, it searches the page.
     *
     * @param <T>
     *            the type of the queried {@link TestBenchElement}
     * @param clazz
     *            the type of element to find, with the tag name defined using
     *            <code>@Element</code> on the class
     * @return an appropriate {@link ElementQuery} instance
     */
    <T extends TestBenchElement> ElementQuery<T> $(Class<T> clazz);

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. Called on an element, it searches inside the
     * element; called on a test, it searches the page.
     *
     * @param tagName
     *            the tag name of the element to find
     * @return an appropriate {@link ElementQuery} instance
     */
    ElementQuery<TestBenchElement> $(String tagName);

}
