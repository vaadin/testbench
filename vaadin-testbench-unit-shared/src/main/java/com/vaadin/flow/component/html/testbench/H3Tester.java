/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.H3;
import com.vaadin.testbench.unit.Tests;

@Tests(H3.class)
public class H3Tester extends HtmlClickContainer<H3> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public H3Tester(H3 component) {
        super(component);
    }
}
