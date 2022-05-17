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

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.nodefeature.ElementListenerMap;

import elemental.json.Json;

/**
 * Test wrapper for Notification components.
 *
 * @param <T>
 *            component type
 */
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
     * Closes the notification.
     *
     * Test methods should always use this method instead of
     * {@link Notification#close()} to simulate the user closing a notification,
     * so that all changes are correctly flushed on server side UI components.
     * 
     * If notification is not displayed an {@link IllegalStateException} is
     * thrown.
     *
     * @throws IllegalStateException
     *             if the notification is not displayed.
     */
    public void close() {
        ensureComponentIsUsable();
        getComponent().close();
        fireOpenChangedDomEvent();
        flushChanges();
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && getComponent().isOpened();
    }

    // Simulate browser event fired when notification is closed
    private void fireOpenChangedDomEvent() {
        Element element = getComponent().getElement();
        element.getNode().getFeature(ElementListenerMap.class).fireEvent(
                new DomEvent(element, "open-changed", Json.createObject()));
    }
}
