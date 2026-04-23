/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.nio.file.Paths;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;

/**
 * Static helper methods for Playwright-based integration tests.
 * <p>
 * Provides browser context creation with optional HAR recording and base URL
 * resolution.
 * <p>
 * HAR recording for k6 load test generation is activated transparently by the
 * {@code loadtest:record-playwright} Maven goal — no test code changes needed.
 */
public final class PlaywrightHelper {

    private PlaywrightHelper() {
    }

    /**
     * Creates a BrowserContext. When the k6 Maven plugin runs a test for
     * recording, it sets a system property that enables HAR capture
     * automatically — the test itself does not need to know about this.
     */
    public static BrowserContext createBrowserContext(Browser browser) {
        String harOutputPath = System.getProperty("k6.harOutputPath");
        if (harOutputPath != null && !harOutputPath.isEmpty()) {
            return browser.newContext(new Browser.NewContextOptions()
                    .setRecordHarPath(Paths.get(harOutputPath))
                    .setRecordHarMode(
                            com.microsoft.playwright.options.HarMode.FULL));
        }
        return browser.newContext();
    }

    /**
     * Creates a BrowserContext. When the k6 Maven plugin runs a test for
     * recording, it sets a system property that enables HAR capture
     * automatically — the test itself does not need to know about this.
     * 
     * @param browser
     *            playwrigth browser to use
     * @param contextOptions
     *            user defined browser context options
     */
    public static BrowserContext createBrowserContext(Browser browser,
            Browser.NewContextOptions contextOptions) {
        String harOutputPath = System.getProperty("k6.harOutputPath");
        if (harOutputPath != null && !harOutputPath.isEmpty()) {
            contextOptions.setRecordHarPath(Paths.get(harOutputPath))
                    .setRecordHarMode(
                            com.microsoft.playwright.options.HarMode.FULL);
        }
        return browser.newContext(contextOptions);
    }

    /**
     * Returns the base URL for the deployment under test, resolved from
     * environment variables and system properties.
     */
    public static String getBaseUrl() {
        return "http://" + getDeploymentHostname() + ":" + getDeploymentPort();
    }

    private static String getDeploymentHostname() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        return "localhost";
    }

    private static int getDeploymentPort() {
        String port = System.getProperty("server.port");
        if (port != null && !port.isEmpty()) {
            return Integer.parseInt(port);
        }
        return 8080;
    }
}
