/**
 * Copyright (C) 2000-2023 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel.setup;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.HttpClient;

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
                HttpClient.Factory httpClientFactory = new HttpClientFactoryDefaultWrapper();
                HttpCommandExecutor executor = new HttpCommandExecutor(
                        new HashMap<>(), new URL(hubURL), httpClientFactory);
                WebDriver dr = TestBench.createDriver(
                        new RemoteWebDriver(executor, capabilities));
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

    // Override the default builder to set timeouts as in newer selenium version
    // readTimeout to 3 minutes instead of 3 hours
    // connectionTimeout to 10 seconds instead of 2 minutes
    private static class HttpClientFactoryDefaultWrapper
            implements HttpClient.Factory {
        private final HttpClient.Factory delegate = HttpClient.Factory
                .createDefault();

        @Override
        public HttpClient.Builder builder() {
            return delegate.builder().connectionTimeout(Duration.ofSeconds(10))
                    .readTimeout(Duration.ofMinutes(3));
        }

        @Override
        public void cleanupIdleClients() {
            delegate.cleanupIdleClients();
        }
    }
}
