/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.notification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.TestBenchUnit;
import com.vaadin.testbench.unit.TestBenchUnit;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class NotificationWrapTest extends TestBenchUnit {

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(NotificationView.class);
        navigate(NotificationView.class);
    }

    @Test
    void notOpenedNotification_isNotUsable() {
        Notification notification = new Notification("Not Opened");

        Assertions.assertFalse(wrap(notification).isUsable(),
                "Not opened Notification shouldn't be usable");
    }

    @Test
    void openedNotification_isUsable() {
        String notificationText = "Opened notification";
        Notification notification = Notification.show(notificationText);
        roundTrip();

        Assertions.assertTrue(wrap(notification).isUsable(),
                "Opened Notification should be usable");
    }

    @Test
    void disabledNotification_isUsable() {
        String notificationText = "Opened disabled notification";
        Notification notification = Notification.show(notificationText);
        notification.setEnabled(false);
        roundTrip();

        Assertions.assertTrue(wrap(notification).isUsable(),
                "Disabled Notification should be usable");
    }

    @Test
    void hiddenNotification_isNotUsable() {
        String notificationText = "Opened hidden notification";
        Notification notification = Notification.show(notificationText);
        notification.setVisible(false);
        roundTrip();

        Assertions.assertFalse(wrap(notification).isUsable(),
                "Hidden Notification should not be usable");
    }

    @Test
    void closedNotification_notFlushed_isNotUsable() {
        String notificationText = "Opened notification";
        Notification notification = Notification.show(notificationText);
        roundTrip();
        notification.close();

        Assertions.assertFalse(wrap(notification).isUsable(),
                "Closed Notification should not be usable");
    }

    @Test
    void autoClose_displayedNotification_isNotUsable() {
        Notification notification = Notification.show("Some text");
        roundTrip();

        NotificationWrap<?> notification_ = wrap(NotificationWrap.class,
                notification);
        notification_.autoClose();

        Assertions.assertFalse(notification_.isUsable(),
                "Notification should not be usable after auto-close");
    }

    @Test
    void autoClose_notOpenedNotification_throws() {
        Notification notification = new Notification("Some text");

        Assertions.assertThrows(IllegalStateException.class,
                wrap(notification)::autoClose,
                "Auto-close not opened notification should fail");
    }

    @Test
    void autoClose_closedNotification_throws() {
        Notification notification = Notification.show("Some text");
        roundTrip();

        NotificationWrap<?> notification_ = wrap(NotificationWrap.class,
                notification);
        notification_.autoClose();
        Assertions.assertThrows(IllegalStateException.class,
                notification_::autoClose,
                "Auto-close already closed notification should fail");
    }

    @Test
    void autoClose_notificationWithDisabledAutoClose_throws() {
        Notification notification = Notification.show("Some text");
        notification.setDuration(0);
        roundTrip();

        Assertions.assertThrows(IllegalStateException.class,
                wrap(notification)::autoClose,
                "Auto-close notification with auto-close disabled should fail");
    }

    @Test
    void getText_displayedNotification_textContentAvailable() {
        String notificationText = "Opened notification";
        Notification notification = Notification.show(notificationText);
        roundTrip();

        Assertions.assertEquals(notificationText, wrap(notification).getText());
    }

    @Test
    void getText_notOpenedNotification_throws() {
        Notification notification = new Notification("Some text");

        Assertions.assertThrows(IllegalStateException.class,
                wrap(notification)::getText,
                "Getting text from not opened notification should fail");
    }

    @Test
    void getText_closedNotification_throws() {
        Notification notification = Notification.show("Some text");
        notification.close();
        roundTrip();

        Assertions.assertThrows(IllegalStateException.class,
                wrap(notification)::getText,
                "Getting text from closed notification should fail");
    }

}
