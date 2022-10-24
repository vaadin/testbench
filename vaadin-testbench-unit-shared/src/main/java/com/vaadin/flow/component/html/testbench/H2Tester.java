/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
