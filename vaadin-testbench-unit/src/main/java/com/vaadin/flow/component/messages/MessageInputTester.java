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

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for MessageInput components.
 *
 * @param <T>
 *            component type
 */
@Tests(MessageInput.class)
public class MessageInputTester<T extends MessageInput>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public MessageInputTester(T component) {
        super(component);
    }

    /**
     * Send an input message through the MessageInput component.
     *
     * @param message
     *            message from component
     */
    public void send(String message) {
        ensureComponentIsUsable();
        if (message == null || message.isEmpty()) {
            return;
        }
        ComponentUtil.fireEvent(getComponent(),
                new MessageInput.SubmitEvent(getComponent(), true, message));
    }
}
