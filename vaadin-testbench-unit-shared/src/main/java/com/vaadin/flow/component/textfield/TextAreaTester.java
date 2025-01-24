/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.textfield;

import java.util.function.Consumer;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for TextArea components.
 *
 * @param <T>
 *            component type
 */
@Tests(TextArea.class)
public class TextAreaTester<T extends TextArea> extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public TextAreaTester(T component) {
        super(component);
    }

    /**
     * Set the value to the component if it is usable.
     *
     * For a non interactable component an IllegalStateException will be thrown
     * as the end user would not be able to set a value.
     *
     * @param value
     *            value to set
     */
    public void setValue(String value) {
        ensureComponentIsUsable();
        getComponent().setValue(value);
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
