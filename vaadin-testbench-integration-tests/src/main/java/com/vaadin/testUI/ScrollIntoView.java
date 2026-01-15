/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Complex UI to reproduce case when element was not scrolled into view before
 * interacting with it
 *
 */
public class ScrollIntoView extends UI {

    @Override
    protected void init(VaadinRequest request) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();

        VerticalLayout vl = new VerticalLayout();
        vl.setHeight("200px");
        vl.addStyleName("v-scrollable");

        VerticalLayout vl2 = new VerticalLayout();
        vl2.setSpacing(false);
        vl2.setMargin(false);

        for (int i = 0; i < 30; i++) {
            Button button = new Button("Button " + i);
            vl2.addComponent(button);
        }

        vl.addComponent(vl2);
        hl.addComponent(vl);
        setContent(hl);
    }

}
