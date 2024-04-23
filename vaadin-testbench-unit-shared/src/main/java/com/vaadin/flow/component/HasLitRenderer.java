package com.vaadin.flow.component;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.function.ValueProvider;
import elemental.json.JsonArray;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

public interface HasLitRenderer<Y> {

    default Set<String> getLitRendererProperties(LitRenderer<Y> litRenderer,
                                                 BiFunction<Class<?>, String, Field> fieldGetter) {
        var valueProvidersField = fieldGetter.apply(LitRenderer.class, "valueProviders");
        try {
            @SuppressWarnings("unchecked")
            var valueProviders = (Map<String, ValueProvider<Y, ?>>) valueProvidersField.get(litRenderer);
            return valueProviders.keySet();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private ValueProvider<Y, ?> findLitRendererProperty(LitRenderer<Y> litRenderer, String propertyName,
                                                        BiFunction<Class<?>, String, Field> fieldGetter) {
        var valueProvidersField = fieldGetter.apply(LitRenderer.class, "valueProviders");
        try {
            @SuppressWarnings("unchecked")
            var valueProviders = (Map<String, ValueProvider<Y, ?>>) valueProvidersField.get(litRenderer);
            var valueProvider = valueProviders.get(propertyName);
            if (valueProvider == null) {
                throw new IllegalArgumentException("Property " + propertyName + " is not registered in LitRenderer.");
            }
            return valueProvider;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default <V> V getLitRendererPropertyValue(int index,
                                              String propertyName, Class<V> propertyClass,
                                              BiFunction<Class<?>, String, Field> fieldGetter,
                                              LitRenderer<Y> litRenderer,
                                              IntFunction<Y> itemGetter) {
        var valueProvider = findLitRendererProperty(litRenderer, propertyName, fieldGetter);
        var untypedValue = valueProvider.apply(itemGetter.apply(index));
        if (propertyClass.isInstance(untypedValue)) {
            return propertyClass.cast(untypedValue);
        } else {
            throw new IllegalArgumentException("Type of property value does not match propertyClass - expected %s, found %s."
                    .formatted(propertyClass.getCanonicalName(), untypedValue.getClass().getCanonicalName()));
        }
    }


    default Set<String> getLitRendererFunctionNames(LitRenderer<Y> litRenderer,
                                                    BiFunction<Class<?>, String, Field> fieldGetter) {
        var clientCallablesField = fieldGetter.apply(LitRenderer.class, "clientCallables");
        try {
            @SuppressWarnings("unchecked")
            var clientCallables = (Map<String, SerializableBiConsumer<Y, JsonArray>>) clientCallablesField.get(litRenderer);
            return clientCallables.keySet();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private SerializableBiConsumer<Y, JsonArray> findLitRendererFunction(LitRenderer<Y> litRenderer, String functionName,
                                                                         BiFunction<Class<?>, String, Field> fieldGetter) {
        var clientCallablesField = fieldGetter.apply(LitRenderer.class, "clientCallables");
        try {
            @SuppressWarnings("unchecked")
            var clientCallables = (Map<String, SerializableBiConsumer<Y, JsonArray>>) clientCallablesField.get(litRenderer);
            var callable = clientCallables.get(functionName);
            if (callable == null) {
                throw new IllegalArgumentException("Function " + functionName + " is not registered in LitRenderer.");
            }
            return callable;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    default void invokeLitRendererFunction(int index, String functionName, JsonArray jsonArray,
                                           BiFunction<Class<?>, String, Field> fieldGetter,
                                           LitRenderer<Y> litRenderer,
                                           IntFunction<Y> itemGetter) {
        var callable = findLitRendererFunction(litRenderer, functionName, fieldGetter);
        callable.accept(itemGetter.apply(index), jsonArray);
    }

}
