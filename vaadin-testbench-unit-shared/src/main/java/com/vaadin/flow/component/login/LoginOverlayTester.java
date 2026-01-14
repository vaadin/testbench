/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.login;

import java.util.function.Consumer;

import com.vaadin.testbench.unit.Tests;

/**
 * Tester for LoginOverlay components.
 *
 * @param <T>
 *            component type
 */
@Tests(LoginOverlay.class)
public class LoginOverlayTester<T extends LoginOverlay>
        extends AbstractLoginTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public LoginOverlayTester(T component) {
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
