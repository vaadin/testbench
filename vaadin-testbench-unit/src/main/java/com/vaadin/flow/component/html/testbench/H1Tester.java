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
