/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.listbox;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for MultiSelectListBox components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Wraps(fqn = { "com.vaadin.flow.component.listbox.MultiSelectListBox" })
public class MultiSelectListBoxWrap<T extends MultiSelectListBox<V>, V>
        extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public MultiSelectListBoxWrap(T component) {
        super(component);
    }

    /**
     * Get the currently selected items.
     *
     * @return current selection
     */
    public Set<V> getSelected() {
        return getComponent().getValue();
    }

    /**
     * Select item(s) by client string representation.
     *
     * @param selection
     *            item representation string, not null
     */
    public void selectItems(String... selection) {
        Objects.requireNonNull(selection, "Can not select null");

        ensureComponentIsUsable();

        getComponent().select(getItemsForSelection(selection));
    }

    /**
     * Deselect item(s) by client string representation.
     *
     * @param selection
     *            item representation string, not null
     * @throws IllegalArgumentException
     *             if selection contained item not available for selection
     */
    public void deselectItems(String... selection) {
        Objects.requireNonNull(selection, "Can not deselect null");

        ensureComponentIsUsable();

        getComponent().deselect(getItemsForSelection(selection));
    }

    private List<V> getItemsForSelection(String[] selection) {
        List<String> items = Arrays.asList(selection);
        final List<V> filtered = getSuggestionItems().stream()
                .filter(item -> items.contains(getItemLabel(item)))
                .collect(Collectors.toList());
        if (filtered.size() != items.size()) {
            throw new IllegalArgumentException(
                    "Selection contained items that didn't have a selection");
        }
        return filtered;
    }

    /**
     * Clear all selected items from the component.
     */
    public void clearSelection() {
        getComponent().clear();
    }

    /**
     * Get available items as String representations sent to the client. Any
     * filter that is set is taken into account.
     *
     * @return List of item representation strings
     */
    public List<String> getSuggestions() {
        final List<V> suggestionItems = getSuggestionItems();
        return suggestionItems.stream().map(this::getItemLabel)
                .collect(Collectors.toList());
    }

    private String getItemLabel(V item) {
        final ItemLabelGenerator<V> itemLabelGenerator = getComponent()
                .getItemLabelGenerator();
        if (itemLabelGenerator != null) {
            return itemLabelGenerator.apply(item);
        }
        return item.toString();
    }

    /**
     * Get the actual items for the dropdown as a List. Any filter that is set
     * is taken into account.
     *
     * @return List of items
     */
    public List<V> getSuggestionItems() {
        return getComponent().getGenericDataView().getItems()
                .collect(Collectors.toList());
    }

}
