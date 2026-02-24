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
package com.vaadin.flow.component.radiobutton;

import java.util.function.Consumer;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for RadioButton components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Tests(fqn = "com.vaadin.flow.component.radiobutton.RadioButton")
public class RadioButtonTester<T extends RadioButton<V>, V>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public RadioButtonTester(T component) {
        super(component);
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !isDisabled();
    }

    @Override
    protected void notUsableReasons(Consumer<String> collector) {
        super.notUsableReasons(collector);
        if (isDisabled()) {
            collector.accept("disabled");
        }
    }

    /**
     * If the component is usable, send click to component as if it was from the
     * client.
     *
     * Checkbox status changes from unchecked to checked or vice versa.
     */
    public void click() {
        ensureComponentIsUsable();
        T radioButton = getComponent();
        ComponentUtil.fireEvent(radioButton, new ClickEvent<>(radioButton, true,
                0, 0, 0, 0, 0, 0, false, false, false, false));
        radioButton.setChecked(true);
    }

    protected boolean isDisabled() {
        return getComponent().getElement().getProperty("disabled", false);
    }
}
