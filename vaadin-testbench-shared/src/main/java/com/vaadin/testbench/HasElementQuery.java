/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

public interface HasElementQuery extends HasSearchContext {

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. If this function gets called through an element,
     * it uses the element as its search context. Otherwise the search context
     * is the driver.
     *
     * @param <T>
     *            the type of the queried {@link TestBenchElement}
     * @param clazz
     *            the type of element to find, with the tag name defined using
     *            <code>@Element</code> on the class
     * @return an appropriate {@link ElementQuery} instance
     */
    public default <T extends TestBenchElement> ElementQuery<T> $(
            Class<T> clazz) {
        return new ElementQuery<>(clazz).context(getContext());
    }

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. If this function gets called through an element,
     * it uses the element as its search context. Otherwise the search context
     * is the driver.
     *
     * @param tagName
     *            the tag name of the element to find
     * @return an appropriate {@link ElementQuery} instance
     */
    public default ElementQuery<TestBenchElement> $(String tagName) {
        return new ElementQuery<>(TestBenchElement.class, tagName)
                .context(getContext());
    }

}
