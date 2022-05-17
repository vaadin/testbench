/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import com.vaadin.flow.component.textfield.GeneratedVaadinTextField;
import com.vaadin.testbench.unit.internal.PrettyPrintTreeKt;

/**
 * Test wrapper for TextField components.
 *
 * @param <T>
 *         component type
 * @param <V>
 *         value type
 */
public class TextFieldWrap<T extends GeneratedVaadinTextField<T, V>, V>
        extends ComponentWrap<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *         target component
     */
    public TextFieldWrap(T component) {
        super(component);
    }

    /**
     * Set the value to the component if it is usable.
     * <p>
     * For a non interactable component an IllegalStateException will be thrown
     * as the end user would not be able to set a value.
     *
     * @param value
     *         value to set
     */
    public void setValue(V value) {
        if (!isUsable()) {
            throw new IllegalStateException(
                    PrettyPrintTreeKt.toPrettyString(getComponent())
                            + " is not usable");
        }

        getComponent().setValue(value);
    }

    @Override
    public boolean isUsable() {
        // TextFields can be read only so the usable check needs extending
        return super.isUsable() && !getComponent().isReadOnly();
    }
}
