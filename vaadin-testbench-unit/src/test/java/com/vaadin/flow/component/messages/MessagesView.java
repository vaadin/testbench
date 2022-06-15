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
