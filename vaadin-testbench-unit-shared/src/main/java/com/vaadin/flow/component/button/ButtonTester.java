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
 * 
 * @deprecated Replace the vaadin-testbench-unit dependency with
 *             browserless-test-junit6 and use the corresponding class from the
 *             com.vaadin.browserless package instead. This class will be
 *             removed in a future version.
 */
@Tests(Button.class)
@Deprecated(forRemoval = true, since = "10.1")
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
