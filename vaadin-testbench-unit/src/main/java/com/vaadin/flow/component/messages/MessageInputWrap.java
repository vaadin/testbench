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
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for MessageInput components.
 *
 * @param <T>
 *            component type
 */
@Wraps(MessageInput.class)
public class MessageInputWrap<T extends MessageInput> extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public MessageInputWrap(T component) {
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
        ComponentUtil.fireEvent(getComponent(),
                new MessageInput.SubmitEvent(getComponent(), true, message));
    }
}
