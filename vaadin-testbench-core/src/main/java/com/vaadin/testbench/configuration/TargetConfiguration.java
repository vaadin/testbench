package com.vaadin.testbench.configuration;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.testbench.addons.webdriver.BrowserType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;

import java.util.List;

public interface TargetConfiguration {

    List<Target> getBrowserTargets();

    static Target localChrome(String driverPath, boolean headless) {
        return new LocalTarget(
                new DesiredCapabilities(BrowserType.CHROME.browserName(), null, null),
                new ChromeOptions().setHeadless(headless),
                driverPath);
    }

    static Target localFirefox(String driverPath, boolean headless) {
        return new LocalTarget(
                new DesiredCapabilities(BrowserType.FIREFOX.browserName(), null, null),
                new FirefoxOptions().setHeadless(headless),
                driverPath);
    }

    static Target localSafari() {
        return new LocalTarget(
                new DesiredCapabilities(BrowserType.SAFARI.browserName(), null, null),
                new SafariOptions(), "/usr/bin/safaridriver");
    }

    static Target saucelabs(BrowserType browserType) {
        return saucelabs(browserType, null, null);
    }

    static Target saucelabs(BrowserType browserType, String version, Platform platform) {
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(browserType.browserName());
        capabilities.setVersion(version);
        capabilities.setPlatform(platform);
        return new SaucelabsTarget(capabilities);
    }
}
