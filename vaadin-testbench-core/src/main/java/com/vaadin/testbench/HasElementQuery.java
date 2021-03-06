/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
