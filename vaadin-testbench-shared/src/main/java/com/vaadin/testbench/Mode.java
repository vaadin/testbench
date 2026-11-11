/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

/**
 * How TestBench behaves where today's behaviour and a better test differ. One
 * switch for the whole run, read by {@link Parameters#getMode()}:
 *
 * <pre>
 * -Dcom.vaadin.testbench.Parameters.mode=modern
 * </pre>
 *
 * Choosing {@link #MODERN} says "I am migrating my tests now and will fix what
 * breaks"; each method whose behaviour differs says so in its own javadoc.
 */
public enum Mode {

    /** The default: behaves as TestBench always has, which mostly means as Selenium does. */
    COMPAT,

    /**
     * Follows the web standards and Playwright where that makes a better test:
     * {@link TestBenchElement#getText()} is the browser's {@code innerText},
     * and a query never silently picks one of several elements
     * ({@link ElementQuery#first()} throws).
     */
    MODERN
}
