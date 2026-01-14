/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.ParameterizedBrowserTest;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;

class ParameterizedBrowserTestTest {

    public static final String TEST_BROWSER_VERSION = "119-test";

    abstract static class Support extends BrowserTestBase
            implements DriverSupplier {

        protected WebDriver webDriver = Mockito.mock(WebDriver.class);
        protected DesiredCapabilities expectedCapabilities;

        public Support(Browser browser) {
            this(browser.getDesiredCapabilities());
        }

        public Support(DesiredCapabilities expectedCapabilities) {
            this.expectedCapabilities = expectedCapabilities;
        }

        @Override
        public WebDriver createDriver() {
            return webDriver;
        }

        @ParameterizedBrowserTest
        @ValueSource(strings = { "param1", "param2" })
        void singleBrowserTest_worksWithParametrizedTest(String parameter,
                BrowserTestInfo browserTestInfo) {
            Assertions.assertNotNull(parameter);
            Assertions.assertTrue(parameter.matches("param[12]"));

            Assertions.assertEquals(expectedCapabilities.getBrowserName(),
                    browserTestInfo.capabilities().getBrowserName());
            Assertions.assertEquals(expectedCapabilities.getBrowserVersion(),
                    browserTestInfo.capabilities().getBrowserVersion());
        }

    }

    @Nested
    @RunLocally
    class BrowserFromRunLocallyDefault extends Support {
        public BrowserFromRunLocallyDefault() {
            super(BrowserUtil.firefox());
        }
    }

    @Nested
    @RunLocally(Browser.CHROME)
    class BrowserFromRunLocally extends Support {
        public BrowserFromRunLocally() {
            super(BrowserUtil.chrome());
        }
    }

    @Nested
    @RunLocally(value = Browser.CHROME, version = TEST_BROWSER_VERSION)
    class BrowserFromRunLocallyWithVersion extends Support {

        public BrowserFromRunLocallyWithVersion() {
            super(BrowserUtil.chrome());
            expectedCapabilities.setVersion(TEST_BROWSER_VERSION);
        }
    }

    @Nested
    @RunOnHub("remote.host")
    class BrowserFromRunOnHub extends Support {

        public BrowserFromRunOnHub() {
            super(BrowserUtil.chrome());
        }
    }

}
