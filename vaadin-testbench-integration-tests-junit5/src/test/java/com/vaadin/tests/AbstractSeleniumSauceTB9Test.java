/**
 * Copyright (C) 2000-2025 Vaadin Ltd
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
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

/**
 * Example of how to use SeleniumJupiter together with TestBench 9+ features.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractSeleniumSauceTB9Test
        extends AbstractSeleniumTB9Test {

    @DriverUrl
    String url = SauceLabsIntegration.getHubUrl();

    @DriverCapabilities
    DesiredCapabilities capabilities = new DesiredCapabilities();
    {
        capabilities.merge(BrowserUtil.chrome());
        capabilities.setPlatform(Platform.WIN10);
        SauceLabsIntegration.setDesiredCapabilities(capabilities);
    }

    @BeforeEach
    public void setDriver(RemoteWebDriver driver) {
        super.setDriver(driver);
    }

}
