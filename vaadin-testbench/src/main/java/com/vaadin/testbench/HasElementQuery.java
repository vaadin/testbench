package com.vaadin.testbench;

public interface HasElementQuery {

    /**
     * Prepare a {@link ElementQuery} instance to use for locating components on
     * the client. The returned object can be manipulated to uniquely identify
     * the sought-after object. If this function gets called through an element,
     * it uses the element as its search context. Otherwise the search context
     * is the driver.
     *
     * @return an appropriate {@link ElementQuery} instance
     */
    public <T extends TestBenchElement> ElementQuery<T> $(Class<T> clazz);

}
