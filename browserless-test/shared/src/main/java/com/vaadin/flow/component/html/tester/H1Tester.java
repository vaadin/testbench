/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.tester;

import com.vaadin.flow.component.html.H1;
import com.vaadin.testbench.unit.Tests;

@Tests(H1.class)
public class H1Tester extends HtmlClickContainer<H1> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public H1Tester(H1 component) {
        super(component);
    }

}
