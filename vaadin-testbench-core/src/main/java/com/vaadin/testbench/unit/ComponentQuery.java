/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import kotlin.Unit;
import kotlin.ranges.IntRange;

import com.vaadin.flow.component.Component;
import com.vaadin.testbench.unit.internal.LocatorKt;
import com.vaadin.testbench.unit.internal.SearchSpec;

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
    private final LocatorSpec<T> locatorSpec = new LocatorSpec<>();

    private Component context;

    public ComponentQuery(Class<T> componentType,
            Function<T, ComponentWrap<T>> wrapperFactory) {
        this.componentType = Objects.requireNonNull(componentType,
                "Component type must not be null");
        this.wrapperFactory = Objects.requireNonNull(wrapperFactory,
                "Component Wrapper Factory type must not be null");
    }

    /**
     * Requires the given property to have expected value.
     *
     * @param getter
     *            the function to get the value of the property of the field,
     *            not null
     * @param expectedValue
     *            value to be compared with the one obtained by applying the
     *            getter function to a component instance
     * @return this element query instance for chaining
     */
    public <V> ComponentQuery<T> withPropertyValue(Function<T, V> getter,
            V expectedValue) {
        Objects.requireNonNull(getter, "getter function must not be null");
        locatorSpec.predicates
                .add(c -> Objects.equals(getter.apply(c), expectedValue));
        return this;
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
     * Executes a search for a component with the given id.
     *
     * @param id
     *            the id to look up
     * @return the wrapper for the component with the given id
     *
     * @throws NoSuchElementException
     *             if no component is found
     */
    public <X extends ComponentWrap<T>> X id(String id) {
        Objects.requireNonNull(id, "id must not be null");
        locatorSpec.id = id;
        locatorSpec.count = new IntRange(1, 1);
        return find();
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
                    locatorSpec::populate);
        }
        return LocatorKt._find(componentType, locatorSpec::populate);
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

    @SuppressWarnings("unchecked")
    protected <X extends ComponentWrap<T>> X find() {
        try {
            return (X) wrapperFactory.apply(findComponent());
        } catch (AssertionError e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    protected T findComponent() {
        if (context != null) {
            return LocatorKt._get(context, componentType, locatorSpec::populate);
        }
        return LocatorKt._get(componentType, locatorSpec::populate);
    }

    /**
     * Private locator content holder used for searching the component.
     *
     * @param <T>
     *            component type for search object.
     */
    private static class LocatorSpec<T extends Component> {

        public String id;
        public String caption;
        public String placeholder;
        public String text;
        public IntRange count = new IntRange(0, Integer.MAX_VALUE);
        public Object value;
        public String classes;
        public String withoutClasses;
        public List<Predicate<T>> predicates = new ArrayList<>(0);

        public Unit populate(SearchSpec<T> spec) {
            if (id != null)
                spec.setId(id);
            if (caption != null)
                spec.setCaption(caption);
            if (placeholder != null)
                spec.setPlaceholder(placeholder);
            if (text != null)
                spec.setText(text);
            if (value != null)
                spec.setValue(value);
            if (classes != null)
                spec.setClasses(classes);
            if (withoutClasses != null)
                spec.setWithoutClasses(withoutClasses);
            spec.setCount(count);
            spec.getPredicates().addAll(predicates);

            return Unit.INSTANCE;
        }

    }

}
