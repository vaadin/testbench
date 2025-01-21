/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.Capabilities;

import com.vaadin.testbench.browser.BrowserTestInfo;
import com.vaadin.testbench.parallel.Browser;

/**
 * A superclass with helper methods to aid TestBench developers create a JUnit 5
 * based tests.
 */
@Execution(ExecutionMode.CONCURRENT)
public abstract class BrowserTestBase extends AbstractBrowserDriverTestBase {

    @RegisterExtension
    public ScreenshotOnFailureExtension screenshotOnFailureExtension = new ScreenshotOnFailureExtension(
            this, true);

    private BrowserTestInfo browserTestInfo;

    @BeforeEach
    public void setBrowserTestInfo(BrowserTestInfo browserTestInfo) {
        setDriver(browserTestInfo.driver());
        this.browserTestInfo = browserTestInfo;
    }

    protected Capabilities getCapabilities() {
        return browserTestInfo.capabilities();
    }

    protected String getHubHostname() {
        return browserTestInfo.hubHostname();
    }

    protected Browser getRunLocallyBrowser() {
        return browserTestInfo.runLocallyBrowser();
    }

    protected String getRunLocallyBrowserVersion() {
        return browserTestInfo.runLocallyBrowserVersion();
    }

}
