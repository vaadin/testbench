/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestTemplate;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.BrowserTestClass;
import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.SauceLabsIntegration;

@BrowserTestClass
public class BrowserTestClassTest implements DriverSupplier {

    @TestTemplate // run by extension, standard JUnit annotation
    public void testTemplate_hasCapabilitiesInjected(TestInfo testInfo,
            BrowserTestInfo browserTestInfo) {
        Assertions.assertTrue(
                testInfo.getDisplayName().contains("[any_Chrome_]"));
        assertCapabilities(testInfo, browserTestInfo);
    }

    @BrowserTest // run by extension, using wrapper additionally
    public void browserTest_hasCapabilitiesInjected(TestInfo testInfo,
            BrowserTestInfo browserTestInfo) {
        Assertions.assertTrue(
                testInfo.getDisplayName().contains("[any_Chrome_]"));
        assertCapabilities(testInfo, browserTestInfo);
    }

    @Test // not run by extension
    public void test_capabilitiesNotInjected(TestInfo testInfo) {
        Assertions.assertFalse(
                testInfo.getDisplayName().contains("[any_Chrome_]"));
    }

    private void assertCapabilities(TestInfo testInfo,
            BrowserTestInfo browserTestInfo) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(
                browserTestInfo.capabilities());
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
