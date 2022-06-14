/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.notification;

import java.util.function.Consumer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.nodefeature.ElementListenerMap;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

import elemental.json.Json;

/**
 * Test wrapper for Notification components.
 *
 * @param <T>
 *            component type
 */
@Wraps(Notification.class)
public class NotificationWrap<T extends Notification> extends ComponentWrap<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public NotificationWrap(T component) {
        super(component);
    }

    /**
     * Gets the text of the notification.
     *
     * If the notification is not displayed an IllegalStateException will be
     * thrown as the end user would not be able to see the contents.
     *
     * @return the text of the notification
     * @throws IllegalStateException
     *             is notification is not displayed
     */
    public String getText() {
        ensureComponentIsUsable();
        return getComponent().getElement().getChild(0).getProperty("innerHTML");
    }

    /**
     * Simulates auto-close of the notification, because of show duration
     * timeout.
     *
     * If notification is not displayed or auto-close is disabled setting
     * duration to 0 or negative, an {@link IllegalStateException} is thrown.
     *
     * @throws IllegalStateException
     *             if the notification is not displayed or has auto-close
     *             disabled.
     */
    public void autoClose() {
        ensureComponentIsUsable();
        if (getComponent().getDuration() <= 0) {
            throw new IllegalStateException("Auto-close is not enabled");
        }
        getComponent().close();
        fireOpenChangedDomEvent();
        roundTrip();
    }

    @Override
    public boolean isUsable() {
        T component = getComponent();
        return component.isVisible() && component.isAttached()
                && component.isOpened();
    }

    @Override
    protected void notUsableReasons(Consumer<String> collector) {
        T component = getComponent();
        if (!component.isAttached()) {
            collector.accept("not attached");
        }
        if (!component.isVisible()) {
            collector.accept("not visible");
        }
        if (component.isOpened()) {
            collector.accept("not opened");
        }
    }

    // Simulate browser event fired when notification is closed
    private void fireOpenChangedDomEvent() {
        Element element = getComponent().getElement();
        element.getNode().getFeature(ElementListenerMap.class).fireEvent(
                new DomEvent(element, "open-changed", Json.createObject()));
    }
}
