/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.html.Input;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

@Tests(Input.class)
@Deprecated(forRemoval = true, since = "10.1")
public class InputTester extends ComponentTester<Input> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public InputTester(Input component) {
        super(component);
    }

    /**
     * Set the value to the component if it is usable.
     * <p>
     * For a non interactable component an IllegalStateException will be thrown
     * as the end user would not be able to set a value.
     *
     * @param value
     *            value to set
     */
    public void setValue(String value) {
        ensureComponentIsUsable();

        if (value == null && getComponent().getEmptyValue() != null) {
            throw new IllegalArgumentException(
                    "Field doesn't allow null values");
        }

        setValueAsUser(value);
    }

    /**
     * Get the current value of the component.
     *
     * @return current component value
     * @throws IllegalStateException
     *             if component not visible
     */
    public String getValue() {
        ensureVisible();
        return getComponent().getValue();
    }

    /**
     * Resets the value to the empty value of the component.
     */
    public void clear() {
        ensureComponentIsUsable();

        setValue(getComponent().getEmptyValue());
    }
}
