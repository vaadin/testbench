/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.tabs;

import java.util.Objects;

import com.vaadin.flow.component.Component;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for TabSheet component.
 *
 * @param <T>
 *            component type
 * 
 * @deprecated Replace the vaadin-testbench-unit dependency with
 *             browserless-test-junit6 and use the corresponding class from the
 *             com.vaadin.browserless package instead. This class will be
 *             removed in a future version.
 */
@Tests(TabSheet.class)
@Deprecated(forRemoval = true, since = "10.1")
public class TabSheetTester<T extends TabSheet> extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public TabSheetTester(T component) {
        super(component);
    }

    /**
     * Selects the tab with the given label.
     *
     * @param label
     *            the tab label
     * @throws IllegalArgumentException
     *             if the tab with given {@code label} is not visible.
     * @throws IllegalStateException
     *             if tab is not usable in the browser
     */
    public void select(String label) {
        Objects.requireNonNull(label, "label must not be null");
        ensureComponentIsUsable();
        Tab tab = findTab(label);
        doSelectTab(tab, "Tab with label '" + label
                + "' cannot be selected because it is not usable");
    }

    /**
     * Selects a visible tab based on its zero-based index.
     * <p>
     * The {@code index} refers to the zero-base position of the currently
     * visible tabs. For example, if there are three tabs
     * {@literal A (position 0), B (position 1) and C (position 2)}, but tab 'B'
     * is hidden, then to select tab 'C' the call must be {@code select(1)} and
     * not {@code select(2)}.
     *
     * @param index
     *            the zero-based index of the selected tab, negative value to
     *            unselect
     * @throws IllegalStateException
     *             if tab at give index is not usable in the browser
     */
    public void select(int index) {
        ensureComponentIsUsable();
        if (index >= 0) {
            doSelectTab(findTab(index), "Tab at index " + index
                    + " cannot be selected because it is not usable");
        } else {
            getComponent().setSelectedTab(null);
        }
    }

    /**
     * Checks if the tab with the given label is currently selected.
     *
     * @param label
     *            the tab label
     * @return {@literal true} if the tab is selected, {@literal false}
     *         otherwise.
     * @throws IllegalArgumentException
     *             if the tab with given {@code label} is not visible.
     */
    public boolean isSelected(String label) {
        ensureComponentIsUsable();
        return findTab(label).isSelected();
    }

    /**
     * Checks if the visible tab at the given index is currently selected.
     * <p>
     * The {@code index} refers to the zero-base position of the currently
     * visible tabs. For example, if there are three tabs
     * {@literal A (position 0), B (position 1) and C (position 2)}, but tab 'B'
     * is hidden, then to check if tab 'C' is selected the call must be
     * {@code isSelected(1)} and not {@code isSelected(2)}.
     *
     * @param index
     *            the zero-based index of the tab
     * @return {@literal true} if the tab is selected, {@literal false}
     *         otherwise.
     * @throws IllegalStateException
     *             if tab at given index is not visible in the browser
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
     * <p>
     * The {@code index} refers to the zero-base position of the currently
     * visible tabs. For example, if there are three tabs
     * {@literal A (position 0), B (position 1) and C (position 2)}, but tab 'B'
     * is hidden, then to get tab 'C', the call must be {@code getTab(1)} and
     * not {@code getTab(2)}.
     *
     * @param index
     *            the zero-based index of the selected tab, negative value to
     *            unselect
     * @return the tab at the given index
     * @throws IllegalStateException
     *             if tab at given index is not visible in the browser
     * @throws IllegalArgumentException
     *             if the {@code index} is less than zero or greater than the
     *             number of visible tabs.
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

    /**
     * Gets the content of the visible tab with the given label.
     *
     * @param label
     *            the tab label
     * @return the content of the tab with the given label
     * @throws IllegalStateException
     *             if tab is not visible
     */
    @SuppressWarnings("unchecked")
    public <E extends Component> E getTabContent(String label) {
        Objects.requireNonNull(label, "label must not be null");
        ensureComponentIsUsable();
        Tab tab = findTab(label);
        if (tab != null && !tab.isVisible()) {
            throw new IllegalStateException("Tab with label '" + label
                    + "' cannot be selected because it is not usable");
        }
        return (E) getComponent().getComponent(tab);
    }

    /**
     * Gets the content of the visible tab at given index.
     * <p>
     * The {@code index} refers to the zero-base position of the currently
     * visible tabs. For example, if there are three tabs
     * {@literal A (position 0), B (position 1) and C (position 2)}, but tab 'B'
     * is hidden, then to get the content of tab 'C', the call must be
     * {@code getTabContent(1)} and not {@code getTabContent(2)}.
     *
     * @param index
     *            the zero-based index of the selected tab, negative value to
     *            unselect
     * @return the content of the tab at the given index
     * @throws IllegalStateException
     *             if tab is not visible
     * @throws IllegalArgumentException
     *             if the {@code index} is less than zero or greater than the
     *             number of visible tabs.
     */
    @SuppressWarnings("unchecked")
    public <E extends Component> E getTabContent(int index) {
        ensureComponentIsUsable();
        Tab tab = findTab(index);
        if (tab != null && !tab.isVisible()) {
            throw new IllegalStateException("Tab at index " + index
                    + " cannot be selected because it is not usable");
        }
        return (E) getComponent().getComponent(tab);
    }

    private Tab findTab(String label) {
        return getComponent().getChildren().filter(Tabs.class::isInstance)
                .flatMap(tabs -> tabs.getChildren()
                        .filter(Tab.class::isInstance))
                .map(Tab.class::cast).filter(t -> label.equals(t.getLabel()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Tab with label '" + label + "' does not exist"));
    }

    private Tab findTab(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "The 'index' argument should be greater than or equal to 0. It was: "
                            + index);
        }
        Tabs tabs = getComponent().getChildren().filter(Tabs.class::isInstance)
                .map(Tabs.class::cast).findFirst().orElseThrow();
        return (Tab) tabs.getChildren().sequential()
                .filter(Component::isVisible).skip(index).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "The 'index' argument should not be greater than or equals to the number of visible children tabs. It was: "
                                + index));
    }

    private void doSelectTab(Tab tab, String errorMessage) {
        if (tab != null && (!tab.isEnabled() || !tab.isVisible())) {
            throw new IllegalStateException(errorMessage);
        }
        getComponent().setSelectedTab(tab);
    }

}
