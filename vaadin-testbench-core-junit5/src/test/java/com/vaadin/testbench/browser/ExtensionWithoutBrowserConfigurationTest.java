/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.browser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.Parameters;

public class ExtensionWithoutBrowserConfigurationTest
        implements DriverSupplier {

    private static String oldBrowsers;

    @BeforeAll
    public static void setParameters() {
        oldBrowsers = Parameters.getGridBrowsersString();
        Parameters.setGridBrowsers("");
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
    public void withoutBrowsersConfiguration(BrowserTestInfo browserTestInfo) {
        DesiredCapabilities caps = CapabilitiesUtil.getDefaultCapabilities()
                .get(0);
        Assertions.assertEquals(caps.getBrowserName(),
                browserTestInfo.capabilities().getBrowserName());
        Assertions.assertEquals(caps.getBrowserVersion(),
                browserTestInfo.capabilities().getBrowserVersion());
        Assertions.assertEquals(caps.getPlatformName(),
                browserTestInfo.capabilities().getPlatformName());
    }

}
