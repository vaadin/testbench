/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component;

import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.TestComponent;
import com.vaadin.testbench.unit.Wraps;

@Wraps(TestComponent.class)
public class TestWrap<T extends TestComponent> extends ComponentWrap<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public TestWrap(T component) {
        super(component);
    }
}
