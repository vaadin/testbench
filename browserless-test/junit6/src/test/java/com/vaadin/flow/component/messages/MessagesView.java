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

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "messages", registerAtStartup = false)
public class MessagesView extends Component implements HasComponents {

    MessageInput input;
    MessageList list;

    MessageListItem one = new MessageListItem("one",
            LocalDateTime.now().withHour(0).toInstant(ZoneOffset.UTC), "Joe");
    MessageListItem two = new MessageListItem("two",
            LocalDateTime.now().withHour(12).toInstant(ZoneOffset.UTC), "Jane");
    MessageListItem three = new MessageListItem("three",
            LocalDateTime.now().withHour(23).toInstant(ZoneOffset.UTC), "Joe");

    public MessagesView() {
        list = new MessageList();
        input = new MessageInput();
        list.setItems(one, two, three);

        add(input, list);
    }
}
