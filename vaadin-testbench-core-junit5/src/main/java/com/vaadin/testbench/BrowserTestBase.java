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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;

/**
 * A superclass with helper methods to aid TestBench developers create a JUnit 5
 * based tests.
 */
public abstract class BrowserTestBase extends AbstractBrowserTestBase
        implements HasCapabilities {

    private WebDriver driver;

    private Capabilities capabilities;

    @BeforeEach
    public void setWebDriverAndCapabilities(WebDriver driver,
            Capabilities capabilities) {
        this.driver = driver;
        this.capabilities = capabilities;
    }

    /**
     * Returns the {@link WebDriver} instance previously specified within
     * {@link #setWebDriverAndCapabilities(WebDriver, Capabilities)}, or (if the
     * previously provided WebDriver instance was not already a
     * {@link TestBenchDriverProxy} instance) a {@link TestBenchDriverProxy}
     * that wraps that driver.
     *
     * @return the active WebDriver instance
     */
    @Override
    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public Capabilities getCapabilities() {
        return capabilities;
    }
}
