/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.checkbox;

import java.util.function.Consumer;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for Checkbox components.
 *
 * @param <T>
 *            component type
 */
@Tests(Checkbox.class)
public class CheckboxTester<T extends Checkbox> extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public CheckboxTester(T component) {
        super(component);
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !getComponent().isReadOnly()
                && !getComponent().isDisabledBoolean();
    }

    @Override
    protected void notUsableReasons(Consumer<String> collector) {
        super.notUsableReasons(collector);
        if (getComponent().isReadOnly()) {
            collector.accept("read only");
        }
        if (getComponent().isDisabledBoolean()) {
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
        T checkbox = getComponent();
        ComponentUtil.fireEvent(checkbox, new ClickEvent<>(checkbox, true, 0, 0,
                0, 0, 0, 0, false, false, false, false));
        checkbox.setValue(!checkbox.getValue());
    }
}
