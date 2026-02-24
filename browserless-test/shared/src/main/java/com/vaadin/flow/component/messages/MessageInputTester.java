/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.messages;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.browserless.Tests;
import com.vaadin.flow.component.ComponentUtil;

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
