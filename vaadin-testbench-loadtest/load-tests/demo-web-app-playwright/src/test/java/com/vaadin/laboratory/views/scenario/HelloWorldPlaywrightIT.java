/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.laboratory.views.scenario;

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
 * User scenario for the HelloWorld view.
 * <p>
 * This Playwright test simulates a typical user interaction:
 * <ol>
 * <li>Open the view</li>
 * <li>Enter a name and press the "Say hello" button</li>
 * </ol>
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
                .launch(new BrowserType.LaunchOptions().setHeadless(true));
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
        // Enter a name and press the say hello button
        enterNameAndSayHello();
    }

    private void enterNameAndSayHello() {
        // Enter name
        page.getByLabel("Your name").fill(TEST_NAME);

        // Press say hello button
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Say hello")).click();

        // Verify notification
        assertThat(page.locator("vaadin-notification-card"))
                .containsText("Hello " + TEST_NAME);
    }
}
