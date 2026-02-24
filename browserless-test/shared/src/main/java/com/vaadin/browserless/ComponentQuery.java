/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import kotlin.Unit;
import kotlin.ranges.IntRange;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.dom.Element;
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
 * @param <T>
 *            the type of the component(s) to search for
 * @see ComponentTester
 */
public class ComponentQuery<T extends Component> {

    private final Class<T> componentType;
    private final LocatorSpec<T> locatorSpec = new LocatorSpec<>();

    private Component context;

    /**
     * Creates a new instance of {@link ComponentQuery} to search for components
     * of given type.
     *
     * @param componentType
     *            the type of the component(s) to search for
     */
    public ComponentQuery(Class<T> componentType) {
        this.componentType = Objects.requireNonNull(componentType,
                "Component type must not be null");
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
     * Requires the component to be an implementation of
     * {@link com.vaadin.flow.component.HasValue} interface and to have exactly
     * the given value.
     *
     * Providing a {@literal null} value as {@code expectedValue} has no effects
     * since the filter will not be applied.
     *
     * @param expectedValue
     *            value to be compared with the one obtained by
     *            {@link com.vaadin.flow.component.HasValue#getValue()}
     * @return this element query instance for chaining
     * @see com.vaadin.flow.component.HasValue#getValue()
     */
    public <V> ComponentQuery<T> withValue(V expectedValue) {
        locatorSpec.value = expectedValue;
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
     * Requires the components to have all the given CSS class names
     *
     * @param className
     *            required CSS class names, not {@literal null}
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withClassName(String... className) {
        if (className == null) {
            throw new IllegalArgumentException("className must not be null");
        }
        if (Stream.of(className).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("class names must not be null");
        }
        if (className.length > 0) {
            locatorSpec.classes.addAll(List.of(className));
        }
        return this;
    }

    /**
     * Requires the components to have none of the given CSS class names
     *
     * @param className
     *            CSS class names that component should not have, not
     *            {@literal null}
     *
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withoutClassName(String... className) {
        if (className == null) {
            throw new IllegalArgumentException("className must not be null");
        }
        if (Stream.of(className).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("class names must not be null");
        }
        if (className.length > 0) {
            locatorSpec.withoutClasses.addAll(List.of(className));
        }
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
     * Requires the component to have a caption equal to the given text
     *
     * Concept of caption vary based on the component type. The check is usually
     * made against the {@link com.vaadin.flow.dom.Element} {@literal label}
     * property, but for some component (e.g. Button) the text content may be
     * used.
     *
     * @param caption
     *            the text the component is expected to have as its caption
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withCaption(String caption) {
        locatorSpec.caption = caption;
        locatorSpec.captionExactMatch = true;
        return this;
    }

    /**
     * Requires the component to have a caption containing the given text
     *
     * Concept of caption vary based on the component type. The check is usually
     * made against the {@link com.vaadin.flow.dom.Element} {@literal label}
     * property, but for some component (e.g. Button) the text content may be
     * used.
     *
     * @param text
     *            the text the component is expected to have as its caption
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withCaptionContaining(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        locatorSpec.caption = text;
        locatorSpec.captionExactMatch = false;
        return this;
    }

    /**
     * Requires the text content of the component to be equal to the given text
     *
     * @param text
     *            the text the component is expected to have as its content
     * @return this element query instance for chaining
     * @see Element#getText()
     */
    public ComponentQuery<T> withText(String text) {
        locatorSpec.text = text;
        locatorSpec.textExactMatch = true;
        return this;
    }

    /**
     * Requires the text content of the component to contain the given text
     *
     * @param text
     *            the text the component is expected to have as its caption
     * @return this element query instance for chaining
     * @see Element#getText()
     */
    public ComponentQuery<T> withTextContaining(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        locatorSpec.text = text;
        locatorSpec.textExactMatch = false;
        return this;
    }

    /**
     * Requires the search to find exactly the given number of components
     *
     * @param count
     *            the expected number of component retrieved by the search
     *
     * @return this element query instance for chaining
     * @throws IllegalArgumentException
     *             if {@code count} is negative
     */
    public ComponentQuery<T> withResultsSize(int count) {
        if (count < 0) {
            throw new IllegalArgumentException(
                    "count must be greater or equal than zero, but was "
                            + count);
        }
        locatorSpec.count = new IntRange(count, count);
        return this;
    }

    /**
     * Requires the search to find a number of components within given range
     *
     * @param min
     *            minimum number of components that should be found (inclusive)
     * @param max
     *            maximum number of components that should be found (inclusive)
     *
     * @return this element query instance for chaining
     * @throws IllegalArgumentException
     *             if {@code min} or {@code max} are negative, or if {@code min}
     *             is greater than {@code max}
     */
    public ComponentQuery<T> withResultsSize(int min, int max) {
        if (min < 0) {
            throw new IllegalArgumentException(
                    "min must be greater or equal than zero, but was " + min);
        }
        if (max < 0) {
            throw new IllegalArgumentException(
                    "max must be greater or equal than zero, but was " + max);
        }
        if (min > max) {
            throw new IllegalArgumentException(
                    "max must be greater or equal than min, but was min=" + min
                            + ", max=" + max + "");
        }
        locatorSpec.count = new IntRange(min, max);
        return this;
    }

    /**
     * Requires the search to find at least the given number of components
     *
     * @param min
     *            minimum number of components that should be found (inclusive)
     *
     * @return this element query instance for chaining
     * @throws IllegalArgumentException
     *             if {@code min} or {@code max} are negative, or if {@code min}
     *             is greater than {@code max}
     */
    public ComponentQuery<T> withMinResults(int min) {
        return withResultsSize(min, locatorSpec.count.getEndInclusive());
    }

    /**
     * Requires the search to find at most the given number of components
     *
     * @param max
     *            maximum number of components that should be found (inclusive)
     *
     * @return this element query instance for chaining
     * @throws IllegalArgumentException
     *             if {@code min} or {@code max} are negative, or if {@code min}
     *             is greater than {@code max}
     */
    public ComponentQuery<T> withMaxResults(int max) {
        return withResultsSize(locatorSpec.count.getStart(), max);
    }

    /**
     * Requires the search to find components with the given attribute set,
     * independently of its value.
     *
     * @param attribute
     *            the name of the attribute, not {@literal null}
     *
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withAttribute(String attribute) {
        locatorSpec.predicates.add(ElementConditions.hasAttribute(attribute));
        return this;
    }

    /**
     * Requires the search to find components having the given attribute with
     * exactly the expected value.
     *
     * @param attribute
     *            the name of the attribute, not {@literal null}
     * @param value
     *            value expected to be set on attribute, not {@literal null}
     *
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withAttribute(String attribute, String value) {
        locatorSpec.predicates
                .add(ElementConditions.hasAttribute(attribute, value));
        return this;
    }

    /**
     * Requires the search to find components without the given attribute.
     *
     * @param attribute
     *            the name of the attribute, not {@literal null}
     *
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withoutAttribute(String attribute) {
        locatorSpec.predicates
                .add(ElementConditions.hasNotAttribute(attribute));
        return this;
    }

    /**
     * Requires the search to find components having the given attribute value
     * different from the provided one, or to not have the attribute at all.
     *
     * @param attribute
     *            the name of the attribute, not {@literal null}
     * @param value
     *            value expected not to be set on attribute, not {@literal null}
     *
     * @return this element query instance for chaining
     */
    public ComponentQuery<T> withoutAttribute(String attribute, String value) {
        locatorSpec.predicates
                .add(ElementConditions.hasNotAttribute(attribute, value));
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
        return new ComponentQuery<>(componentType).from(atIndex(index));
    }

    /**
     * Executes the search against current context and returns the component,
     * expecting to find exactly one.
     *
     * Exceptions are thrown if the search produces zero or more than one
     * result.
     *
     * @return the component of the type specified in the constructor.
     * @throws java.util.NoSuchElementException
     *             if not exactly one component is found
     */
    public T single() {
        return find();
    }

    /**
     * Executes the search against current context and returns the first result.
     * <p>
     * <strong>Warning:</strong> This method can lead to flaky tests when
     * multiple matching components exist, as it arbitrarily selects the first
     * one without validation. Consider using {@link #single()} instead, which
     * asserts that exactly one component matches and fails immediately if
     * multiple components are found, making tests more reliable and failures
     * easier to diagnose.
     *
     * @return a component of the type specified in the constructor.
     * @throws java.util.NoSuchElementException
     *             if no component is found
     * @see #single()
     * @deprecated Use {@link #single()} for more reliable tests that assert
     *             exactly one matching component, or use {@link #atIndex(int)}
     *             with an explicit index if selecting from multiple components
     *             is intentional.
     */
    @Deprecated(since = "10.0", forRemoval = true)
    public T first() {
        return all().stream().findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Cannot find component for current query"));
    }

    /**
     * Executes the search against current context and returns the last result.
     *
     * @return a component of the type specified in the constructor.
     * @throws java.util.NoSuchElementException
     *             if no component is found
     */
    public T last() {
        return all().stream().reduce((first, second) -> second)
                .orElseThrow(() -> new NoSuchElementException(
                        "Cannot find component for current query"));
    }

    /**
     * Executes the search against current context and returns the component at
     * given index.
     *
     * Index is 1-based. Given a zero or negative index or an index higher than
     * the actual number of components found results in an
     * {@link IndexOutOfBoundsException}.
     *
     * @return the component of the type specified in the constructor.
     * @throws IllegalArgumentException
     *             if index is zero or negative
     * @throws IndexOutOfBoundsException
     *             if index is greater than the number of found components
     * @throws java.util.NoSuchElementException
     *             if no component is found
     */
    public T atIndex(int index) {
        if (index <= 0) {
            throw new IllegalArgumentException(
                    "Index must be greater than zero, but was " + index);
        }
        List<T> result = all();
        if (result.isEmpty()) {
            throw new NoSuchElementException(
                    "Cannot find component for current query");
        }
        int resultSize = result.size();
        if (index > resultSize) {
            throw new IndexOutOfBoundsException("Index out of range: " + index
                    + ". Current query produces " + resultSize + " results");
        }
        return result.get(index - 1);
    }

    /**
     * Executes a search for a component with the given id.
     *
     * @param id
     *            the id to look up
     * @return the component with the given id
     *
     * @throws NoSuchElementException
     *             if no component is found
     */
    public T id(String id) {
        Objects.requireNonNull(id, "id must not be null");
        withId(id);
        // Exactly one element with given id is expected
        locatorSpec.count = new IntRange(1, 1);
        return find();
    }

    /**
     * Checks if this {@link ComponentQuery} describes existing components.
     *
     * @return {@literal true} if components are found, otherwise
     *         {@literal false}.
     */
    public boolean exists() {
        return !all().isEmpty();
    }

    /**
     * Executes the search against current context and returns a list of
     * matching components.
     *
     * @return a list of found components, or an empty list if search does not
     *         produce results. Never {@literal null}.
     */
    public List<T> all() {
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

    protected T find() {
        locatorSpec.count = new IntRange(1, 1);
        try {
            if (context != null) {
                return LocatorKt._get(context, componentType,
                        locatorSpec::populate);
            }
            return LocatorKt._get(componentType, locatorSpec::populate);
        } catch (AssertionError e) {
            // Happens when found component(s) are not of the expected type
            throw new NoSuchElementException(e.getMessage());
        }
    }

    /**
     * Private locator content holder used for searching the component.
     *
     * @param <T>
     *            component type for search object.
     */
    private static class LocatorSpec<T extends Component> {

        String id;
        String caption;
        boolean captionExactMatch = false;
        String placeholder;
        String text;
        boolean textExactMatch = true;
        IntRange count = new IntRange(0, Integer.MAX_VALUE);
        Object value;
        final Set<String> classes = new HashSet<>();
        final Set<String> withoutClasses = new HashSet<>();
        String themes;
        String withoutThemes;
        List<Predicate<T>> predicates = new ArrayList<>(0);

        public Unit populate(SearchSpec<T> spec) {
            if (id != null)
                spec.setId(id);
            if (caption != null && captionExactMatch)
                spec.setCaption(caption);
            else if (caption != null)
                spec.captionContains(caption);
            if (placeholder != null)
                spec.setPlaceholder(placeholder);
            if (text != null && textExactMatch)
                spec.setText(text);
            else if (text != null)
                spec.getPredicates().add(ElementConditions.containsText(text));
            if (value != null)
                spec.setValue(value);
            if (!classes.isEmpty())
                spec.setClasses(String.join(" ", classes));
            if (!withoutClasses.isEmpty())
                spec.setWithoutClasses(String.join(" ", withoutClasses));
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
