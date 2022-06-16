/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
 */
@Tests(DatePicker.class)
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

        final Method isInvalid = getMethod("isInvalid", LocalDate.class);
        try {
            if ((boolean) isInvalid.invoke(getComponent(), date)) {
                throw new IllegalArgumentException(
                        "Given date is not a valid value");
            }

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        getComponent().setValue(date);
    }
}
