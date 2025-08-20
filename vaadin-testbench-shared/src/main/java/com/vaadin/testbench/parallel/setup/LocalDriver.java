/**
 * Copyright (C) 2000-2025 Vaadin Ltd
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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.parallel.BrowserUtil;

public class LocalDriver {

    /**
     * Use createDriver method instead of constructor
     */

    /**
     * Creates a {@link WebDriver} instance used for running the test locally
     * for debug purposes. Uses firefoxDriver as WebDriver.
     *
     * @return a driver instance
     */
    static public WebDriver createDriver() {
        return createDriver(BrowserUtil.chrome());
    }

    /**
     * Creates a {@link WebDriver} instance used for running the test locally
     * for debug purposes.
     *
     * @param desiredCapabilities
     *            the capabilities to use for the driver
     * @return a driver instance
     */
    static public WebDriver createDriver(
            DesiredCapabilities desiredCapabilities) {
        WebDriver driver;
        if (BrowserUtil.isFirefox(desiredCapabilities)) {
            String firefoxPath = System.getProperty("firefox.path");
            String profilePath = System.getProperty("firefox.profile.path");

            FirefoxOptions options = new FirefoxOptions();
            if (firefoxPath != null) {
                options.setBinary(firefoxPath);

            }
            if (profilePath != null) {
                File profileDir = new File(profilePath);
                FirefoxProfile profile = new FirefoxProfile(profileDir);
                options.setProfile(profile);
            }
            if (Parameters.isHeadless()) {
                options.addArguments("-headless");
            }
            driver = new FirefoxDriver(options);
        } else if (BrowserUtil.isChrome(desiredCapabilities)) {
            ChromeOptions options = new ChromeOptions();
            // Tells chrome not to show warning
            // "You are using an unsupported command-line flag:
            // --ignore-certifcate-errors".
            // #14319
            options.addArguments("--test-type ");
            // Disable search engine choice screen
            options.addArguments("--disable-search-engine-choice-screen");

            if (Parameters.isHeadless()) {
                options.addArguments("--headless=new");
            }
            options.addArguments(Parameters.getChromeOptions());
            driver = new ChromeDriver(options);
        } else if (BrowserUtil.isSafari(desiredCapabilities)) {
            driver = new SafariDriver();
        } else if (BrowserUtil.isEdge(desiredCapabilities)) {
            driver = new EdgeDriver();
        } else {
            throw new RuntimeException(
                    "Not implemented support for running locally on "
                            + desiredCapabilities.getBrowserName());
        }

        return TestBench.createDriver(driver);
    }
}
