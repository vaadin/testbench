package com.vaadin.testbench.parallel;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.parallel.ParallelRunner.TBMethod;

public class DefaultBrowserConfigurationTest {

    @Test
    public void withBrowserConfigurationInClass() throws InitializationError {
        String oldBrowsers = Parameters.getGridBrowsersString();
        try {
            Parameters.setGridBrowsers("firefox,safari-9");
            ParallelRunner parallelRunner = new ParallelRunner(
                    ParallelTestWithBrowserConfiguration.class);
            List<FrameworkMethod> testMethods = parallelRunner
                    .computeTestMethods();
            Assert.assertEquals(1, testMethods.size());
            DesiredCapabilities caps = Browser.IE11.getDesiredCapabilities();
            TBMethod method = (TBMethod) testMethods.get(0);
            Assert.assertEquals(caps.getBrowserName(),
                    method.getCapabilities().getBrowserName());
            Assert.assertEquals(caps.getVersion(),
                    method.getCapabilities().getVersion());
            Assert.assertEquals(caps.getPlatform(),
                    method.getCapabilities().getPlatform());
        } finally {
            Parameters.setGridBrowsers(oldBrowsers);
        }
    }

    @Test
    public void withBrowsersConfigurationInParameters()
            throws InitializationError {
        String oldBrowsers = Parameters.getGridBrowsersString();
        try {
            Parameters.setGridBrowsers("firefox,safari-9");
            ParallelRunner parallelRunner = new ParallelRunner(
                    ParallelTestWithoutBrowserConfiguration.class);
            List<FrameworkMethod> testMethods = parallelRunner
                    .computeTestMethods();
            Assert.assertEquals(2, testMethods.size());
            DesiredCapabilities caps1 = Browser.FIREFOX
                    .getDesiredCapabilities();
            TBMethod method1 = (TBMethod) testMethods.get(0);
            DesiredCapabilities caps2 = Browser.SAFARI.getDesiredCapabilities();
            caps2.setVersion("9");
            TBMethod method2 = (TBMethod) testMethods.get(1);
            Assert.assertEquals(caps1.getBrowserName(),
                    method1.getCapabilities().getBrowserName());
            Assert.assertEquals(caps1.getVersion(),
                    method1.getCapabilities().getVersion());
            Assert.assertEquals(caps1.getPlatform(),
                    method1.getCapabilities().getPlatform());
            Assert.assertEquals(caps2.getBrowserName(),
                    method2.getCapabilities().getBrowserName());
            Assert.assertEquals(caps2.getVersion(),
                    method2.getCapabilities().getVersion());
            Assert.assertEquals(caps2.getPlatform(),
                    method2.getCapabilities().getPlatform());
        } finally {
            Parameters.setGridBrowsers(oldBrowsers);
        }
    }

    @Test
    public void withoutBrowsersConfiguration() throws InitializationError {
        String oldBrowsers = Parameters.getGridBrowsersString();
        try {
            Parameters.setGridBrowsers("");
            ParallelRunner parallelRunner = new ParallelRunner(
                    ParallelTestWithoutBrowserConfiguration.class);
            List<FrameworkMethod> testMethods = parallelRunner
                    .computeTestMethods();
            Assert.assertEquals(1, testMethods.size());
            DesiredCapabilities caps = Browser.CHROME.getDesiredCapabilities();
            TBMethod method = (TBMethod) testMethods.get(0);
            Assert.assertEquals(caps.getBrowserName(),
                    method.getCapabilities().getBrowserName());
            Assert.assertEquals(caps.getVersion(),
                    method.getCapabilities().getVersion());
            Assert.assertEquals(caps.getPlatform(),
                    method.getCapabilities().getPlatform());
        } finally {
            Parameters.setGridBrowsers(oldBrowsers);
        }
    }

    public static class ParallelTestWithBrowserConfiguration
            extends ParallelTest {

        public ParallelTestWithBrowserConfiguration() {
        }

        @Override
        public void setup() throws Exception {
            // Do not actually start a session, just test the class methods
        }

        @Test
        public void dummy() {

        }

        @BrowserConfiguration
        public List<DesiredCapabilities> getBrowserConfiguration() {
            return Arrays.asList(Browser.IE11.getDesiredCapabilities());
        }
    }

    public static class ParallelTestWithoutBrowserConfiguration
            extends ParallelTest {
        public ParallelTestWithoutBrowserConfiguration() {
        }

        @Override
        public void setup() throws Exception {
            // Do not actually start a session, just test the class methods
        }

        @Test
        public void dummy() {

        }
    }
}
