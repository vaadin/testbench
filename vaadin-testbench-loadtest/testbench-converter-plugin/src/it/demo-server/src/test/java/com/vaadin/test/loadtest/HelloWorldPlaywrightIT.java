/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.test.loadtest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.vaadin.testbench.loadtest.PlaywrightHelper;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright user scenario for the HelloWorldView. When the
 * {@code k6.harOutputPath} system property is set, {@link PlaywrightHelper}
 * configures the BrowserContext with native HAR recording and
 * {@code TestbenchRecordMojo} converts the resulting HAR into a k6 script.
 * <p>
 * Uses the system-installed Chrome via {@code setChannel("chrome")} to avoid
 * requiring a separate Playwright Chromium download.
 */
public class HelloWorldPlaywrightIT {

    private static final String TEST_NAME = "Vaadin User";

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setChannel("chrome")
                        .setHeadless(true));
        context = PlaywrightHelper.createBrowserContext(browser);
        page = context.newPage();
        page.navigate(PlaywrightHelper.getBaseUrl() + "/");
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

    @Test
    public void helloWorldWorkflow() {
        page.getByLabel("Your name").fill(TEST_NAME);
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Say hello")).click();
        assertThat(page.locator("#greeting"))
                .containsText("Hello " + TEST_NAME);
    }
}
