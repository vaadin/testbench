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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.HasDriver;
import com.vaadin.testbench.TestBenchTest;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.SauceLabsIntegration;
import com.vaadin.testbench.parameters.TestBenchTestInfo;

public class JobNameCapabilitiesTest implements HasDriver {

    @TestBenchTest
    public void tbMethodNameInCapabilities(TestInfo testInfo,
            TestBenchTestInfo testBenchTestInfo) {
        DesiredCapabilities capabilities = new DesiredCapabilities(
                testBenchTestInfo.getCapabilities());
        Assertions.assertEquals("bar",
                SauceLabsIntegration.getSauceLabsOption(capabilities, "foo"));
        Assertions.assertEquals(testInfo.getDisplayName(),
                SauceLabsIntegration.getSauceLabsOption(capabilities,
                        SauceLabsIntegration.CapabilityType.NAME));
    }

    @Override
    public WebDriver getDriver() {
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
