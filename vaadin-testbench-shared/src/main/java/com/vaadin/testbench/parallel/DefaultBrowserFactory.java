/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.Platform;
import com.vaadin.testbench.annotations.BrowserFactory;

/**
 * <p>
 * Default {@link TestBenchBrowserFactory} used to generate {@link Capabilities}
 * through {@link BrowserFactory}
 * </p>
 * The browser names are the W3C ones Selenium's options classes set; the
 * Selenium backend adds those classes' own defaults when it starts the browser.
 */
public class DefaultBrowserFactory implements TestBenchBrowserFactory {

    @Override
    public Capabilities create(Browser browser) {
        return create(browser, "", Platform.ANY);
    }

    @Override
    public Capabilities create(Browser browser, String version) {
        return create(browser, version, Platform.ANY);
    }

    @Override
    public Capabilities create(Browser browser, String version,
            Platform platform) {
        Capabilities capabilities = new Capabilities();
        capabilities.setCapability(Capabilities.BROWSER_NAME, switch (browser) {
        case CHROME -> "chrome";
        case SAFARI -> "safari";
        case EDGE -> "MicrosoftEdge";
        case FIREFOX -> "firefox";
        });
        if (version != null && !version.isEmpty()) {
            capabilities.setCapability(Capabilities.BROWSER_VERSION, version);
        }
        if (platform != null) {
            capabilities.setCapability(Capabilities.PLATFORM_NAME, platform);
        }
        return capabilities;
    }
}
