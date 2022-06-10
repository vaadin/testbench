/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.flow.component.tabs;

import java.util.Objects;

import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for Tabs components.
 *
 * @param <T>
 *            component type
 */
@Wraps(Tabs.class)
public class TabsWrap<T extends Tabs> extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public TabsWrap(T component) {
        super(component);
    }

    /**
     * Selects the tab with the given label.
     *
     * @param label
     *            the tab label
     */
    public void select(String label) {
        Objects.requireNonNull(label, "label must not be null");
        ensureComponentIsUsable();
        Tab tab = findTab(label);
        doSelectTab(tab, "Tab with label '" + label
                + "' cannot be selected because it is not usable");
    }

    /**
     * Selects a tab based on its zero-based index.
     *
     * @param index
     *            the zero-based index of the selected tab, negative value to
     *            unselect
     */
    public void select(int index) {
        ensureComponentIsUsable();
        doSelectTab(findTab(index), "Tab at index " + index
                + " cannot be selected because it is not usable");
    }

    /**
     * Checks if the tab with the given label is currently selected.
     *
     * @param label
     *            the tab label
     * @return {@literal true} if the tab is selected, {@literal false}
     *         otherwise.
     */
    public boolean isSelected(String label) {
        ensureComponentIsUsable();
        return findTab(label).isSelected();
    }

    /**
     * Checks if the tab at the given index is currently selected.
     *
     * @param index
     *            the zero-based index of the tab
     * @return {@literal true} if the tab is selected, {@literal false}
     *         otherwise.
     */
    public boolean isSelected(int index) {
        ensureComponentIsUsable();
        return findTab(index).isSelected();
    }

    /**
     * Gets the visible tab with the given label.
     *
     * @param label
     *            the tab label
     * @throws IllegalStateException
     *             if tab is not visible
     */
    public Tab getTab(String label) {
        Objects.requireNonNull(label, "label must not be null");
        ensureComponentIsUsable();
        Tab tab = findTab(label);
        if (tab != null && !tab.isVisible()) {
            throw new IllegalStateException("Tab with label '" + label
                    + "' cannot be selected because it is not usable");
        }
        return tab;
    }

    /**
     * Gets the visible tab at given index.
     *
     * @param index
     *            the zero-based index of the selected tab, negative value to
     *            unselect
     * @throws IllegalStateException
     *             if tab is not visible
     */
    public Tab getTab(int index) {
        ensureComponentIsUsable();
        Tab tab = findTab(index);
        if (tab != null && !tab.isVisible()) {
            throw new IllegalStateException("Tab at index " + index
                    + " cannot be selected because it is not usable");
        }
        return tab;
    }

    private Tab findTab(String label) {
        return getComponent().getChildren().filter(Tab.class::isInstance)
                .map(Tab.class::cast).filter(t -> label.equals(t.getLabel()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Tab with label '" + label + "' does not exist"));
    }

    private Tab findTab(int index) {
        if (index > getComponent().getComponentCount()) {
            throw new IllegalArgumentException("Invalid tab index " + index);
        }
        Tab tab = null;
        if (index >= 0) {
            tab = (Tab) getComponent().getComponentAt(index);
        }
        return tab;
    }

    private void doSelectTab(Tab tab, String errorMessage) {
        if (tab != null && (!tab.isEnabled() || !tab.isVisible())) {
            throw new IllegalStateException(errorMessage);
        }
        getComponent().setSelectedTab(tab);
    }

}
