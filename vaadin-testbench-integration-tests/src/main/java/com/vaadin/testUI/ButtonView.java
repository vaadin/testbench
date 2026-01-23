/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("button-view")
public class ButtonView extends VerticalLayout {

    public ButtonView() {
        NativeButton button = new NativeButton("Click me");
        button.setId("test-button");
        button.addSingleClickListener(event -> {
            Span newSpan = new Span(
                    "Button single clicked: " + event.getClickCount());
            newSpan.setId("single-click");
            add(newSpan);
        });
        button.addDoubleClickListener(event -> {
            Span newSpan = new Span(
                    "Button double clicked: " + event.getClickCount());
            newSpan.setId("double-click");
            add(newSpan);
        });
        button.addFocusListener(event -> {
            Span newSpan = new Span("Button focused");
            newSpan.setId("focus-event");
            add(newSpan);
        });
        add(button);
    }

}
