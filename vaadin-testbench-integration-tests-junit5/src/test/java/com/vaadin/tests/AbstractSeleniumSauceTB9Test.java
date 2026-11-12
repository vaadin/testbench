/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import io.github.bonigarcia.seljup.DriverCapabilities;
import io.github.bonigarcia.seljup.DriverUrl;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.backend.selenium.SeleniumCapabilities;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

/**
 * Example of how to use SeleniumJupiter against a remote grid together with
 * TestBench 9+ features. The grid is either Sauce Labs (when configured) or a
 * Selenium hub selected via the {@code hubHostname} parameter (e.g. the
 * {@code selenium/standalone-chrome} container used in CI).
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractSeleniumSauceTB9Test
        extends AbstractSeleniumTB9Test {

    @DriverUrl
    String url = getHubUrl();

    @DriverCapabilities
    DesiredCapabilities capabilities = createCapabilities();

    @BeforeEach
    public void setDriver(RemoteWebDriver driver) {
        super.setDriver(driver);
    }

    private static String getHubUrl() {
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            return SauceLabsIntegration.getHubUrl();
        }
        return String.format("http://%s:%d/wd/hub", Parameters.getHubHostname(),
                Parameters.getHubPort());
    }

    // selenium-jupiter takes Selenium's capabilities; TestBench builds ours
    private static DesiredCapabilities createCapabilities() {
        com.vaadin.testbench.Capabilities capabilities = BrowserUtil.chrome();
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            capabilities.setCapability(
                    com.vaadin.testbench.Capabilities.PLATFORM_NAME,
                    com.vaadin.testbench.Platform.WIN10);
            SauceLabsIntegration.setDesiredCapabilities(capabilities);
        }
        return SeleniumCapabilities.toSelenium(capabilities);
    }

}
