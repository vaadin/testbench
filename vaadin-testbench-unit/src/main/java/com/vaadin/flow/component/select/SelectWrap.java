/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.select;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataViewUtils;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;
import com.vaadin.testbench.unit.internal.BasicUtilsKt;

@Wraps(fqn = { "com.vaadin.flow.component.select.Select" })
public class SelectWrap<T extends Select<Y>, Y> extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public SelectWrap(T component) {
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
            getComponent().setValue(null);
            return;
        }
        final List<Y> filtered = getSuggestionItems().stream()
                .filter(item -> selection.equals(getItemLabel(item)))
                .collect(Collectors.toList());
        if (filtered.size() > 1 || filtered.isEmpty()) {
            throw new IllegalArgumentException(
                    "No item found for '" + selection + "'");
        }
        getComponent().setValue(filtered.get(0));
    }

    /**
     * Get dropdown suggestions as String representations sent to the client.
     * any filter that is set is taken into account.
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
     * Get the actual items for the dropdown as a List. any filter that is set
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
