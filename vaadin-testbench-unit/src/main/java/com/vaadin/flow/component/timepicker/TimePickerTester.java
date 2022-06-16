/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.timepicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalTime;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for TimePicker components.
 *
 * @param <T>
 *            component type
 */
@Tests(TimePicker.class)
public class TimePickerTester<T extends TimePicker> extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public TimePickerTester(T component) {
        super(component);
    }

    /**
     * Set the time to the component.
     * <p/>
     * Will throw if component is not enabled or value is not valid.
     *
     * @param time
     *            time to set to component
     * @throws IllegalArgumentException
     *             if value is invalid
     */
    public void setValue(LocalTime time) {
        ensureComponentIsUsable();

        final Method isInvalid = getMethod("isInvalid", LocalTime.class);
        try {
            if ((boolean) isInvalid.invoke(getComponent(), time)) {
                throw new IllegalArgumentException(
                        "Given time is not a valid value");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        getComponent().setValue(time);
    }

}
