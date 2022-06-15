/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.html.testbench;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.shared.HasClearButton;
import com.vaadin.flow.component.textfield.TextFieldTester;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

@Tests(Input.class)
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

        getComponent().setValue(value);
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
