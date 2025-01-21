/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.parallel.Browser;

/**
 * Record that is automatically resolved by {@link BrowserExtension} providing
 * most important information on currently run test.
 * @param driver reference to {@link WebDriver}
 * @param capabilities immutable list of capabilities
 * @param hubHostname hostname of the hub
 * @param runLocallyBrowser {@link Browser} used for local execution
 * @param runLocallyBrowserVersion version of {@link Browser} used for local execution
 */
public record BrowserTestInfo(WebDriver driver, Capabilities capabilities,
                              String hubHostname, Browser runLocallyBrowser,
                              String runLocallyBrowserVersion) {

}
