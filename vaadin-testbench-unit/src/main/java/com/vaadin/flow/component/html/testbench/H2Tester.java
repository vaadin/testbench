/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.H2;
import com.vaadin.testbench.unit.Tests;

@Tests(H2.class)
public class H2Tester extends HtmlClickContainer<H2> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public H2Tester(H2 component) {
        super(component);
    }
}
