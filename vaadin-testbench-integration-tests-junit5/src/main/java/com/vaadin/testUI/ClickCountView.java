/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.router.Route;

@Route("ClickCountView")
public class ClickCountView extends Div {

    public ClickCountView() {
        NativeButton button = new NativeButton("Click me");
        button.setId("click-target");

        Div log = new Div();
        log.setId("click-count");

        button.getElement().addEventListener("click", e -> {
            log.setText(String
                    .valueOf(e.getEventData().get("event.detail").intValue()));
        }).addEventData("event.detail");

        add(button, log);
    }
}
