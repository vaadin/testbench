/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
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
        desiredCapabilities.setCapability(CapabilityType.VERSION, version);
        desiredCapabilities.setCapability(PLATFORM, platform);

        return new DesiredCapabilities(desiredCapabilities);
    }
}
