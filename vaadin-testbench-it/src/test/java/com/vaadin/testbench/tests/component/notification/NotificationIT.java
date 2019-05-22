package com.vaadin.testbench.tests.component.notification;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.notification.testbench.test.NotificationView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.ui.GenericTestPageObject;
import com.vaadin.testbench.tests.component.common.AbstractIT;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static com.vaadin.flow.component.notification.testbench.test.NotificationView.NAV;

@VaadinTest(navigateTo = NAV)
public class NotificationIT extends AbstractIT {

    @VaadinTest
    public void getText(GenericTestPageObject po) {
        final NotificationElement noText = po.$(NotificationElement.class).id(NotificationView.NOTEXT);
        final NotificationElement text = po.$(NotificationElement.class).id(NotificationView.TEXT);

        Assertions.assertEquals("", noText.getText());
        Assertions.assertEquals("Some text", text.getText());
    }

    @VaadinTest
    public void isOpen(GenericTestPageObject po) {
        final NotificationElement noText = po.$(NotificationElement.class).id(NotificationView.NOTEXT);
        final NotificationElement text = po.$(NotificationElement.class).id(NotificationView.TEXT);
        final NotificationElement components = po.$(NotificationElement.class).id(NotificationView.COMPONENTS);

        Assertions.assertTrue(noText.isOpen());
        Assertions.assertTrue(text.isOpen());
        Assertions.assertTrue(components.isOpen());
        components.$(ButtonElement.class).id("close").click();
        Assertions.assertFalse(components.isOpen());
    }

    @VaadinTest
    public void findAllNotifications(GenericTestPageObject po) {
        List<NotificationElement> notifications = po.$(NotificationElement.class).all();
        Assertions.assertEquals(3, notifications.size());
    }

    @VaadinTest
    public void componentInsideNotification(GenericTestPageObject po) {
        final NotificationElement components = po.$(NotificationElement.class).id(NotificationView.COMPONENTS);

        ButtonElement hello = components.$(ButtonElement.class).id("hello");
        ButtonElement close = components.$(ButtonElement.class).id("close");
        hello.click();
        Assertions.assertEquals("1. Hello in notification clicked", getLogRow(po, 0));
        close.click();
        Assertions.assertFalse(components.isOpen());
    }

}
