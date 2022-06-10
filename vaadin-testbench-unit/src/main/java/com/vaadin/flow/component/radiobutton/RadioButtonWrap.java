/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.radiobutton;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for RadioButton components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Wraps(fqn = "com.vaadin.flow.component.radiobutton.RadioButton")
public class RadioButtonWrap<T extends RadioButton<V>, V>
        extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public RadioButtonWrap(T component) {
        super(component);
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !getComponent().isDisabledBoolean();
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
}
