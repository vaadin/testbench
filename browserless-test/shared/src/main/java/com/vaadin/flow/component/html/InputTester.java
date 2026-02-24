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
package com.vaadin.flow.component.html;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.browserless.Tests;
import com.vaadin.flow.component.html.Input;

@Tests(Input.class)
public class InputTester extends ComponentTester<Input> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public InputTester(Input component) {
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
    public void setValue(String value) {
        ensureComponentIsUsable();

        if (value == null && getComponent().getEmptyValue() != null) {
            throw new IllegalArgumentException(
                    "Field doesn't allow null values");
        }

        setValueAsUser(value);
    }

    /**
     * Get the current value of the component.
     *
     * @return current component value
     * @throws IllegalStateException
     *             if component not visible
     */
    public String getValue() {
        ensureVisible();
        return getComponent().getValue();
    }

    /**
     * Resets the value to the empty value of the component.
     */
    public void clear() {
        ensureComponentIsUsable();

        setValue(getComponent().getEmptyValue());
    }
}
