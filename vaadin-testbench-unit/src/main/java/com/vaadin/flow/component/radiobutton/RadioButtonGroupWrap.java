/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.radiobutton;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.binder.HasItemComponents;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.Wraps;

/**
 * Test wrapper for RadioButtonGroup components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Wraps(fqn = "com.vaadin.flow.component.radiobutton.RadioButtonGroup")
public class RadioButtonGroupWrap<T extends RadioButtonGroup<V>, V>
        extends ComponentWrap<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public RadioButtonGroupWrap(T component) {
        super(component);
    }

    @Override
    public boolean isUsable() {
        return super.isUsable() && !getComponent().isReadOnly();
    }

    /**
     * Selects an item by its client string representation.
     *
     * @param selection
     *            item string representation
     */
    public void selectItem(String selection) {
        ensureComponentIsUsable();
        updateSelection(selection);
    }

    /**
     * Deselects item selection.
     */
    public void deselectItem() {
        ensureComponentIsUsable();
        deselectAll();
    }

    /**
     * Deselects all client usable items.
     */
    public void deselectAll() {
        ensureComponentIsUsable();
        getComponent().setValue(getComponent().getEmptyValue());
    }

    /**
     * Get the list of currently selected items.
     *
     * @return current selection, or an empty list. Never {@literal null}.
     */
    public V getSelected() {
        return getComponent().getValue();
    }

    @NotNull
    private Stream<RadioButton> getRadioButtons(Predicate<RadioButton> filter) {
        return getComponent().getChildren()
                .filter(RadioButton.class::isInstance)
                .map(RadioButton.class::cast).filter(filter);
    }

    // RadioButtonGroup uses an internal RadioButton subclass that holds the
    // item and
    // implements HasItemComponents.ItemComponent
    @SuppressWarnings("unchecked")
    private V getRadioButtonValue(RadioButton<T> radioButton) {
        HasItemComponents.ItemComponent<V> cast = (HasItemComponents.ItemComponent<V>) radioButton;
        return cast.getItem();
    }

    private boolean isUsableRadioButton(RadioButton<T> radioButton,
            boolean throwIfNotUsable) {
        boolean usable = new RadioButtonWrap<>(radioButton).isUsable();
        if (!usable && throwIfNotUsable) {
            throw new IllegalStateException(
                    "Item " + radioButton.getValueString() + " is not usable");
        }
        return usable;
    }

    public void updateSelection(String selection) {
        final ItemLabelGenerator<V> itemLabelGenerator = getComponent()
                .getItemLabelGenerator();
        Map<String, V> selectedItems = getRadioButtons(
                child -> selection.equals(itemLabelGenerator
                        .apply((V) getRadioButtonValue(child)))).filter(
                                child -> isUsableRadioButton(child, true))
                                .collect(Collectors.toMap(
                                        button -> itemLabelGenerator
                                                .apply((V) getRadioButtonValue(
                                                        button)),
                                        this::getRadioButtonValue));

        if (selectedItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid Item string representation: " + selection);
        }
        getComponent().setValue(selectedItems.get(selection));
    }

}
