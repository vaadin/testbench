/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

/**
 * Specifies default browser configuration for {@link AbstractTB6Test}
 * tests.
 */
public class TB6TestBrowserFactory extends DefaultBrowserFactory {

    private static Map<Browser, String> defaultBrowserVersion = new HashMap<>();
    static {
        defaultBrowserVersion.put(Browser.CHROME, "");
        defaultBrowserVersion.put(Browser.SAFARI, "11");
        defaultBrowserVersion.put(Browser.IE11, "11");
        defaultBrowserVersion.put(Browser.FIREFOX, "");
    }

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {

        if (browser != Browser.SAFARI) {
            platform = Platform.WIN10;
        }
 
        DesiredCapabilities desiredCapabilities = super.create(browser,
                version, platform);

        if ("".equals(version) && defaultBrowserVersion.containsKey(browser)) {
            desiredCapabilities.setVersion(defaultBrowserVersion.get(browser));
        }
        if(browser.equals(Browser.FIREFOX)) {
            desiredCapabilities.setCapability(FirefoxDriver.MARIONETTE, false);
        }
        SauceLabsIntegration.setSauceLabsOption(desiredCapabilities,
                "screenResolution", "1600x1200");
        return desiredCapabilities;
    }
}
