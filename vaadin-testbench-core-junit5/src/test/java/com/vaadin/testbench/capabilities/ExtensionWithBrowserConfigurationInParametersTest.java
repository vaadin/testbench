/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.capabilities;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.HasDriver;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchTest;
import com.vaadin.testbench.parallel.Browser;

public class ExtensionWithBrowserConfigurationInParametersTest
        implements HasDriver {

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
    public WebDriver getDriver() {
        return Mockito.mock(WebDriver.class);
    }

    @TestBenchTest
    public void withBrowsersConfigurationInParameters(TestInfo testInfo,
            Capabilities capabilities) {
        DesiredCapabilities caps1 = Browser.FIREFOX.getDesiredCapabilities();
        DesiredCapabilities caps2 = Browser.SAFARI.getDesiredCapabilities();
        caps2.setVersion("9");
        if (testInfo.getDisplayName().contains("Firefox")) {
            Assertions.assertEquals(caps1.getBrowserName(),
                    capabilities.getBrowserName());
            Assertions.assertEquals(caps1.getBrowserVersion(),
                    capabilities.getBrowserVersion());
            Assertions.assertEquals(caps1.getPlatformName(),
                    capabilities.getPlatformName());
        } else {
            Assertions.assertEquals(caps2.getBrowserName(),
                    capabilities.getBrowserName());
            Assertions.assertEquals(caps2.getBrowserVersion(),
                    capabilities.getBrowserVersion());
            Assertions.assertEquals(caps2.getPlatformName(),
                    capabilities.getPlatformName());
        }
    }

}
