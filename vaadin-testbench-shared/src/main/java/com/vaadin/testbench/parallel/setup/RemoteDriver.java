/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel.setup;

import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;

public class RemoteDriver {

    private static final int BROWSER_INIT_ATTEMPTS = 5;

    /**
     * Creates a {@link WebDriver} instance used for running the test remotely.
     *
     * @param hubURL
     *            the URL of the hub
     * @param capabilities
     *            the type of browser needed
     * @return a driver instance
     * @throws Exception
     *             if the driver could not be created
     */
    public WebDriver createDriver(String hubURL,
            DesiredCapabilities capabilities) throws Exception {
        for (int i = 1; i <= BROWSER_INIT_ATTEMPTS; i++) {
            try {
                ClientConfig config = ClientConfig.defaultConfig()
                        .readTimeout(
                                Duration.ofSeconds(Parameters.getReadTimeout()))
                        .baseUrl(new URL(hubURL));
                CommandExecutor executor = new HttpCommandExecutor(config);
                WebDriver driver = new RemoteWebDriver(executor, capabilities);
                return TestBench.createDriver(driver);
            } catch (Exception e) {
                getLogger().error("Browser startup for " + capabilities
                        + " failed on attempt " + i, e);
                if (i == BROWSER_INIT_ATTEMPTS) {
                    throw e;
                }
            }
        }

        // should never happen
        return null;
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());

    }
}
