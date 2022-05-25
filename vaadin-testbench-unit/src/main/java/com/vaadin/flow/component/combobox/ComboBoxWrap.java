/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.combobox;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;
import com.vaadin.testbench.unit.internal.BasicUtilsKt;

@Wraps(fqn = "com.vaadin.flow.component.combobox.ComboBox")
public class ComboBoxWrap<T extends ComboBox<Y>, Y> extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *         target component
     */
    public ComboBoxWrap(T component) {
        super(component);
    }

    /**
     * Simulate writing a filter to the combobox.
     * <p/>
     * Use {@link #getSuggestions()} to get the string values show in the
     * dropdown or
     * {@link #getSuggestionItems()} to get the actual items in the suggestion.
     *
     * @param filter
     *         string to use for filtering
     */
    public void setFilter(String filter) {
        ensureComponentIsUsable();
        try {
            final Field filterSlot = ComboBox.class.getDeclaredField(
                    "filterSlot");
            final boolean state = filterSlot.canAccess(getComponent());
            filterSlot.setAccessible(true);
            ((SerializableConsumer<String>) filterSlot.get(
                    getComponent())).accept(filter);
            filterSlot.setAccessible(state);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Select item by client string representation.
     *
     * @param selection
     *         item representation string
     */
    public void selectItem(String selection) {
        final List<Y> suggestionItems = getSuggestionItems();
        final ItemLabelGenerator<Y> itemLabelGenerator = getComponent().getItemLabelGenerator();
        final List<Y> filtered = suggestionItems.stream()
                .filter(item -> selection.equals(
                        itemLabelGenerator.apply(item)))
                .collect(Collectors.toList());
        if (filtered.size() > 1 || filtered.isEmpty()) {
            throw new IllegalArgumentException(
                    "No item found for '" + selection + "'");
        }
        getComponent().setValue(filtered.get(0));
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
     * Get dropdown suggestions as String representations sent to the client.
     * any filter that is set is taken into account.
     *
     * @return List of item representation strings
     */
    public List<String> getSuggestions() {
        final List<Y> suggestionItems = getSuggestionItems();
        final ItemLabelGenerator<Y> itemLabelGenerator = getComponent().getItemLabelGenerator();

        return suggestionItems.stream()
                .map(item -> itemLabelGenerator.apply(item))
                .collect(Collectors.toList());
    }

    /**
     * Get the actual items for the dropdown as a List.
     * any filter that is set is taken into account.
     *
     * @return List of items
     */
    public List<Y> getSuggestionItems() {
        try {
            final Field filterSlot = ComboBox.class.getDeclaredField(
                    "dataCommunicator");
            boolean state = filterSlot.canAccess(getComponent());
            filterSlot.setAccessible(true);
            final DataCommunicator<T> dataCommunicator = (DataCommunicator) filterSlot.get(
                    getComponent());
            filterSlot.setAccessible(state);

            final Method fetchFromProvider = DataCommunicator.class.getDeclaredMethod(
                    "fetchFromProvider", int.class, int.class);
            state = fetchFromProvider.canAccess(dataCommunicator);
            fetchFromProvider.setAccessible(true);
            List<Y> result = ((Stream<Y>) fetchFromProvider.invoke(
                    dataCommunicator, 0,
                    BasicUtilsKt.get_saneFetchLimit())).collect(
                    Collectors.toList());
            fetchFromProvider.setAccessible(state);
            return result;
        } catch (NoSuchFieldException | IllegalAccessException |
                 NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
