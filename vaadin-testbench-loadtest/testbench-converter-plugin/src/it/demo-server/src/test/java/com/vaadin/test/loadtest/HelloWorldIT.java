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

import com.vaadin.flow.component.html.testbench.InputTextElement;
import com.vaadin.flow.component.html.testbench.NativeButtonElement;
import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.testbench.BrowserTest;

/**
 * User scenario for the HelloWorldView. Drives the browser through the
 * recording proxy so that {@code TestbenchRecordMojo} captures a HAR.
 */
public class HelloWorldIT extends AbstractIT {

    private static final String TEST_NAME = "Vaadin User";

    @BrowserTest
    public void helloWorldWorkflow() {
        InputTextElement nameField = $(InputTextElement.class).id("name");
        nameField.setValue(TEST_NAME);

        NativeButtonElement sayHelloButton = $(NativeButtonElement.class)
                .id("say-hello");
        sayHelloButton.click();

        SpanElement greeting = $(SpanElement.class).id("greeting");
        Assertions.assertTrue(
                greeting.getText().contains("Hello " + TEST_NAME),
                "Greeting should mention the entered name, got: "
                        + greeting.getText());
    }

    @Override
    public String getViewName() {
        return "";
    }
}
