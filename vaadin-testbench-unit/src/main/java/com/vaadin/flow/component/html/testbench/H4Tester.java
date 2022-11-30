/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.H4;
import com.vaadin.testbench.unit.Tests;

@Tests(H4.class)
public class H4Tester extends HtmlClickContainer<H4> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public H4Tester(H4 component) {
        super(component);
    }
}
