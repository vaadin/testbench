/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.datetimepicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for DateTimePicker components.
 *
 * @param <T>
 *            component type
 */
@Tests(DateTimePicker.class)
public class DateTimePickerTester<T extends DateTimePicker>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DateTimePickerTester(T component) {
        super(component);
    }

    /**
     * Set the date to the component.
     * <p/>
     * Will throw if component is not enabled or value is not valid.
     *
     * @param dateTime
     *            date time to set to component
     * @throws IllegalArgumentException
     *             if value is invalid
     */
    public void setValue(LocalDateTime dateTime) {
        ensureComponentIsUsable();

        final Method isInvalid = getMethod("isInvalid", LocalDateTime.class);
        try {
            if ((boolean) isInvalid.invoke(getComponent(), dateTime)) {
                throw new IllegalArgumentException(
                        "Given date is not a valid value");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        getComponent().setValue(dateTime);
    }

}
