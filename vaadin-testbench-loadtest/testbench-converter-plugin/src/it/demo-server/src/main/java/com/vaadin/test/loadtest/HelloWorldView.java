/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.test.loadtest;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Hello World")
@Route("")
public class HelloWorldView extends Div {

    public HelloWorldView() {
        Input name = new Input();
        name.setId("name");
        NativeLabel nameLabel = new NativeLabel("Your name");
        nameLabel.setFor(name);

        Span greeting = new Span();
        greeting.setId("greeting");

        NativeButton sayHello = new NativeButton("Say hello",
                e -> greeting.setText("Hello " + name.getValue()));
        sayHello.setId("say-hello");

        getStyle().set("padding", "1em");
        add(nameLabel, name, sayHello, greeting);
    }
}
