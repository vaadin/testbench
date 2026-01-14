/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.Browser;

public class ExtensionWithBrowserConfigurationTest implements DriverSupplier {

    private static String oldBrowsers;

    @BeforeAll
    public static void setParameters() {
        oldBrowsers = Parameters.getGridBrowsersString();
        Parameters.setGridBrowsers("firefox,safari-9");
    }

    @Override
    public WebDriver createDriver() {
        return Mockito.mock(WebDriver.class);
    }

    @AfterAll
    public static void unsetParameters() {
        Parameters.setGridBrowsers(oldBrowsers);
    }

    @BrowserTest
    public void withBrowserConfigurationInClass(
            BrowserTestInfo browserTestInfo) {
        DesiredCapabilities caps = Browser.FIREFOX.getDesiredCapabilities();
        Assertions.assertEquals(caps.getBrowserName(),
                browserTestInfo.capabilities().getBrowserName());
        Assertions.assertEquals(caps.getBrowserVersion(),
                browserTestInfo.capabilities().getBrowserVersion());
        Assertions.assertEquals(caps.getPlatformName(),
                browserTestInfo.capabilities().getPlatformName());
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowserConfiguration() {
        return Arrays.asList(Browser.FIREFOX.getDesiredCapabilities());
    }

}
