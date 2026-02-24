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
package com.vaadin.flow.component.select;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.DataViewUtils;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

@Tests(fqn = { "com.vaadin.flow.component.select.Select" })
public class SelectTester<T extends Select<Y>, Y> extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public SelectTester(T component) {
        super(component);
    }

    /**
     * Get the currently selected item.
     *
     * @return current selection
     */
    public Y getSelected() {
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
            setValueAsUser(null);
            return;
        }
        final List<Y> filtered = getSuggestionItems().stream()
                .filter(item -> selection.equals(getItemLabel(item)))
                .collect(Collectors.toList());
        if (filtered.size() != 1) {
            throw new IllegalArgumentException(
                    "No item found for '" + selection + "'");
        }
        setValueAsUser(filtered.get(0));
    }

    /**
     * Get dropdown suggestions as String representations sent to the client.
     * Any filter that is set is taken into account.
     *
     * @return List of item representation strings
     */
    public List<String> getSuggestions() {
        final List<Y> suggestionItems = getSuggestionItems();
        return suggestionItems.stream().map(this::getItemLabel)
                .collect(Collectors.toList());
    }

    private String getItemLabel(Y item) {
        final ItemLabelGenerator<Y> itemLabelGenerator = getComponent()
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
    public List<Y> getSuggestionItems() {
        return (List<Y>) getComponent().getDataProvider()
                .fetch(DataViewUtils.getQuery(getComponent()))
                .collect(Collectors.toList());
    }
}
