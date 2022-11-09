/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
