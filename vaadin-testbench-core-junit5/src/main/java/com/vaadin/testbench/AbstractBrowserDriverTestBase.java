/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.openqa.selenium.WebDriver;

/**
 * Base class for tests using {@link WebDriver}. Provides wrapping for
 * {@link TestBenchDriverProxy}.
 *
 * @since 9.0
 */
public abstract class AbstractBrowserDriverTestBase
        extends AbstractBrowserTestBase {

    private BrowserDriver driver;

    @Override
    public BrowserDriver getDriver() {
        return driver;
    }

    public void setDriver(BrowserDriver driver) {
        this.driver = driver;
    }

    /**
     * Sets a Selenium driver this test has set up itself, as TestBench's
     * {@link BrowserDriver}: the test then runs on Selenium.
     */
    public void setDriver(WebDriver driver) {
        setDriver(driver == null ? null : TestBench.createDriver(driver));
    }
}
