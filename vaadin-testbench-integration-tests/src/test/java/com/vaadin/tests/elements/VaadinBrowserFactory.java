/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;

/**
 * Specifies default browser configuration for {@link PrivateTB5Configuration}
 * tests.
 */
public class VaadinBrowserFactory extends DefaultBrowserFactory {

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {
        DesiredCapabilities desiredCapabilities = super.create(browser,
                version, platform);

        if (platform == Platform.ANY) {
            desiredCapabilities
                    .setPlatform(Platform.LINUX);
        }

        return desiredCapabilities;
    }
}
