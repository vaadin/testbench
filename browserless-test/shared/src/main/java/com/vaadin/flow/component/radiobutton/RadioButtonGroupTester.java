/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.radiobutton;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.binder.HasItemComponents;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;

/**
 * Tester for RadioButtonGroup components.
 *
 * @param <T>
 *            component type
 * @param <V>
 *            value type
 */
@Tests(fqn = "com.vaadin.flow.component.radiobutton.RadioButtonGroup")
public class RadioButtonGroupTester<T extends RadioButtonGroup<V>, V>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public RadioButtonGroupTester(T component) {
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
        updateSelection(selection);
    }

    /**
     * Deselects item selection.
     */
    public void deselectItem() {
        ensureComponentIsUsable();
        setValueAsUser(getComponent().getEmptyValue());
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
        boolean usable = new RadioButtonTester<>(radioButton).isUsable();
        if (!usable && throwIfNotUsable) {
            throw new IllegalStateException(
                    "Item " + radioButton.getElement().getProperty("value")
                            + " is not usable");
        }
        return usable;
    }

    private void updateSelection(String selection) {
        final ItemLabelGenerator<V> itemLabelGenerator = getComponent()
                .getItemLabelGenerator();
        Map<String, V> selectedItems = getRadioButtons(
                child -> selection.equals(itemLabelGenerator
                        .apply((V) getRadioButtonValue(child))))
                .filter(child -> isUsableRadioButton(child, true))
                .collect(Collectors.toMap(
                        button -> itemLabelGenerator
                                .apply((V) getRadioButtonValue(button)),
                        this::getRadioButtonValue));

        if (selectedItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid Item string representation: " + selection);
        }
        setValueAsUser(selectedItems.get(selection));
    }

}
