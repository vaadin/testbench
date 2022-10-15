/**
 * Copyright (C) 2020 Vaadin Ltd
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchTest;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

public class InvocationContextProviderTest extends CapabilitiesTest {

    @Override
    @BeforeEach
    public void setup() {
        // Do not actually start a session, just test the class methods
    }

    @TestBenchTest
    public void tbMethodNameInCapabilities(TestInfo testInfo) {
        Assertions.assertEquals("bar", SauceLabsIntegration
                .getSauceLabsOption(getDesiredCapabilities(), "foo"));
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            Assertions.assertEquals(testInfo.getDisplayName(),
                    SauceLabsIntegration.getSauceLabsOption(
                            getDesiredCapabilities(),
                            SauceLabsIntegration.CapabilityType.NAME));
        }
    }

    @BrowserConfiguration
    public List<DesiredCapabilities> getBrowsers() {
        List<DesiredCapabilities> caps = Arrays.asList(
                Browser.CHROME.getDesiredCapabilities(),
                Browser.FIREFOX.getDesiredCapabilities());
        for (DesiredCapabilities cap : caps) {
            SauceLabsIntegration.setSauceLabsOption(cap, "foo", "bar");
        }
        return caps;
    }
}
