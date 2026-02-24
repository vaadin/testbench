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
package com.vaadin.flow.component.html.tester;

import java.math.BigDecimal;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.browserless.Tests;
import com.vaadin.flow.component.html.RangeInput;

@Tests(RangeInput.class)
public class RangeInputTester extends ComponentTester<RangeInput> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public RangeInputTester(RangeInput component) {
        super(component);
    }

    /**
     * Get the current value of the component.
     *
     * @return current component value
     * @throws IllegalStateException
     *             if component not visible
     */
    public Double getValue() {
        ensureVisible();
        return null;
    }

    /**
     * Set the value to the component if it is usable.
     * <p>
     * For a non interactable component an IllegalStateException will be thrown
     * as the end user would not be able to set a value.
     * <p>
     * </p>
     * The value must be in the component {@literal min - max} range, and should
     * be valid according to the {@literal step} scale factor, otherwise an
     * {@link IllegalArgumentException} is thrown.
     *
     * @param value
     *            value to set
     * @throws IllegalStateException
     *             if component is not interactable
     * @throws IllegalArgumentException
     *             if the value is not valid based on
     *             {@literal min, max and step} settings.
     */
    public void setValue(Double value) {
        ensureComponentIsUsable();
        if (value == null) {
            throw new IllegalArgumentException(
                    "RangeInput doesn't allow null values");
        }
        if (value < getComponent().getMin()) {
            throw new IllegalArgumentException(
                    "Value cannot be less than min. Got " + value
                            + " but min is " + getComponent().getMin());
        }
        if (value > getComponent().getMax()) {
            throw new IllegalArgumentException(
                    "Value cannot be greater than min. Got " + value
                            + " but max is " + getComponent().getMax());
        }

        if (getComponent().getStep() != null
                && (Math.abs(value - getComponent().getValue())
                        % getComponent().getStep() != 0)) {
            throw new IllegalArgumentException(
                    "Value cannot does not match step factory. Got " + value
                            + " but current value is "
                            + getComponent().getValue() + " and step is "
                            + getComponent().getStep());

        }
        setValueAsUser(value);
    }

    /**
     * Increase the value by the amount defined by component {@literal step}.
     * <p>
     * </p>
     * If the {@literal step} is not defined, an {@link IllegalStateException}
     * is thrown. An {@link IllegalArgumentException} is thrown if increase
     * operation exceeds the {@literal max} boundaries.
     *
     * @throws IllegalStateException
     *             if component is not interactable or the step is not defined.
     */
    public void increase() {
        increase(1);
    }

    /**
     * Increases the value by a specified multiple of the component step
     * setting.
     * <p>
     * </p>
     * If the {@literal step} is not defined, an {@link IllegalStateException}
     * is thrown. An {@link IllegalArgumentException} is thrown if increase
     * operation exceeds the {@literal max} boundaries.
     *
     * @param times
     *            The number of times the component step value should be
     *            multiplied before adding to the component value. Must be a
     *            non-negative integer.
     *
     * @throws IllegalArgumentException
     *             If the {@code times} parameter is a negative integer.
     * @throws IllegalStateException
     *             if component is not interactable or the step is not defined.
     */
    public void increase(int times) {
        if (times < 0) {
            throw new IllegalArgumentException(
                    "The 'times' parameter must be a non-negative integer.");
        }
        ensureComponentIsUsable();
        if (getComponent().getStep() == null) {
            throw new IllegalStateException(
                    "Cannot increase value if component step is not defined");
        }
        setValue(getComponent().getValue() + getComponent().getStep() * times);
    }

    /**
     * Decreases the value by the amount defined by component {@literal step}.
     * <p>
     * </p>
     * If the {@literal step} is not defined, an {@link IllegalStateException}
     * is thrown. An {@link IllegalArgumentException} is thrown if increase
     * operation exceeds the {@literal min} boundaries.
     *
     * @throws IllegalStateException
     *             if component is not interactable or the step is not defined.
     */
    public void decrease() {
        decrease(1);
    }

    /**
     * Decreases the value by a specified multiple of the component step
     * setting.
     * <p>
     * </p>
     * If the {@literal step} is not defined, an {@link IllegalStateException}
     * is thrown. An {@link IllegalArgumentException} is thrown if increase
     * operation exceeds the {@literal min} boundaries.
     *
     * @param times
     *            The number of times the component step value should be
     *            multiplied before subtracting from the component value. Must
     *            be a non-negative integer.
     *
     * @throws IllegalArgumentException
     *             If the {@code times} parameter is a negative integer.
     * @throws IllegalStateException
     *             if component is not interactable or the step is not defined.
     */
    public void decrease(int times) {
        if (times < 0) {
            throw new IllegalArgumentException(
                    "The 'times' parameter must be a non-negative integer.");
        }
        ensureComponentIsUsable();
        if (getComponent().getStep() == null) {
            throw new IllegalStateException(
                    "Cannot decrease value if component step is not defined");
        }
        // Use BigDecimal to subtract to prevent potential double precision loss
        double newValue = BigDecimal.valueOf(getComponent().getValue())
                .subtract(BigDecimal.valueOf(getComponent().getStep() * times))
                .doubleValue();
        setValue(newValue);
    }

}
