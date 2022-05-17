/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.notification.Notification;

class NotificationWrapTest extends UIUnitTest {

    @Override
    protected String scanPackage() {
        return "com.example";
    }

    @Test
    void notOpenedNotification_isNotUsable() {
        Notification notification = new Notification("Not Opened");

        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);

        Assertions.assertFalse(notification_.isUsable(),
                "Not opened Notification shouldn't be usable");
    }

    @Test
    void openedNotification_isUsable() {
        String notificationText = "Opened notification";
        Notification notification = Notification.show(notificationText);
        ComponentWrap.flushChanges();

        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);
        Assertions.assertTrue(notification_.isUsable(),
                "Opened Notification should be usable");
    }

    @Test
    void closedNotification_notFlushed_isNotUsable() {
        String notificationText = "Opened notification";
        Notification notification = Notification.show(notificationText);
        ComponentWrap.flushChanges();
        notification.close();

        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);
        Assertions.assertFalse(notification_.isUsable(),
                "Closed Notification should not be usable");
    }

    @Test
    void close_displayedNotification_isNotUsable() {
        Notification notification = Notification.show("Some text");
        ComponentWrap.flushChanges();

        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);
        notification_.close();

        Assertions.assertFalse(notification_.isUsable(),
                "Closed Notification should be usable");
    }

    @Test
    void close_notOpenedNotification_throws() {
        Notification notification = new Notification("Some text");
        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);

        Assertions.assertThrows(IllegalStateException.class,
                notification_::close,
                "Closing not opened notification should fail");
    }

    @Test
    void close_closedNotification_throws() {
        Notification notification = Notification.show("Some text");
        ComponentWrap.flushChanges();

        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);
        notification_.close();
        Assertions.assertThrows(IllegalStateException.class,
                notification_::close,
                "Closing already closed notification should fail");
    }

    @Test
    void getText_displayedNotification_textContentAvailable() {
        String notificationText = "Opened notification";
        Notification notification = Notification.show(notificationText);
        ComponentWrap.flushChanges();

        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);

        Assertions.assertEquals(notificationText, notification_.getText());
    }

    @Test
    void getText_notOpenedNotification_throws() {
        Notification notification = new Notification("Some text");

        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);

        Assertions.assertThrows(IllegalStateException.class,
                notification_::getText,
                "Getting text from not opened notification should fail");
    }

    @Test
    void getText_closedNotification_throws() {
        Notification notification = Notification.show("Some text");
        notification.close();
        ComponentWrap.flushChanges();

        NotificationWrap<?> notification_ = $(NotificationWrap.class,
                notification);

        Assertions.assertThrows(IllegalStateException.class,
                notification_::getText,
                "Getting text from closed notification should fail");
    }

}
