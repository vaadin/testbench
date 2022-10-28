/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import org.openqa.selenium.WebDriver;

/**
 * Base class for tests using {@link WebDriver}. Provides wrapping for
 * {@link TestBenchDriverProxy}.
 */
public abstract class AbstractBrowserDriverTestBase
        extends AbstractBrowserTestBase {

    private WebDriver driver;

    /**
     * Returns the {@link WebDriver} instance or (if the previously provided
     * WebDriver instance was not already a {@link TestBenchDriverProxy}
     * instance) a {@link TestBenchDriverProxy} that wraps that driver.
     *
     * @return the active WebDriver instance
     */
    @Override
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Sets the active {@link WebDriver} that is used by this test case
     *
     * @param driver
     *            The WebDriver instance to set.
     */
    public void setDriver(WebDriver driver) {
        if (driver != null && !(driver instanceof TestBenchDriverProxy)) {
            driver = TestBench.createDriver(driver);
        }
        this.driver = driver;
    }
}
