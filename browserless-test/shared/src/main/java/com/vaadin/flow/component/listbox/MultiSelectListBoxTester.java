/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.listbox;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.browserless.Tests;
import com.vaadin.flow.component.ItemLabelGenerator;

/**
 * Tester for MultiSelectListBox components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Tests(fqn = { "com.vaadin.flow.component.listbox.MultiSelectListBox" })
public class MultiSelectListBoxTester<T extends MultiSelectListBox<V>, V>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public MultiSelectListBoxTester(T component) {
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

        var items = getItemsForSelection(selection);
        items.addAll(getSelected());
        setValueAsUser(items);
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

        var items = getItemsForSelection(selection);
        var value = getSelected().stream().filter(item -> !items.contains(item))
                .collect(Collectors.toSet());
        setValueAsUser(value);
    }

    private Set<V> getItemsForSelection(String[] selection) {
        List<String> items = Arrays.asList(selection);
        final Set<V> filtered = getSuggestionItems().stream()
                .filter(item -> items.contains(getItemLabel(item)))
                .collect(Collectors.toSet());
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
