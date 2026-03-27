/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.testbench.unit.Tests;

@Tests(Emphasis.class)
@Deprecated(forRemoval = true, since = "10.1")
public class EmphasisTester extends HtmlClickContainer<Emphasis> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public EmphasisTester(Emphasis component) {
        super(component);
    }
}
