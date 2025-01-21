/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.testbench.unit.Tests;

@Tests(NativeButton.class)
public class NativeButtonTester extends HtmlClickContainer<NativeButton> {
    /**
     * > Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NativeButtonTester(NativeButton component) {
        super(component);
    }

}
