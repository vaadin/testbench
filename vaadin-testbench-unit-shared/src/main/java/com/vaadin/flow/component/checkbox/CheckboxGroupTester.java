/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.checkbox;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.vaadin.flow.data.binder.HasItemComponents;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for CheckboxGroup components.
 *
 * @param <T>
 *            component type
 */
@Tests(fqn = "com.vaadin.flow.component.checkbox.CheckboxGroup")
public class CheckboxGroupTester<T extends CheckboxGroup<V>, V>
        extends ComponentTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public CheckboxGroupTester(T component) {
        super(component);
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !getComponent().isReadOnly();
    }

    @Override
    protected void notUsableReasons(Consumer<String> collector) {
        super.notUsableReasons(collector);
        if (getComponent().isReadOnly()) {
            collector.accept("read only");
        }
    }

    /**
     * Selects an item by its client string representation.
     *
     * @param selection
     *            item string representation
     */
    public void selectItem(String selection) {
        ensureComponentIsUsable();
        selectItems(List.of(selection));
    }

    /**
     * Selects multiple items by client string representation.
     *
     * @param selection
     *            items string representation
     */
    public void selectItems(String... selection) {
        ensureComponentIsUsable();
        selectItems(List.of(selection));
    }

    /**
     * Selects multiple items by client string representation.
     *
     * @param selection
     *            items string representation
     */
    public void selectItems(Collection<String> selection) {
        ensureComponentIsUsable();
        updateSelection(selection, Collection::addAll);
    }

    /**
     * Selects all client usable items.
     */
    public void selectAll() {
        ensureComponentIsUsable();
        getComponent()
                .setValue(getCheckboxes(child -> isUsableCheckbox(child, false))
                        .map(this::getCheckboxValue)
                        .collect(Collectors.toSet()));
    }

    /**
     * Deselects an item by its client string representation.
     *
     * @param selection
     *            item string representation
     */
    public void deselectItem(String selection) {
        ensureComponentIsUsable();
        deselectItems(List.of(selection));
    }

    /**
     * Deselects multiple items by client string representation.
     *
     * @param selection
     *            items string representation
     */
    public void deselectItems(String... selection) {
        ensureComponentIsUsable();
        deselectItems(List.of(selection));
    }

    /**
     * Deselects items by client string representation.
     *
     * @param selection
     *            items string representation
     */
    public void deselectItems(Collection<String> selection) {
        ensureComponentIsUsable();
        updateSelection(selection, Collection::removeAll);
    }

    /**
     * Deselects all client usable items.
     */
    public void deselectAll() {
        ensureComponentIsUsable();
        Set<V> usableItems = getCheckboxes(
                child -> isUsableCheckbox(child, false))
                        .map(this::getCheckboxValue)
                        .collect(Collectors.toSet());
        Set<V> selectedItems = new HashSet<>(getComponent().getValue());
        selectedItems.removeAll(usableItems);
        getComponent().setValue(selectedItems);
    }

    /**
     * Get the list of currently selected items.
     *
     * @return current selection, or an empty list. Never {@literal null}.
     */
    public Set<V> getSelected() {
        return getComponent().getValue();
    }

    @NotNull
    private Stream<Checkbox> getCheckboxes(Predicate<Checkbox> filter) {
        return getComponent().getChildren().filter(Checkbox.class::isInstance)
                .map(Checkbox.class::cast).filter(filter);
    }

    // CheckboxGroup uses an internal CheckBox subclass that holds the item and
    // implements HasItemComponents.ItemComponent
    @SuppressWarnings("unchecked")
    private V getCheckboxValue(Checkbox checkbox) {
        HasItemComponents.ItemComponent<V> cast = (HasItemComponents.ItemComponent<V>) checkbox;
        return cast.getItem();
    }

    private boolean isUsableCheckbox(Checkbox checkbox,
            boolean throwIfNotUsable) {
        boolean usable = new CheckboxTester<>(checkbox).isUsable();
        if (!usable && throwIfNotUsable) {
            throw new IllegalStateException(
                    "Item " + checkbox.getLabel() + " is not usable");
        }
        return usable;
    }

    public void updateSelection(Collection<String> selection,
            BiConsumer<Collection<V>, Collection<V>> updater) {
        Set<String> uniqueItems = new HashSet<>(selection);
        Map<String, V> selectedItems = getCheckboxes(
                child -> uniqueItems.contains(child.getLabel()))
                        .filter(child -> isUsableCheckbox(child, true))
                        .collect(Collectors.toMap(Checkbox::getLabel,
                                this::getCheckboxValue));
        // Check all selected items exist
        uniqueItems.removeAll(selectedItems.keySet());
        if (!uniqueItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid Item string representation: " + uniqueItems);
        }
        Set<V> newValues = new HashSet<>(getComponent().getValue());
        updater.accept(newValues, selectedItems.values());
        getComponent().setValue(Set.copyOf(newValues));
    }

}
