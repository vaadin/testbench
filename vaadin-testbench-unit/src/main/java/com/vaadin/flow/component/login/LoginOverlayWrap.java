/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.login;

import java.util.function.Consumer;

import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for LoginOverlay components.
 *
 * @param <T>
 *            component type
 */
@Wraps(LoginOverlay.class)
public class LoginOverlayWrap<T extends LoginOverlay>
        extends AbstractLoginWrap<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public LoginOverlayWrap(T component) {
        super(component);
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && getComponent().isOpened();
    }

    @Override
    protected void notUsableReasons(Consumer<String> collector) {
        super.notUsableReasons(collector);
        if (getComponent().isOpened()) {
            collector.accept("not opened");
        }
    }

    /**
     * Open LoginOverlay to enable logging in through it.
     */
    public void openOverlay() {
        getComponent().setOpened(true);
    }

    /**
     * Check if login overlay is open.
     *
     * @return {@code true} if overlay is open and visible
     */
    public boolean isOpen() {
        return getComponent().isOpened() && getComponent().isVisible();
    }
}
