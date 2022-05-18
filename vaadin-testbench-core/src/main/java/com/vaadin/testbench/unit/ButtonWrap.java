/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.testbench.unit.internal.PrettyPrintTreeKt;

/**
 *
 * Test wrapper for Button components.
 *
 * @param <T>
 *            component type
 */
public class ButtonWrap<T extends Button> extends ComponentWrap<T> {
    /**
     * Wrap given button for testing.
     *
     * @param component
     *            target button
     */
    public ButtonWrap(T component) {
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
