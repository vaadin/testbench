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
 * A test implementing this builds its own browser, with a backend's library;
 * the test then runs on that backend:
 *
 * <pre>{@code
 * public WebDriver createDriver() {
 *     return new ChromeDriver(options);
 * }
 * }</pre>
 *
 * The return type is {@code Object} so this interface names no library; an
 * implementation narrows it.
 *
 * @since 9.0
 */
public interface DriverSupplier {

    /**
     * @return a Selenium {@code WebDriver}, or a Playwright {@code Browser},
     *         {@code BrowserContext} or {@code Page}; {@code null} to let
     *         TestBench start the browser
     */
    Object createDriver();

}
