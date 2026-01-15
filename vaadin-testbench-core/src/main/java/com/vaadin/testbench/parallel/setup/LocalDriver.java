/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel.setup;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.ParallelTest;

public class LocalDriver {

    /**
     * Use createDriver method instead of constructor
     */

    /**
     * Creates a {@link WebDriver} instance used for running the test locally
     * for debug purposes. Uses firefoxDriver as WebDriver.
     *
     * @return created driver instance
     */
    static public WebDriver createDriver() {
        return createDriver(ParallelTest.getDefaultCapabilities().get(0));
    }

    /**
     * Creates a {@link WebDriver} instance used for running the test locally
     * for debug purposes.
     *
     * @param desiredCapabilities
     *            the capabilities for driver setup
     * @return created driver instance
     */
    static public WebDriver createDriver(DesiredCapabilities desiredCapabilities) {
        WebDriver driver;
        if (BrowserUtil.isFirefox(desiredCapabilities)) {
            String firefoxPath = System.getProperty("firefox.path");
            String profilePath = System.getProperty("firefox.profile.path");

            FirefoxOptions options = new FirefoxOptions();
            if (firefoxPath != null) {
                options.setBinary(new FirefoxBinary(new File(firefoxPath)));
            }
            if (profilePath != null) {
                File profileDir = new File(profilePath);
                FirefoxProfile profile = new FirefoxProfile(profileDir);
                options.setProfile(profile);
            }
            driver = new FirefoxDriver(options);
        } else if (BrowserUtil.isChrome(desiredCapabilities)) {
            // Tells chrome not to show warning
            // "You are using an unsupported command-line flag: --ignore-certifcate-errors".
            // #14319
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--test-type ");
            options.addArguments("--disable-search-engine-choice-screen");
            driver = new ChromeDriver(options);
        } else if (BrowserUtil.isSafari(desiredCapabilities)) {
            driver = new SafariDriver();
        } else if (BrowserUtil.isEdge(desiredCapabilities)) {
            driver = new EdgeDriver();
        } else if (BrowserUtil.isIE(desiredCapabilities)) {
            driver = new InternetExplorerDriver();
        } else {
            throw new RuntimeException(
                    "Not implemented support for running locally on "
                            + desiredCapabilities.getBrowserName());
        }

        return TestBench.createDriver(driver);
    }
}
