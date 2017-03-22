/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.parallel.setup;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
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
     */
    static public WebDriver createDriver() {
        return createDriver(ParallelTest.getDefaultCapabilities().get(0));
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

            if (firefoxPath != null) {
                if (profilePath != null) {
                    File profileDir = new File(profilePath);
                    FirefoxProfile profile = new FirefoxProfile(profileDir);
                    driver = new FirefoxDriver(
                            new FirefoxBinary(new File(firefoxPath)), profile);
                } else {
                    driver = new FirefoxDriver(
                            new FirefoxBinary(new File(firefoxPath)), null);
                }

            } else {
                driver = new FirefoxDriver();
            }
        } else if (BrowserUtil.isChrome(desiredCapabilities)) {
            // Tells chrome not to show warning
            // "You are using an unsupported command-line flag:
            // --ignore-certifcate-errors".
            // #14319
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--test-type ");
            driver = new ChromeDriver(options);
        } else if (BrowserUtil.isSafari(desiredCapabilities)) {
            driver = new SafariDriver();
        } else if (BrowserUtil.isPhantomJS(desiredCapabilities)) {
            driver = new PhantomJSDriver();
        } else {
            throw new RuntimeException(
                    "Not implemented support for running locally on "
                            + desiredCapabilities.getBrowserName());
        }

        return TestBench.createDriver(driver);
    }
}
