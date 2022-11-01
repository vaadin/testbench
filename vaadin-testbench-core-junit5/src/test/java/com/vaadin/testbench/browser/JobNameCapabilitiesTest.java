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

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

public class JobNameCapabilitiesTest implements DriverSupplier {

    @BrowserTest
    public void tbMethodNameInCapabilities(TestInfo testInfo,
            Capabilities capabilities) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(
                capabilities);
        Assertions.assertEquals("bar", SauceLabsIntegration
                .getSauceLabsOption(desiredCapabilities, "foo"));
        Assertions.assertEquals(testInfo.getDisplayName(),
                SauceLabsIntegration.getSauceLabsOption(desiredCapabilities,
                        SauceLabsIntegration.CapabilityType.NAME));
    }

    @Override
    public WebDriver createDriver() {
        return Mockito.mock(WebDriver.class);
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowsers() {
        List<DesiredCapabilities> caps = Arrays
                .asList(Browser.CHROME.getDesiredCapabilities());
        for (DesiredCapabilities cap : caps) {
            SauceLabsIntegration.setSauceLabsOption(cap, "foo", "bar");
        }
        return caps;
    }

}
