/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.datetimepicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for DateTimePicker components.
 *
 * @param <T>
 *            component type
 */
@Wraps(DateTimePicker.class)
public class DateTimePickerWrap<T extends DateTimePicker>
        extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DateTimePickerWrap(T component) {
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
