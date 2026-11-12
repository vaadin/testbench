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
 * Base class for tests using the browser driver. Provides wrapping for
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
     * Sets a driver this test built with a backend's library, as TestBench's
     * {@link BrowserDriver}, on that backend:
     *
     * <pre>{@code
     * setDriver(new ChromeDriver());
     * }</pre>
     *
     * @param driver
     *            a Selenium {@code WebDriver}, a Playwright {@code Browser},
     *            {@code BrowserContext} or {@code Page}, or a
     *            {@link BrowserDriver}
     * @see TestBench#createDriver(Object)
     */
    public void setDriver(Object driver) {
        setDriver(driver == null || driver instanceof BrowserDriver
                ? (BrowserDriver) driver
                : TestBench.createDriver(driver));
    }
}
