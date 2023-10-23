/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.messages;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class MessageInputTesterTest extends UIUnitTest {

    MessagesView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(MessagesView.class);
        view = navigate(MessagesView.class);
    }

    @Test
    void emptyMessage_noEventIsFired() {
        AtomicReference<String> message = new AtomicReference<>();

        view.input.addSubmitListener(submitEvent -> message.compareAndSet(null,
                submitEvent.getValue()));

        final String testMessage = "";
        test(view.input).send(testMessage);
        Assertions.assertNull(message.get());
    }

    @Test
    void sendMessage_eventIsReceived() {
        AtomicReference<String> message = new AtomicReference<>();

        view.input.addSubmitListener(submitEvent -> message.compareAndSet(null,
                submitEvent.getValue()));

        final String testMessage = "Hello";
        test(view.input).send(testMessage);
        Assertions.assertEquals(testMessage, message.get());
    }

    @Test
    void disabledMessage_throwsException() {
        AtomicReference<String> message = new AtomicReference<>();

        view.input.addSubmitListener(submitEvent -> message.compareAndSet(null,
                submitEvent.getValue()));
        view.input.setEnabled(false);

        final String testMessage = "Hello";
        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.input).send(testMessage));
    }
}
