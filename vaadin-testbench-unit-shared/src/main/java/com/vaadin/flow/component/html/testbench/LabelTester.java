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

import com.vaadin.flow.component.html.Label;
import com.vaadin.testbench.unit.Tests;

@Tests(Label.class)
public class LabelTester extends HtmlContainerTester<Label> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public LabelTester(Label component) {
        super(component);
    }
}
