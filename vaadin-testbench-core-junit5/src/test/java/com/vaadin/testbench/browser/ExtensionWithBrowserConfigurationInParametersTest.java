/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.parallel.Browser;

public class ExtensionWithBrowserConfigurationInParametersTest
        implements DriverSupplier {

    private static String oldBrowsers;

    @BeforeAll
    public static void setParameters() {
        oldBrowsers = Parameters.getGridBrowsersString();
        Parameters.setGridBrowsers("firefox,safari-9");
    }

    @AfterAll
    public static void unsetParameters() {
        Parameters.setGridBrowsers(oldBrowsers);
    }

    @Override
    public WebDriver createDriver() {
        return Mockito.mock(WebDriver.class);
    }

    @BrowserTest
    public void withBrowsersConfigurationInParameters(TestInfo testInfo,
            BrowserTestInfo browserTestInfo) {
        DesiredCapabilities caps1 = Browser.FIREFOX.getDesiredCapabilities();
        DesiredCapabilities caps2 = Browser.SAFARI.getDesiredCapabilities();
        caps2.setVersion("9");
        if (testInfo.getDisplayName().contains("Firefox")) {
            Assertions.assertEquals(caps1.getBrowserName(),
                    browserTestInfo.capabilities().getBrowserName());
            Assertions.assertEquals(caps1.getBrowserVersion(),
                    browserTestInfo.capabilities().getBrowserVersion());
            Assertions.assertEquals(caps1.getPlatformName(),
                    browserTestInfo.capabilities().getPlatformName());
        } else {
            Assertions.assertEquals(caps2.getBrowserName(),
                    browserTestInfo.capabilities().getBrowserName());
            Assertions.assertEquals(caps2.getBrowserVersion(),
                    browserTestInfo.capabilities().getBrowserVersion());
            Assertions.assertEquals(caps2.getPlatformName(),
                    browserTestInfo.capabilities().getPlatformName());
        }
    }

}
