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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;
import com.vaadin.testbench.unit.internal.BasicUtilsKt;

@Tests(fqn = "com.vaadin.flow.component.combobox.ComboBox")
public class ComboBoxTester<T extends ComboBox<Y>, Y>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public ComboBoxTester(T component) {
        super(component);
    }

    /**
     * Simulate writing a filter to the combobox.
     * <p/>
     * Use {@link #getSuggestions()} to get the string values show in the
     * dropdown or {@link #getSuggestionItems()} to get the actual items in the
     * suggestion.
     *
     * @param filter
     *            string to use for filtering
     */
    public void setFilter(String filter) {
        ensureComponentIsUsable();
        try {

            final Field dataControllerField = getField(ComboBoxBase.class,
                    "dataController");
            ComboBoxDataController<T> dataController = (ComboBoxDataController) dataControllerField
                    .get(getComponent());
            final Field filterSlot = getField(ComboBoxDataController.class,
                    "filterSlot");
            ((SerializableConsumer<String>) filterSlot.get(dataController))
                    .accept(filter);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Select item by client string representation.
     *
     * @param selection
     *            item representation string
     */
    public void selectItem(String selection) {
        if (selection == null) {
            getComponent().setValue(null);
            return;
        }
        final List<Y> suggestionItems = getSuggestionItems();
        final ItemLabelGenerator<Y> itemLabelGenerator = getComponent()
                .getItemLabelGenerator();
        final List<Y> filtered = suggestionItems.stream().filter(
                item -> selection.equals(itemLabelGenerator.apply(item)))
                .collect(Collectors.toList());
        if (filtered.size() != 1) {
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
     * Any filter that is set is taken into account.
     *
     * @return List of item representation strings
     */
    public List<String> getSuggestions() {
        final List<Y> suggestionItems = getSuggestionItems();
        final ItemLabelGenerator<Y> itemLabelGenerator = getComponent()
                .getItemLabelGenerator();

        return suggestionItems.stream()
                .map(item -> itemLabelGenerator.apply(item))
                .collect(Collectors.toList());
    }

    /**
     * Get the actual items for the dropdown as a List. Any filter that is set
     * is taken into account.
     *
     * @return List of items
     */
    public List<Y> getSuggestionItems() {
        try {
            final Field dataControllerField = getField(ComboBoxBase.class,
                    "dataController");
            ComboBoxDataController<T> dataController = (ComboBoxDataController) dataControllerField
                    .get(getComponent());
            final Field dataCommunicatorField = getField(
                    ComboBoxDataController.class, "dataCommunicator");
            final DataCommunicator<T> dataCommunicator = (DataCommunicator) dataCommunicatorField
                    .get(dataController);

            final Method fetchFromProvider = getMethod(DataCommunicator.class,
                    "fetchFromProvider", int.class, int.class);
            List<Y> result = ((Stream<Y>) fetchFromProvider.invoke(
                    dataCommunicator, 0, BasicUtilsKt.get_saneFetchLimit()))
                            .collect(Collectors.toList());
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
