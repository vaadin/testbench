/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.laboratory.views;

import org.junit.jupiter.api.BeforeEach;

import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.loadtest.LoadTestItHelper;

/**
 * Base class for all integration tests, allowing us to change the applicable
 * driver, test URL or other configurations in one place.
 * <p>
 * For k6 recording, set the system property
 * {@code k6.proxy.host=localhost:6000}. This will configure the browser to
 * route traffic through a recording proxy.
 * <p>
 * TODO: The k6 proxy configuration code in this class is a temporary solution.
 * In the final implementation, this boilerplate will not be needed - either:
 * <ul>
 * <li>AI coding assistants (like Claude Code) will learn to add it
 * automatically, or</li>
 * <li>This functionality will be built directly into TestBench itself</li>
 * </ul>
 * The challenge is that BrowserTestBase's @BeforeEach runs after JUnit
 * extension callbacks, so we cannot configure the proxy driver in an extension.
 * The proxy configuration must happen in a @BeforeEach method that runs after
 * BrowserTestBase sets its driver but before navigation occurs.
 */
public abstract class AbstractIT extends BrowserTestBase {

    @BeforeEach
    public void open() {
        setDriver(LoadTestItHelper.openWithProxy(getDriver(), getViewUrl()));
    }

    private String getViewUrl() {
        return LoadTestItHelper.getRootURL() + "/" + getViewName();
    }

    abstract public String getViewName();
}
