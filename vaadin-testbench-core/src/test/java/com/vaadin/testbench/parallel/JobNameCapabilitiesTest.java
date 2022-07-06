/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parallel;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.ParallelRunner.TBMethod;

public class JobNameCapabilitiesTest {

    @Test
    public void tbMethodNameInCapabilities() throws InitializationError {
        ParallelRunner parallelRunner = new ParallelRunner(
                ParallelTestDummy.class);
        List<FrameworkMethod> testMethods = parallelRunner.computeTestMethods();
        Assert.assertEquals(4, testMethods.size());
        for (FrameworkMethod testMethod : testMethods) {
            Assert.assertEquals(testMethod.getName(),
                    SauceLabsIntegration.getSauceLabsOption(
                            ((TBMethod) testMethod).getCapabilities(),
                            SauceLabsIntegration.CapabilityType.NAME));
        }
    }

    public static class ParallelTestDummy extends ParallelTest {

        public ParallelTestDummy() {
        }

        @Override
        public void setup() throws Exception {
            // Do not actually start a session, just test the class methods
        }

        @Test
        public void dummy() {

        }

        @Test
        public void dummy2() {

        }

        @BrowserConfiguration
        public List<DesiredCapabilities> getBrowsers() {
            return Arrays.asList(Browser.CHROME.getDesiredCapabilities(),
                    Browser.FIREFOX.getDesiredCapabilities());
        }
    }
}
