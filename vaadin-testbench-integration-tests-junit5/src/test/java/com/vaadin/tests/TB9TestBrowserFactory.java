/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import com.vaadin.testbench.Platform;
import com.vaadin.testbench.Capabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

/**
 * Specifies default browser configuration for {@link AbstractTB9Test} tests.
 */
public class TB9TestBrowserFactory extends DefaultBrowserFactory {

    @Override
    public Capabilities create(Browser browser, String version,
            Platform platform) {
        Capabilities desiredCapabilities = super.create(browser, version,
                platform);

        SauceLabsIntegration.setSauceLabsOption(desiredCapabilities,
                "screenResolution", "1600x1200");
        return desiredCapabilities;
    }
}
