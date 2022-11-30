/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route("PerformanceView")
public class PerformanceView extends Div {

    public PerformanceView() {
        NativeButton button = new NativeButton("1s delay", e -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
            }
            add(new Span("Done sleeping"));
        });
        add(button);
    }

}
