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
 * instance which searches through the whole component tree, or a
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
    private final Function<Component, ? extends ComponentWrap<?>> wrapperFactory;
    private final LocatorSpec<T> locatorSpec = new LocatorSpec<>();

    private Component context;

    /**
     * Creates a new instance of {@link ComponentQuery} to search for components
     * of given type.
     *
     * @param componentType
     *            the type of the component(s) to search for
     * @param wrapperFactory
     *            function to create a component wrapper for found components
     * @see ComponentWrap
     */
    public ComponentQuery(Class<T> componentType,
            Function<Component, ? extends ComponentWrap<?>> wrapperFactory) {
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
     * Requires the component to have the given id
     *
     * @param id
     *            the id to look up
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withId(String id) {
        locatorSpec.id = id;
        // At most one element with given id is expected
        locatorSpec.count = new IntRange(0, 1);
        return this;
    }

    /**
     * Requires the components to satisfy the given condition.
     *
     * @param condition
     *            the condition to check against the components.
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withCondition(Predicate<T> condition) {
        Objects.requireNonNull(condition, "condition must not be null");
        locatorSpec.predicates.add(condition);
        return this;
    }

    /**
     * Add theme that should be set on the target component.
     *
     * @param theme
     *            theme that should exist on the component.
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withTheme(String theme) {
        if (locatorSpec.themes == null || locatorSpec.themes.isEmpty()) {
            locatorSpec.themes = theme;
        } else {
            locatorSpec.themes = locatorSpec.themes + " " + theme;
        }
        return this;
    }

    /**
     * Add theme that should not be available on the target component.
     *
     * @param theme
     *            theme that should not exist on the component.
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withoutTheme(String theme) {
        if (locatorSpec.withoutThemes == null
                || locatorSpec.withoutThemes.isEmpty()) {
            locatorSpec.withoutThemes = theme;
        } else {
            locatorSpec.withoutThemes = locatorSpec.withoutThemes + " " + theme;
        }
        return this;
    }

    /**
     * Gets a new {@link ComponentQuery} to search for given component type on
     * the context of first matching component for current query.
     *
     * @param componentType
     *            the type of the component(s) to search for
     * @param <E>
     *            the type of the component(s) to search for
     * @return a new query object, to search for nested components.
     * @throws java.util.NoSuchElementException
     *             if first component is found
     */
    public <E extends Component> ComponentQuery<E> thenOnFirst(
            Class<E> componentType) {
        return thenOn(1, componentType);
    }

    /**
     * Gets a new {@link ComponentQuery} to search for given component type on
     * the context of the matching component at given index for current query.
     *
     * Index is 1-based. Given a zero or negative index or an index higher than
     * the actual number of components found results in an
     * {@link IndexOutOfBoundsException}.
     *
     * @param componentType
     *            the type of the component(s) to search for
     * @param <E>
     *            the type of the component(s) to search for
     * @return a new query object, to search for nested components.
     * @see #atIndex(int)
     * @throws IllegalArgumentException
     *             if index is zero or negative
     * @throws IndexOutOfBoundsException
     *             if index is greater than the number of found components
     * @throws java.util.NoSuchElementException
     *             if current query does not produce results
     */
    public <E extends Component> ComponentQuery<E> thenOn(int index,
            Class<E> componentType) {
        return new ComponentQuery<>(componentType, wrapperFactory)
                .from(atIndex(index).getComponent());
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
     * Executes the search against current context and returns the test wrapper
     * for the component at given index.
     *
     * Index is 1-based. Given a zero or negative index or an index higher than
     * the actual number of components found results in an
     * {@link IndexOutOfBoundsException}.
     *
     * @return a test wrapper for the component of the type specified in the
     *         constructor.
     * @throws IllegalArgumentException
     *             if index is zero or negative
     * @throws IndexOutOfBoundsException
     *             if index is greater than the number of found components
     * @throws java.util.NoSuchElementException
     *             if no component is found
     */
    @SuppressWarnings("unchecked")
    public <X extends ComponentWrap<T>> X atIndex(int index) {
        if (index <= 0) {
            throw new IllegalArgumentException(
                    "Index must be greater than zero, but was " + index);
        }
        List<T> result = allComponents();
        if (result.isEmpty()) {
            throw new NoSuchElementException(
                    "Cannot find component for current query");
        }
        int resultSize = result.size();
        if (index > resultSize) {
            throw new IndexOutOfBoundsException("Index out of range: " + index
                    + ". Current query produces " + resultSize + " results");
        }
        return (X) wrapperFactory.apply(result.get(index - 1));
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
        withId(id);
        // Exactly one element with given id is expected
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
        locatorSpec.count = new IntRange(1, 1);
        if (context != null) {
            return LocatorKt._get(context, componentType,
                    locatorSpec::populate);
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
        public String themes;
        public String withoutThemes;
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
            if (themes != null)
                spec.setThemes(themes);
            if (withoutThemes != null)
                spec.setWithoutThemes(withoutThemes);
            spec.setCount(count);
            spec.getPredicates().addAll(predicates);

            return Unit.INSTANCE;
        }

    }

}
