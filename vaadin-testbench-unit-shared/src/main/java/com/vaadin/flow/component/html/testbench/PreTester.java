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

import com.vaadin.flow.component.html.Pre;
import com.vaadin.testbench.unit.Tests;

@Tests(Pre.class)
public class PreTester extends HtmlClickContainer<Pre> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public PreTester(Pre component) {
        super(component);
    }
}
