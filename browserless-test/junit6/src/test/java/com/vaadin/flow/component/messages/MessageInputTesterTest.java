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

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
class MessageInputTesterTest extends BrowserlessTest {

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
