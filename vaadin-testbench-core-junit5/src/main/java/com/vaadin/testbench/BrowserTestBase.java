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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;

/**
 * A superclass with helper methods to aid TestBench developers create a JUnit 5
 * based tests.
 */
public abstract class BrowserTestBase extends AbstractBrowserDriverTestBase
        implements HasCapabilities {

    @RegisterExtension
    public ScreenshotOnFailureExtension screenshotOnFailureExtension = new ScreenshotOnFailureExtension(
            this, true);

    private Capabilities capabilities;

    @BeforeEach
    public void setWebDriverAndCapabilities(WebDriver driver,
            Capabilities capabilities) {
        setDriver(driver);
        this.capabilities = capabilities;
    }

    @Override
    public Capabilities getCapabilities() {
        return capabilities;
    }
}
