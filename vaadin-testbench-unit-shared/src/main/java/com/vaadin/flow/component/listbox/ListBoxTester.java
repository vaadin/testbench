/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.listbox;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for ListBox components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Tests(fqn = { "com.vaadin.flow.component.listbox.ListBox" })
public class ListBoxTester<T extends ListBox<V>, V> extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public ListBoxTester(T component) {
        super(component);
    }

    /**
     * Get the currently selected item.
     *
     * @return current selection
     */
    public V getSelected() {
        return getComponent().getValue();
    }

    /**
     * Select item by client string representation.
     *
     * @param selection
     *            item representation string
     */
    public void selectItem(String selection) {
        ensureComponentIsUsable();
        if (selection == null) {
            getComponent().setValue(null);
            return;
        }
        final List<V> filtered = getSuggestionItems().stream()
                .filter(item -> selection.equals(getItemLabel(item)))
                .collect(Collectors.toList());
        if (filtered.size() != 1) {
            throw new IllegalArgumentException(
                    "No item found for '" + selection + "'");
        }
        getComponent().setValue(filtered.get(0));
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
