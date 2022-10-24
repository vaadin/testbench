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
