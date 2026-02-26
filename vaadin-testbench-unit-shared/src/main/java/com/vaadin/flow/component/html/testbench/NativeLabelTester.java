/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.testbench.unit.Tests;

@Tests(NativeLabel.class)
@Deprecated(forRemoval = true, since = "10.1")
public class NativeLabelTester extends HtmlContainerTester<NativeLabel> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NativeLabelTester(NativeLabel component) {
        super(component);
    }
}
