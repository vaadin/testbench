/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class ElementAttributeUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(false);
        vl.setMargin(false);
        setContent(vl);

        TextField regularField = new TextField("Regular field");
        regularField.setId("regularField");
        vl.addComponent(regularField);

        TextField readOnlyField = new TextField("Read-only field");
        readOnlyField.setId("readOnlyField");
        readOnlyField.setReadOnly(true);
        vl.addComponent(readOnlyField);

        Button button = new Button("Dummy button");
        button.setId("button");
        vl.addComponent(button);

        vl.setSizeUndefined();
    }
}
