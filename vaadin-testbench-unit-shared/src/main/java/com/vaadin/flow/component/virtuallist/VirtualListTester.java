/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.virtuallist;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLitRenderer;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.Tests;
import elemental.json.Json;
import elemental.json.JsonArray;

import java.util.Collections;

/**
 * Tester for VirtualList components.
 *
 * @param <T>
 *            component type
 * @param <Y>
 *            value type
 */
@Tests(VirtualList.class)
public class VirtualListTester<T extends VirtualList<Y>, Y> extends ComponentTester<T>
        implements HasLitRenderer<Y> {

    // don't use a value too large otherwise
    // Vaadin 19+ will calculate a negative limit and will pass it to SizeVerifier
    private static final int SANE_FETCH_LIMIT = Integer.MAX_VALUE / 1000;

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public VirtualListTester(T component) {
        super(component);
    }

    /**
     * Get the amount of items in the virtual list.
     *
     * @return number of items in the virtual list
     */
    public int size() {
        var dataCommunicator = getComponent().getDataCommunicator();
        return dataCommunicator.isDefinedSize() ?
                dataCommunicator.getDataProviderSize() :
                dataCommunicator.getDataProvider().fetch(saneQuery()).toList().size();
    }

    /**
     * Get the item at the given index.
     *
     * @param index
     *            the zero-based index of the item to get
     * @return virtual list item at index
     */
    public Y getItem(int index) {
        ensureVisible();

        return getComponent().getDataCommunicator().getItem(index);
    }

    public String getItemText(int index) {
        ensureVisible();

        var itemRenderer = getItemRenderer();
        if (itemRenderer instanceof ComponentRenderer) {
            var component = getItemComponent(index);
            if (component == null) {
                return null;
            }
            return component.getElement().getTextRecursively();
        }

        // else default to the element at index having the text
        return getComponent().getChildren()
                .skip(index)
                .findFirst()
                .map(thing -> thing.getElement().getTextRecursively())
                .orElse(null);
    }

    public Component getItemComponent(int index) {
        ensureVisible();

        if (getItemRenderer() instanceof ComponentRenderer<?, Y> componentRenderer) {
            return componentRenderer.createComponent(getItem(index));
        }
        throw new IllegalArgumentException(
                "VirtualList doesn't use a ComponentRenderer.");
    }

    @SuppressWarnings("unchecked")
    private Renderer<Y> getItemRenderer() {
        var rendererField = getField("renderer");
        try {
            return (Renderer<Y>) rendererField.get(getComponent());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get property value for item's LitRenderer.
     *
     * @param index
     *            the zero-based index of the item to get
     * @param propertyName
     *            the name of the LitRenderer property
     * @param propertyClass
     *            the class of the value of the LitRenderer property
     * @param <V>
     *            the type of the LitRenderer property
     * @return value of the LitRenderer property
     * @throws IllegalArgumentException
     *             when the VirtualList is not using a LitRenderer or
     *             when the given type of the property does not match the actual property type
     */
    public <V> V getLitRendererPropertyValue(int index,
                                             String propertyName, Class<V> propertyClass) {
        ensureVisible();

        return getLitRendererPropertyValue(index, propertyName, propertyClass,
                this::getField, getItemRenderer(), this::getItem, "VirtualList");
    }

    /**
     * Invoke named function for item's LitRenderer using the supplied JSON arguments.
     *
     * @param index
     *            the zero-based index of the item to get
     * @param functionName
     *            the name of the LitRenderer function to invoke
     * @param jsonArray
     *            the arguments to pass to the function
     *
     * @see #invokeLitRendererFunction(int, String)
     */
    public void invokeLitRendererFunction(int index, String functionName, JsonArray jsonArray) {
        ensureVisible();

        invokeLitRendererFunction(index, functionName, jsonArray,
                this::getField, getItemRenderer(), this::getItem, "VirtualList");
    }

    /**
     * Invoke named function for item's LitRenderer.
     *
     * @param index
     *            the zero-based index of the item to get
     * @param functionName
     *            the name of the LitRenderer function to invoke
     *
     * @see #invokeLitRendererFunction(int, String, JsonArray)
     */
    public void invokeLitRendererFunction(int index, String functionName) {
        invokeLitRendererFunction(index, functionName, Json.createArray());
    }

    private <F> Query<Y, F> saneQuery() {
        return new Query<>(
                0,
                SANE_FETCH_LIMIT,
                Collections.emptyList(),
                null,
                null);
    }

}
