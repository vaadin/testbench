/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.test.loadtest;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Hello World")
@Route("")
public class HelloWorldView extends HorizontalLayout {

    public HelloWorldView() {
        TextField name = new TextField("Your name");
        Button sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> Notification.show("Hello " + name.getValue()));
        setMargin(true);
        add(name, sayHello);
    }
}
