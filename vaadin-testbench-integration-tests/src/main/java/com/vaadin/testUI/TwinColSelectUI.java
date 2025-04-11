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

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TwinColSelectUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(false);
        vl.setMargin(false);
        setContent(vl);

        TwinColSelect<String> tcSelect = new TwinColSelect<String>("Select",
                Arrays.asList("One", "Two"));
        vl.addComponent(tcSelect);

        vl.setSizeUndefined();
    }
}
