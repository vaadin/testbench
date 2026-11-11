/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend;

import org.openqa.selenium.WebDriver;

/**
 * Starts a browser on a backend other than Selenium, for the JUnit integration,
 * which finds it with {@link java.util.ServiceLoader}. Browser setup is still
 * Selenium-typed, so the browser comes back as a {@link WebDriver} that also
 * implements {@link HasBackend}; temporary, until setup no longer names
 * Selenium.
 */
public interface BrowserLauncher {

    /** As {@link com.vaadin.testbench.Parameters#getBackend()} names it. */
    String getBackendName();

    /** A new browser, isolated from any other this launcher started. */
    WebDriver launch();
}
