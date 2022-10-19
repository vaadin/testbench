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

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.HasDriver;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchTest;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.Browser;

public class ExtensionWithBrowserConfigurationTest implements HasDriver {

    private static String oldBrowsers;

    @BeforeAll
    public static void setParameters() {
        oldBrowsers = Parameters.getGridBrowsersString();
        Parameters.setGridBrowsers("firefox,safari-9");
    }

    @Override
    public WebDriver getDriver() {
        return Mockito.mock(WebDriver.class);
    }

    @AfterAll
    public static void unsetParameters() {
        Parameters.setGridBrowsers(oldBrowsers);
    }

    @TestBenchTest
    public void withBrowserConfigurationInClass(Capabilities capabilities) {
        DesiredCapabilities caps = Browser.FIREFOX.getDesiredCapabilities();
        Assertions.assertEquals(caps.getBrowserName(),
                capabilities.getBrowserName());
        Assertions.assertEquals(caps.getBrowserVersion(),
                capabilities.getBrowserVersion());
        Assertions.assertEquals(caps.getPlatformName(),
                capabilities.getPlatformName());
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return Arrays.asList(Browser.FIREFOX.getDesiredCapabilities());
    }

}
