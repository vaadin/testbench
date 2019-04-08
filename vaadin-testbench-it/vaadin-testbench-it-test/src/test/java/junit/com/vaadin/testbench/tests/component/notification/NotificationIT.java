package junit.com.vaadin.testbench.tests.component.notification;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.notification.testbench.NotificationElement;
import com.vaadin.flow.component.notification.testbench.test.NotificationView;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.component.common.AbstractIT;
import junit.com.vaadin.testbench.tests.testui.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static com.vaadin.flow.component.notification.testbench.test.NotificationView.NAV;

@VaadinTest
public class NotificationIT extends AbstractIT {

    @VaadinTest(navigateAsString = NAV)
    public void getText(GenericTestPageObject po) throws Exception {
        final NotificationElement noText = po.$(NotificationElement.class).id(NotificationView.NOTEXT);
        final NotificationElement text = po.$(NotificationElement.class).id(NotificationView.TEXT);

        Assertions.assertEquals("", noText.getText());
        Assertions.assertEquals("Some text", text.getText());
    }

    @VaadinTest(navigateAsString = NAV)
    public void isOpen(GenericTestPageObject po) throws Exception {
        final NotificationElement noText = po.$(NotificationElement.class).id(NotificationView.NOTEXT);
        final NotificationElement text = po.$(NotificationElement.class).id(NotificationView.TEXT);
        final NotificationElement components = po.$(NotificationElement.class).id(NotificationView.COMPONENTS);

        Assertions.assertTrue(noText.isOpen());
        Assertions.assertTrue(text.isOpen());
        Assertions.assertTrue(components.isOpen());
        components.$(ButtonElement.class).id("close").click();
        Assertions.assertFalse(components.isOpen());
    }

    @VaadinTest(navigateAsString = NAV)
    public void findAllNotifications(GenericTestPageObject po) throws Exception {
        List<NotificationElement> notifications = po.$(NotificationElement.class).all();
        Assertions.assertEquals(3, notifications.size());
    }

    @VaadinTest(navigateAsString = NAV)
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
