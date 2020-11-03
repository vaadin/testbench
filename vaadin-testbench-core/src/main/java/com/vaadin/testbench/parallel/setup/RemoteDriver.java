/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parallel.setup;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.TestBench;

public class RemoteDriver {

    private static final int BROWSER_INIT_ATTEMPTS = 5;

    /**
     * Creates a {@link WebDriver} instance used for running the test remotely.
     *
     * @since
     * @param capabilities
     *            the type of browser needed
     * @throws Exception
     */
    public WebDriver createDriver(String hubURL,
            DesiredCapabilities capabilities) throws Exception {
        for (int i = 1; i <= BROWSER_INIT_ATTEMPTS; i++) {
            try {
                WebDriver dr = TestBench.createDriver(
                        new RemoteWebDriver(new URL(hubURL), capabilities));
                return dr;
            } catch (Exception e) {
                System.err.println("Browser startup for " + capabilities
                        + " failed on attempt " + i + ": " + e.getMessage());
                if (i == BROWSER_INIT_ATTEMPTS) {
                    throw e;
                }
            }
        }

        // should never happen
        return null;
    }
}
