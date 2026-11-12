/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.selenium;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.BuildInfo;
import org.openqa.selenium.WebDriver;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBenchVersion;
import com.vaadin.testbench.backend.BrowserLauncher;
import com.vaadin.testbench.backend.BrowserSetup;
import com.vaadin.testbench.parallel.setup.LocalDriver;
import com.vaadin.testbench.parallel.setup.RemoteDriver;
import com.vaadin.testbench.spi.BrowserBackend;

/**
 * Starts a browser with {@link LocalDriver} or, on a hub, with
 * {@link RemoteDriver}, and takes any {@link WebDriver} a test built.
 */
public class SeleniumLauncher implements BrowserLauncher {

    static {
        String found = new BuildInfo().getReleaseLabel();
        String expected = TestBenchVersion.expectedSeleniumVersion;
        if (found == null || !found.equals(expected)) {
            LoggerFactory.getLogger(SeleniumLauncher.class).warn(
                    "This version of TestBench depends on Selenium version "
                            + expected + " but version " + found
                            + " was found. Make sure you do not have multiple versions of Selenium on the classpath.");
        }
    }

    @Override
    public String getBackendName() {
        return Parameters.SELENIUM;
    }

    @Override
    public BrowserBackend launch(BrowserSetup setup) throws Exception {
        WebDriver driver = setup.hubUrl() != null
                ? new RemoteDriver().createDriver(setup.hubUrl(),
                        setup.capabilities())
                : LocalDriver.createDriver(setup.capabilities());
        return new SeleniumBackend(driver);
    }

    @Override
    public @Nullable BrowserBackend wrap(Object driver) {
        return driver instanceof WebDriver webDriver
                ? new SeleniumBackend(webDriver)
                : null;
    }

    @Override
    public String describeDrivers() {
        return "a Selenium WebDriver, such as new ChromeDriver()";
    }

    @Override
    public @Nullable Capabilities toCapabilities(Object capabilities) {
        return capabilities instanceof org.openqa.selenium.Capabilities selenium
                ? SeleniumCapabilities.fromSelenium(selenium)
                : null;
    }
}
