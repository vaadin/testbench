/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.test.loadtest;

import org.junit.jupiter.api.Assertions;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.BrowserTest;

/**
 * User scenario for the HelloWorldView. Drives the browser through the
 * recording proxy so that {@code TestbenchRecordMojo} captures a HAR.
 */
public class HelloWorldIT extends AbstractIT {

    private static final String TEST_NAME = "Vaadin User";

    @BrowserTest
    public void helloWorldWorkflow() {
        TextFieldElement nameField = $(TextFieldElement.class)
                .withCaption("Your name").waitForFirst();
        nameField.setValue(TEST_NAME);

        ButtonElement sayHelloButton = $(ButtonElement.class)
                .withCaption("Say hello").waitForFirst();
        sayHelloButton.click();

        NotificationElement notification = $(NotificationElement.class)
                .waitForFirst();
        Assertions.assertTrue(
                notification.getText().contains("Hello " + TEST_NAME),
                "Notification should greet the entered name, got: "
                        + notification.getText());
    }

    @Override
    public String getViewName() {
        return "";
    }
}
