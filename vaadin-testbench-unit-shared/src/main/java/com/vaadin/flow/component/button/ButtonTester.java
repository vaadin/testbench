/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.MetaKeys;
import com.vaadin.testbench.unit.Tests;
import com.vaadin.testbench.unit.internal.PrettyPrintTreeKt;

/**
 *
 * Tester for Button components.
 *
 * @param <T>
 *            component type
 */
@Tests(Button.class)
public class ButtonTester<T extends Button> extends ComponentTester<T> {
    /**
     * Wrap given button for testing.
     *
     * @param component
     *            target button
     */
    public ButtonTester(T component) {
        super(component);
    }

    /**
     * If the component is usable send click to component as if it was from the
     * client.
     */
    public void click() {
        click(0, new MetaKeys());
    }

    /**
     * If the component is usable send click to component as if it was from the
     * client with defined meta keys pressed.
     */
    public void click(MetaKeys metaKeys) {
        click(0, metaKeys);
    }

    /**
     * Click with middle button.
     */
    public void middleClick() {
        click(1, new MetaKeys());
    }

    /**
     * Click with middle button and given meta keys.
     */
    public void middleClick(MetaKeys metaKeys) {
        click(1, metaKeys);
    }

    /**
     * Click with right button.
     */
    public void rightClick() {
        click(2, new MetaKeys());
    }

    /**
     * Click with right button and given meta keys.
     */
    public void rightClick(MetaKeys metaKeys) {
        click(2, metaKeys);
    }

    private void click(int button, MetaKeys metaKeys) {
        if (!isUsable()) {
            throw new IllegalStateException(
                    PrettyPrintTreeKt.toPrettyString(getComponent())
                            + " is not usable");
        }
        ComponentUtil.fireEvent(getComponent(),
                new ClickEvent(getComponent(), true, 0, 0, 0, 0, 0, button,
                        metaKeys.isCtrl(), metaKeys.isShift(), metaKeys.isAlt(),
                        metaKeys.isMeta()));
    }
}
