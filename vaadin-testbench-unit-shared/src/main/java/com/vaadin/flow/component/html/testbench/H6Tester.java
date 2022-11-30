/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.H6;
import com.vaadin.testbench.unit.Tests;

@Tests(H6.class)
public class H6Tester extends HtmlClickContainer<H6> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public H6Tester(H6 component) {
        super(component);
    }
}
