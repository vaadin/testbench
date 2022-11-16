/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
