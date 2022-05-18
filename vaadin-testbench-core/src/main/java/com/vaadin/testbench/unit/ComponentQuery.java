/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.testbench.unit;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import kotlin.Unit;

import com.vaadin.flow.component.Component;
import com.vaadin.testbench.unit.internal.LocatorKt;

/**
 * Query class used for finding a component inside a given search context.
 * 
 * The search context is either the current {@link com.vaadin.flow.component.UI}
 * instance which searches through the whole components tree, or a
 * {@link com.vaadin.flow.component.Component} instance, which limits the search
 * to the component subtree.
 *
 * Depending on the used terminal operator, the result of the search can be
 * either a UI component (single or list) or a component wrapper.
 * 
 * The component type specified in the constructor defines the type of the
 * component which is searched for and also the type for the component wrapper.
 *
 * @param <T>
 *            the type of the component(s) to search for
 * @see ComponentWrap
 */
public class ComponentQuery<T extends Component> {

    private final Class<T> componentType;
    private final Function<T, ? extends ComponentWrap<T>> wrapperFactory;

    private Component context;

    public ComponentQuery(Class<T> componentType,
            Function<T, ComponentWrap<T>> wrapperFactory) {
        this.componentType = Objects.requireNonNull(componentType,
                "Component type must not be null");
        this.wrapperFactory = Objects.requireNonNull(wrapperFactory,
                "Component Wrapper Factory type must not be null");
    }

    /**
     * Executes the search against current context and returns the test wrapper
     * for first result.
     * 
     * @return a test wrapper for the component of the type specified in the
     *         constructor.
     * @throws java.util.NoSuchElementException
     *             if no component is found
     */
    @SuppressWarnings("unchecked")
    public <X extends ComponentWrap<T>> X first() {
        return (X) allComponents().stream().findFirst().map(wrapperFactory)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cannot find component for current query"));
    }

    /**
     * Executes the search against current context and returns the test wrapper
     * for last result.
     *
     * @return a test wrapper for the component of the type specified in the
     *         constructor.
     * @throws java.util.NoSuchElementException
     *             if no component is found
     */
    @SuppressWarnings("unchecked")
    public <X extends ComponentWrap<T>> X last() {
        return (X) allComponents().stream().reduce((first, second) -> second)
                .map(wrapperFactory)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cannot find component for current query"));
    }

    /**
     * Executes the search against current context and returns a list of test
     * wrappers for matching components.
     *
     * @return a list of test wrappers for found components, or an empty list if
     *         search does not produce results. Never {@literal null}.
     */
    @SuppressWarnings("unchecked")
    public <X extends ComponentWrap<T>> List<X> all() {
        return allComponents().stream()
                .map(component -> (X) wrapperFactory.apply(component))
                .collect(Collectors.toList());
    }

    /**
     * Executes the search against current context and returns a list of
     * matching components.
     *
     * @return a list of found components, or an empty list if search does not
     *         produce results. Never {@literal null}.
     */
    public List<T> allComponents() {
        if (context != null) {
            return LocatorKt._find(context, componentType,
                    spec -> Unit.INSTANCE);
        }
        return LocatorKt._find(componentType, spec -> Unit.INSTANCE);
    }

    /**
     * Sets the context to search inside.
     * 
     * If a {@literal null} value is given, the search will be performed againt
     * the UI.
     * 
     * @param context
     *            a component used as starting element for search.
     * @return this component query instance for chaining.
     */
    public ComponentQuery<T> from(Component context) {
        this.context = context;
        return this;
    }
}
