/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.textfield;

import java.util.Objects;

import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for NumberField components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Wraps(fqn = { "com.vaadin.flow.component.textfield.IntegerField",
        "com.vaadin.flow.component.textfield.NumberField" })
public class NumberFieldWrap<T extends AbstractNumberField<T, V>, V extends Number>
        extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NumberFieldWrap(T component) {
        super(component);
    }

    /**
     * Set the given value for the component.
     * <p/>
     * Throws if component is not usable or the value is invalid.
     *
     * @param value
     *            value to set
     * @throws IllegalArgumentException
     *             if given value is not valid
     */
    public void setValue(V value) {
        ensureComponentIsUsable();
        if (!isValid(value)) {
            throw new IllegalArgumentException(
                    "Given value '" + value + "' is not valid");
        }
        getComponent().setValue(value);
    }

    private boolean isValid(V value) {
        final boolean isRequiredButEmpty = getComponent().isRequiredBoolean()
                && Objects.equals(getComponent().getEmptyValue(), value);
        final boolean isGreaterThanMax = value != null
                && value.doubleValue() > getComponent().getMaxDouble();
        final boolean isSmallerThanMin = value != null
                && value.doubleValue() < getComponent().getMinDouble();

        return !(isRequiredButEmpty || isGreaterThanMax || isSmallerThanMin);
        // TODO: Can we access the Generic isValidByStep
        // || !isValidByStep(value);
    }

    // TODO: support stepUp/stepDown if controls are visible.

    @Override
    public boolean isUsable() {
        // TextFields can be read only so the usable check needs extending
        return super.isUsable() && !getComponent().isReadOnly();
    }
}
