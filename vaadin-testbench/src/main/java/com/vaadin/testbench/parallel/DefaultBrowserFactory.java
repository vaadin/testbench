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

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

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
        DesiredCapabilities desiredCapabilities;

        switch (browser) {
        case CHROME:
            desiredCapabilities = DesiredCapabilities.chrome();
            desiredCapabilities.setVersion(version);
            desiredCapabilities.setPlatform(platform);
            break;
        case PHANTOMJS:
            desiredCapabilities = DesiredCapabilities.phantomjs();
            desiredCapabilities.setVersion(version);
            desiredCapabilities.setPlatform(platform);
            break;
        case SAFARI:
            desiredCapabilities = DesiredCapabilities.safari();
            desiredCapabilities.setVersion(version);
            desiredCapabilities.setPlatform(platform);
            break;
        case IE11:
            desiredCapabilities = DesiredCapabilities.internetExplorer();
            desiredCapabilities.setVersion("11");
            desiredCapabilities.setPlatform(platform);
            // There are 2 capabilities ie.ensureCleanSession and
            // ensureCleanSession in Selenium
            // IE 11 uses ie.ensureCleanSession
            desiredCapabilities.setCapability("ie.ensureCleanSession", true);
            break;
        case EDGE:
            desiredCapabilities = DesiredCapabilities.edge();
            desiredCapabilities.setPlatform(platform);
            break;
        case FIREFOX:
        default:
            desiredCapabilities = DesiredCapabilities.firefox();
            desiredCapabilities.setVersion(version);
            desiredCapabilities.setPlatform(platform);
        }

        return desiredCapabilities;
    }
}
