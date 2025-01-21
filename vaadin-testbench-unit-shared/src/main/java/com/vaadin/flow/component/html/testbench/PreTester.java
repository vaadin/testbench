/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.Pre;
import com.vaadin.testbench.unit.Tests;

@Tests(Pre.class)
public class PreTester extends HtmlClickContainer<Pre> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public PreTester(Pre component) {
        super(component);
    }
}
