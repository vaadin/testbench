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

import static org.openqa.selenium.remote.CapabilityType.PLATFORM;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
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
        case IE11:
            desiredCapabilities = new InternetExplorerOptions();
            // There are 2 capabilities ie.ensureCleanSession and
            // ensureCleanSession in Selenium
            // IE 11 uses ie.ensureCleanSession
            desiredCapabilities.setCapability("ie.ensureCleanSession", true);
            break;
        case EDGE:
            desiredCapabilities = new EdgeOptions();
            break;
        case FIREFOX:
        default:
            desiredCapabilities = new FirefoxOptions();
        }
        desiredCapabilities.setCapability(CapabilityType.BROWSER_VERSION,
                version);
        desiredCapabilities.setCapability(PLATFORM, platform);

        return new DesiredCapabilities(desiredCapabilities);
    }
}
