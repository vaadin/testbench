/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parallel.setup;

import java.io.File;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.parallel.BrowserUtil;

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

public class LocalDriver {

    /**
     * Use createDriver method instead of constructor
     */

    /**
     * Creates a {@link WebDriver} instance used for running the test locally
     * for debug purposes.
     */
    static public WebDriver createDriver() {
        return createDriver(BrowserUtil.chrome());
    }

    /**
     * Creates a {@link WebDriver} instance used for running the test locally
     * for debug purposes.
     */
    static public WebDriver createDriver(
            DesiredCapabilities desiredCapabilities) {
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
            options.setHeadless(Parameters.isHeadless());
            driver = new FirefoxDriver(options);
        } else if (BrowserUtil.isChrome(desiredCapabilities)) {
            // Tells chrome not to show warning
            // "You are using an unsupported command-line flag:
            // --ignore-certifcate-errors".
            // #14319
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--test-type ");
            options.setHeadless(Parameters.isHeadless());
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
