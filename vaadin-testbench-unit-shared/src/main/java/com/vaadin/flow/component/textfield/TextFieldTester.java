/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.textfield;

import java.util.function.Consumer;

import com.vaadin.flow.component.shared.HasClearButton;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for TextField components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Tests({ TextField.class, PasswordField.class, EmailField.class,
        BigDecimalField.class })
public class TextFieldTester<T extends GeneratedVaadinTextField<T, V>, V>
        extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public TextFieldTester(T component) {
        super(component);
    }

    /**
     * Set the value to the component if it is usable.
     *
     * @param value
     *            value to set
     */
    public void setValue(V value) {
        ensureComponentIsUsable();

        if (value == null && getComponent().getEmptyValue() != null) {
            throw new IllegalArgumentException(
                    "Field doesn't allow null values");
        }

        getComponent().setValue(value);
    }

    /**
     * Resets the value to the empty one, as when clicking on component clear
     * button on the browser.
     *
     * An {@link IllegalStateException} is thrown if the clear button is not
     * visible.
     *
     * @throws IllegalStateException
     *             if the text field is not usable or the clear button is not
     *             visible.
     */
    public void clear() {
        ensureComponentIsUsable();

        if (getComponent() instanceof HasClearButton
                && ((HasClearButton) getComponent()).isClearButtonVisible()) {
            setValue(getComponent().getEmptyValue());
        } else {
            throw new IllegalStateException("Clear button is not visible");
        }
    }

    private boolean hasValidation() {
        return getValidationSupport() != null;
    }

    private TextFieldValidationSupport getValidationSupport() {
        try {
            return (TextFieldValidationSupport) getField("validationSupport")
                    .get(getComponent());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            // NO-OP Field didn't exist for given GeneratedVaadinTextField
            // implementation
        }
        return null;
    }

    @Override
    public boolean isUsable() {
        // TextFields can be read only so the usable check needs extending
        return super.isUsable() && !getComponent().isReadOnly();
    }

    @Override
    protected void notUsableReasons(Consumer<String> collector) {
        super.notUsableReasons(collector);
        if (getComponent().isReadOnly()) {
            collector.accept("read only");
        }
    }
}
