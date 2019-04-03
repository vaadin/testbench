package com.vaadin.flow.component.notification.testbench.test;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(NotificationView.NAV)
@Theme(Lumo.class)
public class NotificationView extends AbstractView {

    public static final String TEXT = "text";
    public static final String NOTEXT = "notext";
    public static final String COMPONENTS = "components";
    public static final String NAV = "Notification";

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Notification notification = Notification.show("", 20000,
                Position.TOP_CENTER);
        notification.setId(NOTEXT);
        add(notification);

        Notification notification2 = Notification.show("Some text", 20000,
                Position.TOP_CENTER);
        notification2.setId(TEXT);
        notification2.getElement().addEventListener("click", e -> {
            notification2.close();
        });
        add(notification2);

        Notification withComponents = new Notification();
        withComponents.setId(COMPONENTS);
        Button hello = new Button("Hello", e -> {
            log("Hello in notification clicked");
        });
        hello.setId("hello");
        withComponents.add(hello);
        Button close = new Button("Close", e -> {
            withComponents.close();
        });
        close.setId("close");
        withComponents.add(close);
        withComponents.setDuration(0);
        withComponents.open();
    }

}
