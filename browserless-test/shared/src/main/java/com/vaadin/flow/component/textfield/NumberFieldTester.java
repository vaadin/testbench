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
package com.vaadin.flow.component.textfield;

import java.util.Objects;
import java.util.function.Consumer;

import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for NumberField components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Tests(fqn = { "com.vaadin.flow.component.textfield.IntegerField",
        "com.vaadin.flow.component.textfield.NumberField" })
public class NumberFieldTester<T extends AbstractNumberField<T, V>, V extends Number>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NumberFieldTester(T component) {
        super(component);
    }

    /**
     * Set the given value for the component.
     * <p/>
     * Throws if component is not usable or the value is invalid.
     *
     * @param value
     *            value to set
     * @throws IllegalArgumentException
     *             if given value is not valid
     */
    public void setValue(V value) {
        ensureComponentIsUsable();
        if (!isValid(value)) {
            throw new IllegalArgumentException(
                    "Given value '" + value + "' is not valid");
        }
        setValueAsUser(value);
    }

    private boolean isValid(V value) {
        final boolean isRequiredButEmpty = getComponent().isRequired()
                && Objects.equals(getComponent().getEmptyValue(), value);
        final boolean isGreaterThanMax = value != null
                && value.doubleValue() > getComponent().getMaxDouble();
        final boolean isSmallerThanMin = value != null
                && value.doubleValue() < getComponent().getMinDouble();

        return !(isRequiredButEmpty || isGreaterThanMax || isSmallerThanMin);
        // TODO: Can we access the Generic isValidByStep
        // || !isValidByStep(value);
    }

    // TODO: support stepUp/stepDown if controls are visible.

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
