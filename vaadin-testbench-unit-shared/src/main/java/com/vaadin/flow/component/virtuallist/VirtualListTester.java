/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.virtuallist;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.LitRendererTestUtil;
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
public class VirtualListTester<T extends VirtualList<Y>, Y> extends ComponentTester<T> {

    // don't use a value too large
    // otherwise Vaadin 19+ will calculate a negative limit
    // and will pass it to SizeVerifier
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
        ensureVisible();

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

    /**
     * Get the text that is shown on the client for the item at index.
     * <p/>
     * The index is zero-based.
     * <p/>
     * For the default renderer ColumnPathRenderer the result is the sent text
     * for defined object path.
     * <p/>
     * For a ComponentRenderer the result is the rendered component as
     * prettyString.
     * <p/>
     * More to be added as we find other renderers that need handling.
     *
     * @param index
     *            the zero-based index of the item
     * @return item content that is sent to the client
     */
    public String getItemText(int index) {
        ensureVisible();

        // use element text of Component if renderer is ComponentRenderer
        var itemRenderer = getItemRenderer();
        if (itemRenderer instanceof ComponentRenderer) {
            var component = getItemComponent(index);
            if (component == null) {
                return null;
            }
            return component.getElement().getTextRecursively();
        }

        // use LitRenderer label if renderer is ValueProvider (i.e., has a single property "label")
        if (itemRenderer instanceof LitRenderer<Y> litRenderer) {
            if ((LitRendererTestUtil.getProperties(litRenderer, this::getField).stream()
                    .allMatch(propertyName -> propertyName.equals("label"))) &&
                    (LitRendererTestUtil.getFunctionNames(litRenderer, this::getField).isEmpty())) {
                return getLitRendererPropertyValue(index, "label", String.class);
            } else {
                throw new UnsupportedOperationException(
                        "VirtualListTester is unable to get item text when VirtualList uses a LitRenderer.");
            }
        }

        throw new UnsupportedOperationException(
                "VirtualListTester is unable to get item text for this VirtualList's renderer.");
    }

    /**
     * Get an initialized copy of the component for the item.
     * <p>
     * Note, this is not the actual component.
     *
     * @param index
     *            the zero-based index of the item
     * @return initialized component for the target item
     */
    public Component getItemComponent(int index) {
        ensureVisible();

        if (getItemRenderer() instanceof ComponentRenderer<?, Y> componentRenderer) {
            var item = getItem(index);
            return componentRenderer.createComponent(item);
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
     *            the zero-based index of the item
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

        if (getItemRenderer() instanceof LitRenderer<Y> litRenderer) {
            return LitRendererTestUtil.getPropertyValue(index, propertyName, propertyClass,
                    this::getField, litRenderer, this::getItem);
        } else {
            throw new IllegalArgumentException(
                    "This VirtualList doesn't use a LitRenderer.");
        }
    }

    /**
     * Invoke named function for item's LitRenderer using the supplied JSON arguments.
     *
     * @param index
     *            the zero-based index of the item
     * @param functionName
     *            the name of the LitRenderer function to invoke
     * @param jsonArray
     *            the arguments to pass to the function
     *
     * @see #invokeLitRendererFunction(int, String)
     */
    public void invokeLitRendererFunction(int index, String functionName, JsonArray jsonArray) {
        ensureVisible();

        if (getItemRenderer() instanceof LitRenderer<Y> litRenderer) {
            LitRendererTestUtil.invokeFunction(index, functionName, jsonArray,
                    this::getField, litRenderer, this::getItem);
        } else {
            throw new IllegalArgumentException(
                    "This VirtualList doesn't use a LitRenderer.");
        }
    }

    /**
     * Invoke named function for item's LitRenderer.
     *
     * @param index
     *            the zero-based index of the item
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
