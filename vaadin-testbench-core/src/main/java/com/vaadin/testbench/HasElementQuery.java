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

public interface HasElementQuery extends HasSearchContext {

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. If this function gets called through an element,
     * it uses the element as its search context. Otherwise the search context
     * is the driver.
     *
     * @param clazz the type of element to find, with the tag name defined using
     *              <code>@Element</code> on the class
     * @return an appropriate {@link ElementQuery} instance
     */
    default <T extends TestBenchElement> ElementQuery<T> $(
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
     * @param tagName the tag name of the element to find
     * @return an appropriate {@link ElementQuery} instance
     */
    default ElementQuery<TestBenchElement> $(String tagName) {
        return new ElementQuery<>(TestBenchElement.class, tagName)
                .context(getContext());
    }
}
