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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for MessageList components.
 *
 * @param <T>
 *            component type
 */
@Wraps(MessageList.class)
public class MessageListWrap<T extends MessageList> extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public MessageListWrap(T component) {
        super(component);
    }

    /**
     * Add a new message item to the MessageList.
     *
     * @param item
     *            item to add
     */
    public void addItem(MessageListItem item) {
        ensureComponentIsUsable();

        final ArrayList<MessageListItem> messageListItems = new ArrayList<>(
                getComponent().getItems());
        messageListItems.add(item);
        getComponent().setItems(messageListItems);
    }

    /**
     * Get amount of messages in the messageList.
     *
     * @return message count
     */
    public int size() {
        ensureComponentIsUsable();
        return getComponent().getItems().size();
    }

    /**
     * Get message in given index. index is 0 based.
     *
     * @param index
     *            item index
     * @return message in index
     * @throws IndexOutOfBoundsException
     *             – if the index is out of range (index < 0 || index >= size())
     */
    public MessageListItem getMessage(int index) {
        ensureComponentIsUsable();
        return getComponent().getItems().get(index);
    }

    /**
     * Get all messages in list.
     *
     * @return all available messages
     */
    public List<MessageListItem> getMessages() {
        ensureComponentIsUsable();
        return getComponent().getItems();
    }

    /**
     * Get messages between given Instances (excluding).
     *
     * @param start
     *            start time
     * @param end
     *            end time
     * @return massages falling between start an end time
     */
    public List<MessageListItem> getMessages(Instant start, Instant end) {
        ensureComponentIsUsable();
        return getComponent().getItems().stream()
                .filter(message -> message.getTime() != null)
                .filter(message -> message.getTime().isAfter(start)
                        && message.getTime().isBefore(end))
                .collect(Collectors.toList());
    }

    /**
     * Get messages after given Instance (excluding).
     *
     * @param start
     *            start time
     * @return messages after start time
     */
    public List<MessageListItem> getMessagesAfter(Instant start) {
        ensureComponentIsUsable();
        return getComponent().getItems().stream()
                .filter(message -> message.getTime() != null)
                .filter(message -> message.getTime().isAfter(start))
                .collect(Collectors.toList());
    }

    /**
     * Get messages before given Instance (excluding).
     *
     * @param end
     *            end time
     * @return messages before end time
     */
    public List<MessageListItem> getMessagesBefore(Instant end) {
        ensureComponentIsUsable();
        return getComponent().getItems().stream()
                .filter(message -> message.getTime() != null)
                .filter(message -> message.getTime().isBefore(end))
                .collect(Collectors.toList());
    }

    /**
     * Get all messages for a given userName.
     *
     * @param userName
     *            user to get messages for (nullable)
     * @return messages for user, for {@code null} messages without defined
     *         userName
     */
    public List<MessageListItem> getMessages(String userName) {
        ensureComponentIsUsable();
        final List<MessageListItem> items = getComponent().getItems();
        if (userName == null) {
            return items.stream()
                    .filter(message -> message.getUserName() == null)
                    .collect(Collectors.toList());
        }
        return items.stream()
                .filter(message -> userName.equals(message.getUserName()))
                .collect(Collectors.toList());
    }
}
