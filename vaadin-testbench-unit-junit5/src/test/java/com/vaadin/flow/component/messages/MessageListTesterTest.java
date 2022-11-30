/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.messages;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.TreeOnFailureExtension;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
@ExtendWith(TreeOnFailureExtension.class)
class MessageListTesterTest extends UIUnitTest {

    MessagesView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(MessagesView.class);
        view = navigate(MessagesView.class);
    }

    @Test
    void size_returnsCorrectSize() {
        Assertions.assertEquals(3, test(view.list).size());

        addItem(view.list, new MessageListItem("Added message"));

        Assertions.assertEquals(4, test(view.list).size(),
                "Message should have been added to the list");
    }

    @Test
    void getMessages_allMessagesReturned() {
        Assertions.assertIterableEquals(
                Arrays.asList(view.one, view.two, view.three),
                test(view.list).getMessages());
    }

    @Test
    void getMessageByIndex_correctMessageReturned() {
        final MessageListTester<MessageList> list_ = test(view.list);
        Assertions.assertEquals(view.two, list_.getMessage(1));
        Assertions.assertEquals(view.three, list_.getMessage(2));
        Assertions.assertEquals(view.one, list_.getMessage(0));
    }

    @Test
    void getMessageFromTime_messagesAfterAreReturned() {
        final MessageListTester<MessageList> list_ = test(view.list);
        Assertions.assertIterableEquals(Arrays.asList(view.two, view.three),
                list_.getMessagesAfter(LocalDateTime.now().withHour(11)
                        .toInstant(ZoneOffset.UTC)));
        Assertions.assertIterableEquals(Arrays.asList(view.three),
                list_.getMessagesAfter(LocalDateTime.now().withHour(12)
                        .toInstant(ZoneOffset.UTC)));
    }

    @Test
    void getMessageUpToTime_messagesBeforeAreReturned() {
        final MessageListTester<MessageList> list_ = test(view.list);
        Assertions.assertIterableEquals(Arrays.asList(view.one, view.two),
                list_.getMessagesBefore(LocalDateTime.now().withHour(13)
                        .toInstant(ZoneOffset.UTC)));
        Assertions.assertIterableEquals(Arrays.asList(view.one),
                list_.getMessagesBefore(LocalDateTime.now().withHour(11)
                        .toInstant(ZoneOffset.UTC)));
    }

    @Test
    void getMessageBetweenTime_matchingMessagesReturned() {
        Assertions.assertIterableEquals(Arrays.asList(view.two),
                test(view.list).getMessages(
                        LocalDateTime.now().withHour(1)
                                .toInstant(ZoneOffset.UTC),
                        LocalDateTime.now().withHour(13)
                                .toInstant(ZoneOffset.UTC)));
    }

    @Test
    void getMessageForUser_messagesForUserReturned() {
        final MessageListTester<MessageList> list_ = test(view.list);
        Assertions.assertIterableEquals(Arrays.asList(view.one, view.three),
                list_.getMessages("Joe"));
        Assertions.assertIterableEquals(Arrays.asList(view.two),
                list_.getMessages("Jane"));

        Assertions.assertTrue(list_.getMessages(null).isEmpty());

        final MessageListItem nullUser = new MessageListItem("hi");
        addItem(view.list, nullUser);

        Assertions.assertIterableEquals(Arrays.asList(nullUser),
                list_.getMessages(null));
    }

    /**
     * Add a new message item to the MessageList.
     *
     * @param item
     *            item to add
     */
    private void addItem(MessageList list, MessageListItem item) {

        final ArrayList<MessageListItem> messageListItems = new ArrayList<>(
                list.getItems());
        messageListItems.add(item);
        list.setItems(messageListItems);
    }
}
