/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.router.Route;

@Route("ElementQueryView")
public class ElementQueryView extends Div {

    public ElementQueryView() {
        setId("element-query-view");
        getElement().getStyle().set("line-height", "12px");
        for (int i = 0; i < 10; i++) {
            NativeButton button = new NativeButton("Button " + i);
            button.getElement().getStyle().set("margin", "0");
            if (i == 2) {
                button.getElement().setAttribute("disabled", true);
            }
            DomListenerRegistration reg = button.getElement()
                    .addEventListener("custom123", e -> {
                        Div div = new Div();
                        div.setId("msg");
                        e.getType();
                        div.setText("Event on " + button.getText()
                                + " bubbles: "
                                + e.getEventData().getBoolean("event.bubbles"));
                        add(div);
                    });
            reg.addEventData("event.bubbles");
            add(new Div(button));
            if (i == 5) {
                button.getElement().setAttribute("boolean", true);
                button.getElement().setAttribute("string", "value");
            }
        }
    }

}
