/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.Hr;
import com.vaadin.testbench.unit.Tests;

@Tests(Hr.class)
public class HrTester extends HtmlComponentTester<Hr> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public HrTester(Hr component) {
        super(component);
    }
}
