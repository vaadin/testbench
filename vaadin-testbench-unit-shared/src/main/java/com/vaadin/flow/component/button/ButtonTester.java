/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.button;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 *
 * Tester for Button components.
 *
 * @param <T>
 *            component type
 */
@Tests(Button.class)
public class ButtonTester<T extends Button> extends ComponentTester<T> {
    /**
     * Wrap given button for testing.
     *
     * @param component
     *            target button
     */
    public ButtonTester(T component) {
        super(component);
    }
}
