/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.timepicker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.browserless.Tests;

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

        try {
            if (isInvalid(time)) {
                throw new IllegalArgumentException(
                        "Given time is not a valid value");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        setValueAsUser(time);
    }

    private boolean isInvalid(LocalTime date)
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
