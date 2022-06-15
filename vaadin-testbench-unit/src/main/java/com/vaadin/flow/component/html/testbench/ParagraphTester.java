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

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.testbench.unit.Tests;

@Tests(Paragraph.class)
public class ParagraphTester extends HtmlClickContainer<Paragraph> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public ParagraphTester(Paragraph component) {
        super(component);
    }
}
