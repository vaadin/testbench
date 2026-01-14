/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

/**
 * Specifies default browser configuration for {@link AbstractTB6Test} tests.
 */
public class TB6TestBrowserFactory extends DefaultBrowserFactory {

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {
        if (browser != Browser.SAFARI) {
            platform = Platform.WIN10;
        }
        DesiredCapabilities desiredCapabilities = super.create(browser, version,
                platform);

        SauceLabsIntegration.setSauceLabsOption(desiredCapabilities,
                "screenResolution", "1600x1200");
        return desiredCapabilities;
    }
}
