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
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.HasCustomDriver;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchTest;

public class ExtensionWithoutBrowserConfigurationTest
        implements HasCustomDriver {

    private static String oldBrowsers;

    @BeforeAll
    public static void setParameters() {
        oldBrowsers = Parameters.getGridBrowsersString();
        Parameters.setGridBrowsers("");
    }

    @Override
    public WebDriver getCustomDriver() {
        return Mockito.mock(WebDriver.class);
    }

    @AfterAll
    public static void unsetParameters() {
        Parameters.setGridBrowsers(oldBrowsers);
    }

    @TestBenchTest
    public void withoutBrowsersConfiguration(Capabilities capabilities) {
        DesiredCapabilities caps = CapabilitiesUtil.getDefaultCapabilities()
                .get(0);
        Assertions.assertEquals(caps.getBrowserName(),
                capabilities.getBrowserName());
        Assertions.assertEquals(caps.getBrowserVersion(),
                capabilities.getBrowserVersion());
        Assertions.assertEquals(caps.getPlatformName(),
                capabilities.getPlatformName());
    }

}
