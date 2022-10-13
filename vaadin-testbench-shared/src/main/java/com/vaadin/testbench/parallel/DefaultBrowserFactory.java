/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parallel;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;

import com.vaadin.testbench.annotations.BrowserFactory;

/**
 * <p>
 * Default {@link TestBenchBrowserFactory} used to generate
 * {@link DesiredCapabilities} through {@link BrowserFactory}
 * </p>
 */
public class DefaultBrowserFactory implements TestBenchBrowserFactory {

    @Override
    public DesiredCapabilities create(Browser browser) {
        return create(browser, "", Platform.ANY);
    }

    @Override
    public DesiredCapabilities create(Browser browser, String version) {
        return create(browser, version, Platform.ANY);
    }

    @Override
    public DesiredCapabilities create(Browser browser, String version,
            Platform platform) {
        MutableCapabilities desiredCapabilities;

        switch (browser) {
        case CHROME:
            desiredCapabilities = new ChromeOptions();
            break;
        case SAFARI:
            desiredCapabilities = new SafariOptions();
            break;
        case EDGE:
            desiredCapabilities = new EdgeOptions();
            break;
        case FIREFOX:
        default:
            desiredCapabilities = new FirefoxOptions();
        }
        if (version != null && !version.isEmpty()) {
            desiredCapabilities.setCapability(CapabilityType.BROWSER_VERSION,
                    version);
        }
        if (platform != null) {
            desiredCapabilities.setCapability(CapabilityType.PLATFORM_NAME,
                    platform);
        }

        return new DesiredCapabilities(desiredCapabilities);
    }
}
