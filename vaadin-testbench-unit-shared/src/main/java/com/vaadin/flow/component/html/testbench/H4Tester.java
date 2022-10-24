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
