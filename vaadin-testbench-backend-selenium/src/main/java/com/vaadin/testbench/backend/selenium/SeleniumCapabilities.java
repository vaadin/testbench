/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.selenium;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.Platform;
import com.vaadin.testbench.parallel.BrowserUtil;

/**
 * Our {@link Capabilities} to and from Selenium's, platforms included.
 */
public final class SeleniumCapabilities {

    private SeleniumCapabilities() {
    }

    /**
     * Selenium's capabilities for ours: the browser's own options class with
     * its defaults, as {@code DefaultBrowserFactory} built them, and ours on
     * top.
     */
    public static DesiredCapabilities toSelenium(Capabilities capabilities) {
        MutableCapabilities selenium;
        if (BrowserUtil.isChrome(capabilities)) {
            selenium = new ChromeOptions();
        } else if (BrowserUtil.isFirefox(capabilities)) {
            selenium = new FirefoxOptions();
        } else if (BrowserUtil.isEdge(capabilities)) {
            selenium = new EdgeOptions();
        } else if (BrowserUtil.isSafari(capabilities)) {
            selenium = new SafariOptions();
        } else {
            selenium = new MutableCapabilities();
        }
        capabilities.asMap()
                .forEach((name, value) -> selenium.setCapability(name,
                        value instanceof Platform platform
                                ? org.openqa.selenium.Platform
                                        .valueOf(platform.name())
                                : value));
        return new DesiredCapabilities(selenium);
    }

    /** Ours for Selenium's capabilities. */
    public static Capabilities fromSelenium(
            org.openqa.selenium.Capabilities capabilities) {
        Map<String, Object> ours = new LinkedHashMap<>();
        capabilities.asMap()
                .forEach((name, value) -> ours.put(name,
                        value instanceof org.openqa.selenium.Platform platform
                                ? Platform.valueOf(platform.name())
                                : value));
        return new Capabilities(ours);
    }
}
