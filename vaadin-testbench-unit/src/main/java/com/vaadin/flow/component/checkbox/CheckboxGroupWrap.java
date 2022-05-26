/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.flow.component.checkbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.vaadin.flow.data.binder.HasItemComponents;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for CheckboxGroup components.
 *
 * @param <T>
 *            component type
 */
@Wraps(CheckboxGroup.class)
public class CheckboxGroupWrap<T extends CheckboxGroup<V>, V>
        extends ComponentWrap<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public CheckboxGroupWrap(T component) {
        super(component);
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !getComponent().isReadOnly();
    }

    /**
     * Select items by client string representation.
     *
     * @param selection
     *            item string representation
     * @param others
     *            other items string representation
     */
    public void selectItem(String selection, String... others) {
        ensureComponentIsUsable();
        selectItem(toCollection(selection, others));
    }

    /**
     * Select items by client string representation.
     *
     * @param selection
     *            items string representation
     */
    public void selectItem(Collection<String> selection) {
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
     * Deselect items by client string representation.
     *
     * @param selection
     *            item string representation
     * @param others
     *            other items string representation
     */
    public void deselectItem(String selection, String... others) {
        ensureComponentIsUsable();
        deselectItem(toCollection(selection, others));
    }

    /**
     * Deselect items by client string representation.
     *
     * @param selection
     *            items string representation
     */
    public void deselectItem(Collection<String> selection) {
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
        boolean usable = new CheckboxWrap<>(checkbox).isUsable();
        if (!usable && throwIfNotUsable) {
            throw new IllegalStateException(
                    "Item " + checkbox.getLabel() + " is not usable");
        }
        return usable;
    }

    private Collection<String> toCollection(String first, String... others) {
        ArrayList<String> list = new ArrayList<>();
        list.add(first);
        if (others.length > 0) {
            list.addAll(List.of(others));
        }
        return list;
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
