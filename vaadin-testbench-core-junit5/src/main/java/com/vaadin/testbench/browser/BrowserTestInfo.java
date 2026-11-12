/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import com.vaadin.testbench.BrowserDriver;
import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.parallel.Browser;

/**
 * Record that is automatically resolved by {@link BrowserExtension} providing
 * most important information on currently run test.
 *
 * @param driver
 *            the test's browser
 * @param capabilities
 *            the capabilities the browser was asked for, a copy
 * @param hubHostname
 *            hostname of the hub
 * @param runLocallyBrowser
 *            {@link Browser} used for local execution
 * @param runLocallyBrowserVersion
 *            version of {@link Browser} used for local execution
 * @since 9.0
 */
public record BrowserTestInfo(BrowserDriver driver, Capabilities capabilities,
        String hubHostname, Browser runLocallyBrowser,
        String runLocallyBrowserVersion) {

}
