/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.laboratory.views.scenario;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.vaadin.testbench.loadtest.AbstractPlaywrightHelper;

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
public class HelloWorldPlaywrightIT extends AbstractPlaywrightHelper {

    private static final String TEST_NAME = "Vaadin User";

    @Test
    public void helloWorldWorkflow() {
        // 1. open the view - done automatically by AbstractPlaywrightHelper

        // 2. enter a name and press the say hello button
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

    @Override
    public String getViewName() {
        return "";
    }
}
