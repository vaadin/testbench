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
package com.vaadin.testbench.unit;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

import tools.jackson.databind.node.ArrayNode;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.ValueProvider;

/**
 * Utility methods for unit testing properties and functions of LitRenderers.
 */
public class LitRendererTestUtil {

    private LitRendererTestUtil() throws InstantiationException {
        throw new InstantiationException(LitRendererTestUtil.class.getName()
                + " should not be instantiated");
    }

    /**
     * Gets the property names for the supplied {@link LitRenderer} using the
     * given field getter.
     *
     * @param litRenderer
     *            the LitRenderer with properties to get
     * @param fieldGetter
     *            the field getter of the ComponentTester
     * @return the set of property names of the LitRenderer
     * @param <Y>
     *            the type being renderer by the LitRenderer
     */
    public static <Y> Set<String> getProperties(LitRenderer<Y> litRenderer,
            BiFunction<Class<?>, String, Field> fieldGetter) {
        var valueProvidersField = fieldGetter.apply(LitRenderer.class,
                "valueProviders");
        try {
            @SuppressWarnings("unchecked")
            var valueProviders = (Map<String, ValueProvider<Y, ?>>) valueProvidersField
                    .get(litRenderer);
            return valueProviders.keySet();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <Y> ValueProvider<Y, ?> findProperty(
            LitRenderer<Y> litRenderer,
            BiFunction<Class<?>, String, Field> fieldGetter,
            String propertyName) {
        var valueProvidersField = fieldGetter.apply(LitRenderer.class,
                "valueProviders");
        try {
            @SuppressWarnings("unchecked")
            var valueProviders = (Map<String, ValueProvider<Y, ?>>) valueProvidersField
                    .get(litRenderer);
            var valueProvider = valueProviders.get(propertyName);
            if (valueProvider == null) {
                throw new IllegalArgumentException("Property " + propertyName
                        + " is not registered in LitRenderer.");
            }
            return valueProvider;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the property value for the supplied {@link LitRenderer}.
     *
     * @param litRenderer
     *            the LitRenderer with properties to get
     * @param fieldGetter
     *            the field getter of the ComponentTester
     * @param itemGetter
     *            the getter for the item rendered by the LitRenderer
     * @param index
     *            the index of the item rendered by the LitRenderer
     * @param propertyName
     *            the name of the property of the LitRenderer
     * @param propertyClass
     *            the type of the property value
     * @return the property value
     * @param <Y>
     *            the type being renderer by the LitRenderer
     * @param <V>
     *            the type of the property value
     * @throws IllegalArgumentException
     *             when the type of property value does not match propertyClass
     */
    public static <Y, V> V getPropertyValue(LitRenderer<Y> litRenderer,
            BiFunction<Class<?>, String, Field> fieldGetter,
            IntFunction<Y> itemGetter, int index, String propertyName,
            Class<V> propertyClass) {
        var valueProvider = findProperty(litRenderer, fieldGetter,
                propertyName);
        var untypedValue = valueProvider.apply(itemGetter.apply(index));
        if (propertyClass.isInstance(untypedValue)) {
            return propertyClass.cast(untypedValue);
        } else {
            throw new IllegalArgumentException(
                    "Type of property value does not match propertyClass - expected %s, found %s."
                            .formatted(propertyClass.getCanonicalName(),
                                    untypedValue.getClass()
                                            .getCanonicalName()));
        }
    }

    /**
     * Gets the function names for the supplied {@link LitRenderer} using the
     * given field getter.
     *
     * @param litRenderer
     *            the LitRenderer with properties to get
     * @param fieldGetter
     *            the field getter of the ComponentTester
     * @return the set of function names of the LitRenderer
     * @param <Y>
     *            the type being renderer by the LitRenderer
     */
    public static <Y> Set<String> getFunctionNames(LitRenderer<Y> litRenderer,
            BiFunction<Class<?>, String, Field> fieldGetter) {
        var clientCallablesField = fieldGetter.apply(LitRenderer.class,
                "clientCallables");
        try {
            @SuppressWarnings("unchecked")
            var clientCallables = (Map<String, SerializableBiConsumer<Y, ArrayNode>>) clientCallablesField
                    .get(litRenderer);
            return clientCallables.keySet();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <Y> SerializableBiConsumer<Y, ArrayNode> findFunction(
            LitRenderer<Y> litRenderer,
            BiFunction<Class<?>, String, Field> fieldGetter,
            String functionName) {
        var clientCallablesField = fieldGetter.apply(LitRenderer.class,
                "clientCallables");
        try {
            @SuppressWarnings("unchecked")
            var clientCallables = (Map<String, SerializableBiConsumer<Y, ArrayNode>>) clientCallablesField
                    .get(litRenderer);
            var callable = clientCallables.get(functionName);
            if (callable == null) {
                throw new IllegalArgumentException("Function " + functionName
                        + " is not registered in LitRenderer.");
            }
            return callable;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the function by name for the supplied {@link LitRenderer} using
     * the given field getter.
     *
     * @param litRenderer
     *            the LitRenderer with properties to get
     * @param fieldGetter
     *            the field getter of the ComponentTester
     * @param itemGetter
     *            the getter for the item rendered by the LitRenderer
     * @param index
     *            the index of the item rendered by the LitRenderer
     * @param functionName
     *            the name of the function of the LitRenderer
     * @param jsonArray
     *            additional parameters to pass to the function
     * @param <Y>
     *            the type being renderer by the LitRenderer
     * @throws IllegalArgumentException
     *             when the function is not registered in LitRenderer
     */
    public static <Y> void invokeFunction(LitRenderer<Y> litRenderer,
            BiFunction<Class<?>, String, Field> fieldGetter,
            IntFunction<Y> itemGetter, int index, String functionName,
            ArrayNode jsonArray) {
        var callable = findFunction(litRenderer, fieldGetter, functionName);
        callable.accept(itemGetter.apply(index), jsonArray);
    }

}
