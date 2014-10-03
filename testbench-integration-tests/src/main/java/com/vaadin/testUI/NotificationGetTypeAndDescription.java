package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class NotificationGetTypeAndDescription extends AbstractTestUI {
    @WebServlet(value = { "/VAADIN/*", "/NotificationGetTypeAndDescription/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = NotificationGetTypeAndDescription.class)
    public static class Servlet extends VaadinServlet {
    }

    private final static Type[] types = { Type.WARNING_MESSAGE,
            Type.ERROR_MESSAGE, Type.HUMANIZED_MESSAGE, Type.TRAY_NOTIFICATION };
    public final static String[] type_names = { "warning", "error",
            "humanized", "tray_notification" };
    public final static String[] captions = { "warningC", "errorC",
            "humanizedC", "tray_notificationC" };
    public final static String[] descriptions = { "warning", "error",
            "humanized", "tray_notification" };

    @Override
    protected void setup(VaadinRequest request) {
        for (int i = 0; i < types.length; i++) {
            Button btn = new Button();
            btn.setId("button" + i);
            btn.setCaption(type_names[i]);
            btn.addClickListener(new CounterClickListener(i));
            addComponent(btn);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Test getType and getDescription methods of NotificationElement";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13768;
    }

    private class CounterClickListener implements ClickListener {
        int index;

        public CounterClickListener(int i) {
            index = i;
        }

        public void buttonClick(ClickEvent event) {
            Notification.show(captions[index], descriptions[index],
                    types[index]);
        }

    }
}
