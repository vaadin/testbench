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
            TBMethod method = new TBMethod(testMethods.get(0).getMethod(),
                    Browser.IE11.getDesiredCapabilities());
            Assert.assertEquals(method, testMethods.get(0));
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
            TBMethod method1 = new TBMethod(testMethods.get(0).getMethod(),
                    Browser.FIREFOX.getDesiredCapabilities());
            DesiredCapabilities safari9Capabilities = Browser.SAFARI.getDesiredCapabilities();
            safari9Capabilities.setVersion("9");
            TBMethod method2 = new TBMethod(testMethods.get(1).getMethod(), safari9Capabilities);
            Assert.assertEquals(method1, testMethods.get(0));
            Assert.assertEquals(method2, testMethods.get(1));
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
            TBMethod method = new TBMethod(testMethods.get(0).getMethod(),
                    Browser.CHROME.getDesiredCapabilities());
            Assert.assertEquals(method, testMethods.get(0));
        } finally {
            Parameters.setGridBrowsers(oldBrowsers);
        }
    }

    public static class ParallelTestWithBrowserConfiguration extends ParallelTest {

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

    public static class ParallelTestWithoutBrowserConfiguration extends ParallelTest {
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
