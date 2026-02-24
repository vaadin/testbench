/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.messages;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for MessageList components.
 *
 * @param <T>
 *            component type
 
  * @deprecated Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.
  */
@Tests(MessageList.class)
@Deprecated(forRemoval = true, since = "10.1")
public class MessageListTester<T extends MessageList>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public MessageListTester(T component) {
        super(component);
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
     * Get messages between given Instant (excluding).
     *
     * @param start
     *            start time
     * @param end
     *            end time
     * @return messages falling between start an end time
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
     * Get messages after given Instant (excluding).
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
     * Get messages before given Instant (excluding).
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
