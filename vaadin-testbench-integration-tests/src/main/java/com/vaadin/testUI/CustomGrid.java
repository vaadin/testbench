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

public class CustomGrid extends Div {
    public CustomGrid() {
        getElement().setAttribute("hierarchical", false);
        getElement().setAttribute("grid-element", true);
        setText(getClass().getSimpleName());
    }
}
