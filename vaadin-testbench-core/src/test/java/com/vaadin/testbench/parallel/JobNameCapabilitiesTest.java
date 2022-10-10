/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
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
            DesiredCapabilities cap = ((TBMethod) testMethod).getCapabilities();
            Assert.assertEquals("bar",
                    SauceLabsIntegration.getSauceLabsOption(cap, "foo"));
            Assert.assertEquals(testMethod.getName(),
                    SauceLabsIntegration.getSauceLabsOption(cap,
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
            List<DesiredCapabilities> caps = Arrays.asList(
                    Browser.CHROME.getDesiredCapabilities(),
                    Browser.FIREFOX.getDesiredCapabilities());
            for (DesiredCapabilities cap : caps) {
                SauceLabsIntegration.setSauceLabsOption(cap, "foo", "bar");
            }
            return caps;
        }
    }
}
