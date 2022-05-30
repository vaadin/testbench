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

import org.slf4j.LoggerFactory;

import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for TextField components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Wraps({ TextField.class, PasswordField.class, EmailField.class,
        BigDecimalField.class })
public class TextFieldWrap<T extends GeneratedVaadinTextField<T, V>, V>
        extends ComponentWrap<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
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
     *            value to set
     */
    public void setValue(V value) {
        ensureComponentIsUsable();

        if (hasValidation()
                && getValidationSupport().isInvalid(value.toString())) {
            if (getComponent().isPreventInvalidInputBoolean()) {
                throw new IllegalArgumentException(
                        "Given value doesn't pass field value validation. Check validation settings for field.");
            }
            LoggerFactory.getLogger(TextFieldWrap.class).warn(
                    "Gave invalid input, but value set as invalid input is not prevented.");
        }

        getComponent().setValue(value);
    }

    private boolean hasValidation() {
        return getValidationSupport() != null;
    }

    private TextFieldValidationSupport getValidationSupport() {
        try {
            return (TextFieldValidationSupport) getField("validationSupport")
                    .get(getComponent());
        } catch (IllegalAccessException e) {
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
}
