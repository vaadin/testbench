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
import com.vaadin.testbench.unit.TestComponentForConcreteWrapper;
import com.vaadin.testbench.unit.Wraps;

@Wraps(TestComponentForConcreteWrapper.class)
public class NonGenericTestWrap
        extends ComponentWrap<TestComponentForConcreteWrapper> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NonGenericTestWrap(TestComponentForConcreteWrapper component) {
        super(component);
    }
}
