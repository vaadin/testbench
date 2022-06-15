/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
class MessageInputWrapTest extends UIUnitTest {

    MessagesView view;
    MessageInputWrap<MessageInput> input_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(MessagesView.class);
        view = navigate(MessagesView.class);
        input_ = wrap(view.input);
    }

    @Test
    void emptyMessage_noEventIsFired() {
        AtomicReference<String> message = new AtomicReference<>();

        view.input.addSubmitListener(submitEvent -> message.compareAndSet(null,
                submitEvent.getValue()));

        final String testMessage = "";
        input_.send(testMessage);
        Assertions.assertNull(message.get());
    }

    @Test
    void sendMessage_eventIsReceived() {
        AtomicReference<String> message = new AtomicReference<>();

        view.input.addSubmitListener(submitEvent -> message.compareAndSet(null,
                submitEvent.getValue()));

        final String testMessage = "Hello";
        input_.send(testMessage);
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
                () -> input_.send(testMessage));
    }
}
