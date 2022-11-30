/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.details;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for Details components.
 *
 * @param <T>
 *            component type
 */
@Tests(Details.class)
public class DetailsTester<T extends Details> extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DetailsTester(T component) {
        super(component);
    }

    /**
     * Shows contents as if the summary is clicked on the browser.
     *
     * An exception will be thrown if the details are already open.
     *
     * @throws IllegalStateException
     *             if the component is not usable or if the details are already
     *             open.
     */
    public void openDetails() {
        ensureComponentIsUsable();
        setOpened(true);
    }

    /**
     * Hides contents as if the summary is clicked on the browser.
     *
     * An exception will be thrown if the details are not open.
     *
     * @throws IllegalStateException
     *             if the component is not usable or if the details are not
     *             open.
     */
    public void closeDetails() {
        ensureComponentIsUsable();
        setOpened(false);
    }

    /**
     * Toggles details visibility, as if the summary is clicked on the browser.
     */
    public void toggleDetails() {
        ensureComponentIsUsable();
        setOpened(!getComponent().isOpened());
    }

    /**
     * Checks if the details are open.
     *
     * @return {@literal true} if the details are open, otherwise
     *         {@literal false}.
     */
    public boolean isOpen() {
        ensureComponentIsUsable();
        return getComponent().isOpened();
    }

    private void setOpened(boolean opened) {
        T component = getComponent();
        if (opened == component.isOpened()) {
            throw new IllegalStateException(
                    "Details are already " + (opened ? "open" : "close"));
        }
        component.setOpened(opened);
        ComponentUtil.fireEvent(component,
                new Details.OpenedChangeEvent(component, false));
    }

}
