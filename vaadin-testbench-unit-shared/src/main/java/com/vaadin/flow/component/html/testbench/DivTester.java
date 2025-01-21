/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.Div;
import com.vaadin.testbench.unit.Tests;

@Tests(Div.class)
public class DivTester extends HtmlClickContainer<Div> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DivTester(Div component) {
        super(component);
    }
}
