/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
