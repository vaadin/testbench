/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.datepicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for the DatePicker components.
 *
 * @param <T>
 *            component type
 
  * @deprecated Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.
  */
@Tests(DatePicker.class)
@Deprecated(forRemoval = true, since = "10.1")
public class DatePickerTester<T extends DatePicker> extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DatePickerTester(T component) {
        super(component);
    }

    /**
     * Set the given date as value to the component.
     * <p/>
     * Will throw if the component is not enabled or the value is invalid.
     *
     * @param date
     *            date to set to the component
     * @throws IllegalArgumentException
     *             if the given value is not valid
     */
    public void setValue(LocalDate date) {
        ensureComponentIsUsable();

        try {
            if (isInvalid(date)) {
                throw new IllegalArgumentException(
                        "Given date is not a valid value");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        setValueAsUser(date);
    }

    private boolean isInvalid(LocalDate date)
            throws InvocationTargetException, IllegalAccessException {
        try {
            // Vaadin 24.4
            final Method isInvalid = getMethod("isInvalid", LocalDate.class);
            return (boolean) isInvalid.invoke(getComponent(), date);
        } catch (RuntimeException ex) {
            if (!(ex.getCause() instanceof NoSuchMethodException)) {
                throw ex;
            }
        }
        // Vaadin 24.5+
        return getComponent().getDefaultValidator().apply(date, null).isError();
    }
}
