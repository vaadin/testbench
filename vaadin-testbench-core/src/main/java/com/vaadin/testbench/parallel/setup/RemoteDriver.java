/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
     * @since 4.0.0
     * @param hubURL
     *            address of the test hub
     * @param capabilities
     *            the type of browser needed
     * @return created driver
     * @throws Exception
     *             when browser startup has failed for any reason on every
     *             initialization attempt (default amount of attempts: 5, final
     *             attempt throws)
     */
    public WebDriver createDriver(String hubURL,
            DesiredCapabilities capabilities) throws Exception {
        for (int i = 1; i <= BROWSER_INIT_ATTEMPTS; i++) {
            try {
                WebDriver dr = TestBench
                        .createDriver(new RemoteWebDriver(new URL(
                                hubURL), capabilities));
                return dr;
            } catch (Exception e) {
                System.err.println("Browser startup for "
                        + capabilities + " failed on attempt " + i
                        + ": " + e.getMessage());
                if (i == BROWSER_INIT_ATTEMPTS) {
                    throw e;
                }
            }
        }

        // should never happen
        return null;
    }
}
