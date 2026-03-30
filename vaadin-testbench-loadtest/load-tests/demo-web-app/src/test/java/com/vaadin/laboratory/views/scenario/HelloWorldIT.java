/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.laboratory.views.scenario;

import org.junit.jupiter.api.Assertions;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.laboratory.views.AbstractIT;
import com.vaadin.testbench.BrowserTest;

/**
 * User scenario for the HelloWorld view.
 * <p>
 * This TestBench test simulates a typical user interaction:
 * <ol>
 * <li>Open the view</li>
 * <li>Enter a name and press the "Say hello" button</li>
 * </ol>
 */
public class HelloWorldIT extends AbstractIT {

    private static final String TEST_NAME = "Vaadin User";

    @BrowserTest
    public void helloWorldWorkflow() {
        // 1. open the view - done automatically by AbstractIT.open()

        // 2. enter a name and press the say hello button
        enterNameAndSayHello();
    }

    private void enterNameAndSayHello() {
        // Enter name
        TextFieldElement nameField = $(TextFieldElement.class)
                .withCaption("Your name").single();
        Assertions.assertNotNull(nameField, "Name field should be present");
        nameField.setValue(TEST_NAME);

        // Press say hello button
        ButtonElement sayHelloButton = $(ButtonElement.class)
                .withCaption("Say hello").waitForFirst();
        sayHelloButton.click();

        // Verify notification
        NotificationElement notification = $(NotificationElement.class)
                .waitForFirst();
        String expectedText = "Hello " + TEST_NAME;
        Assertions.assertTrue(notification.getText().contains(expectedText),
                "Notification should contain '" + expectedText + "' but was '"
                        + notification.getText() + "'");
    }

    @Override
    public String getViewName() {
        return "";
    }
}
