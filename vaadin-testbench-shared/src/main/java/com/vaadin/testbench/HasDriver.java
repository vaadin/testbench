/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

/** A test with a browser, such as every TestBench test base. */
public interface HasDriver {

    /** @return {@code null} before the browser is set up */
    BrowserDriver getDriver();
}
