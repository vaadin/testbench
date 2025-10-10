/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Platform;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;

/**
 * Specifies default browser configuration for {@link PrivateTB3Configuration}
 * tests.
 */
public class VaadinBrowserFactory extends DefaultBrowserFactory {

    private static Map<Browser, String> defaultBrowserVersion = new HashMap<Browser, String>();
    static {
        defaultBrowserVersion.put(Browser.CHROME, "141");
        defaultBrowserVersion.put(Browser.PHANTOMJS, "1");
        defaultBrowserVersion.put(Browser.SAFARI, "7");
        defaultBrowserVersion.put(Browser.IE11, "11");
        defaultBrowserVersion.put(Browser.FIREFOX, "45");
    }

    private static Map<Browser, Platform> defaultBrowserPlatform = new HashMap<Browser, Platform>();
    static {
        defaultBrowserPlatform.put(Browser.CHROME, Platform.ANY);
        defaultBrowserPlatform.put(Browser.PHANTOMJS, Platform.LINUX);
        defaultBrowserPlatform.put(Browser.SAFARI, Platform.MAC);
        defaultBrowserPlatform.put(Browser.IE11, Platform.WINDOWS);
        defaultBrowserPlatform.put(Browser.FIREFOX, Platform.WINDOWS);
    }

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {
        final String PHANTOMJS_PATH_PROPERTY = "phantomjs.binary.path";
        final String PHANTOMJS_PATH_VALUE = "/usr/bin/phantomjs2";
        if (browser == Browser.PHANTOMJS) {
            DesiredCapabilities phantom2 = super.create(browser, "2",
                    Platform.LINUX);
            // Hack for the test cluster
            phantom2.setCapability(PHANTOMJS_PATH_PROPERTY,
                PHANTOMJS_PATH_VALUE);
            return phantom2;
        }

        DesiredCapabilities desiredCapabilities = super.create(browser,
                version, platform);

        if (platform == Platform.ANY
                && defaultBrowserPlatform.containsKey(browser)) {
            desiredCapabilities
                    .setPlatform(defaultBrowserPlatform.get(browser));
        }

        if ("".equals(version) && defaultBrowserVersion.containsKey(browser)) {
            desiredCapabilities.setVersion(defaultBrowserVersion.get(browser));
        }
        if(browser.equals(Browser.FIREFOX)) {
            desiredCapabilities.setCapability(FirefoxDriver.MARIONETTE, false);
        }
        return desiredCapabilities;
    }
}
