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
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for Playwright-based integration tests.
 * <p>
 * Provides managed Playwright lifecycle (browser, context, page) and automatic
 * navigation to the view returned by {@link #getViewName()}.
 * <p>
 * HAR recording for k6 load test generation is activated transparently by the
 * {@code loadtest:record-playwright} Maven goal — no test code changes needed.
 * <p>
 * Usage:
 * 
 * <pre>
 * public class MyViewIT extends AbstractPlaywrightHelper {
 *     {@literal @}Test
 *     public void myWorkflow() {
 *         page.getByLabel("Name").fill("test");
 *         page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit")).click();
 *     }
 *
 *     {@literal @}Override
 *     public String getViewName() {
 *         return "my-view";
 *     }
 * }
 * </pre>
 */

public abstract class AbstractPlaywrightHelper {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = createBrowserContext();
        page = context.newPage();
        page.navigate(getBaseUrl() + "/" + getViewName());
    }

    @AfterEach
    public void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * Creates the BrowserContext. When the k6 Maven plugin runs this test for
     * recording, it sets a system property that enables HAR capture
     * automatically — the test itself does not need to know about this.
     */
    private BrowserContext createBrowserContext() {
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
     * Returns the view path (e.g., "" for root, "crud-example" for
     * /crud-example).
     */
    public abstract String getViewName();

    protected String getBaseUrl() {
        return "http://" + getDeploymentHostname() + ":" + getDeploymentPort();
    }

    protected static String getDeploymentHostname() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        return "localhost";
    }

    protected static int getDeploymentPort() {
        String port = System.getProperty("server.port");
        if (port != null && !port.isEmpty()) {
            return Integer.parseInt(port);
        }
        return 8080;
    }

}
