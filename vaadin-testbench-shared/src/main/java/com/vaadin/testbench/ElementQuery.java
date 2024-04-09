/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import com.vaadin.testbench.internal.SharedUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Query class used for finding a given element inside a given search context.
 * <p>
 * The search context is either a {@link WebDriver} instance which searches
 * starting from the root of the current document, or a {@link WebElement}
 * instance, which searches both in the light DOM and inside the shadow root of
 * the given element.
 * <p>
 * When the search context is a {@link WebElement}, the shadow root is searched
 * first. E.g. when searching by ID and the same ID is used by a light DOM child
 * of the element and also inside its shadow root, the element from the shadow
 * root is returned.
 * <p>
 * The element class specified in the constructor defines the tag name which is
 * searched for an also the type of element returned.
 */
public class ElementQuery<T extends TestBenchElement> {

    private static final int DEFAULT_WAIT_TIME_OUT_IN_SECONDS = 10;

    public static class AttributeMatch {
        private final String name;
        private final String operator;
        private final String value;

        public AttributeMatch(String name, String value) {
            this(name, "=", value);
        }

        public AttributeMatch(String name, String operator, String value) {
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

        public AttributeMatch(String name) {
            this.name = name;
            operator = null;
            value = null;
        }

        @Override
        public String toString() {
            return getExpression();
        }

        public String getExpression() {
            if (operator == null) {
                // [disabled]
                return "[" + name + "]";
            } else {
                // [type='text']
                return "[" + name + operator + "'" + escapeAttributeValue(value)
                        + "']";
            }
        }

        private static String escapeAttributeValue(String value) {
            return value.replace("'", "\\'");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AttributeMatch)) {
                return false;
            }

            return getExpression()
                    .equals(((AttributeMatch) obj).getExpression());
        }

        @Override
        public int hashCode() {
            return getExpression().hashCode();
        }

    }

    private final Class<T> elementClass;
    private final String tagName;
    private final Set<AttributeMatch> attributes;
    private final List<Predicate<T>> conditions;
    private SearchContext searchContext;

    /**
     * Instantiate a new ElementQuery to look for the given type of element.
     *
     * @param elementClass
     *            the type of element to look for and return
     */
    public ElementQuery(Class<T> elementClass) {
        this(elementClass, getTagName(elementClass));
    }

    /**
     * Instantiate a new ElementQuery to look for the given type of element.
     *
     * @param elementClass
     *            the type of element to return
     * @param tagName
     *            the tag name of the element to find
     */
    public ElementQuery(Class<T> elementClass, String tagName) {
        this.elementClass = elementClass;
        this.tagName = tagName;
        // Linked to ensure that elements are always returned in the same order.
        this.attributes = new LinkedHashSet<>(getAttributes(elementClass));
        this.conditions = new ArrayList<>();
    }

    /**
     * Executes a search for element with the given id.
     *
     * @param id
     *            the id to look up
     * @return the element with the given id
     *
     * @throws NoSuchElementException
     *             if no element is found
     */
    public T id(String id) {
        return attribute("id", id).first();
    }

    /**
     * Requires the given attribute to be set.
     *
     * @param name
     *            the attribute name
     * @return this element query instance for chaining
     */
    public ElementQuery<T> hasAttribute(String name) {
        attributes.add(new AttributeMatch(name));
        return this;
    }

    /**
     * Requires the given attribute to match the given value.
     * <p>
     * For matching a token in the attribute, see
     * {@link #attributeContains(String, String)}.
     *
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value
     * @return this element query instance for chaining
     */
    public ElementQuery<T> attribute(String name, String value) {
        attributes.add(new AttributeMatch(name, value));
        return this;
    }

    /**
     * Requires the given attribute to contain the given value.
     * <p>
     * Compares with space separated tokens so that e.g.
     * <code>attributeContains('class','myclass');</code> matches
     * <code>class='someclass myclass'</code>.
     * <p>
     * For matching the full attribute value, see
     * {@link #attribute(String, String)}.
     *
     * @param name
     *            the attribute name
     * @param token
     *            the token to look for
     * @return this element query instance for chaining
     */
    public ElementQuery<T> attributeContains(String name, String token) {
        attributes.add(new AttributeMatch(name, "~=", token));
        return this;
    }

    /**
     * Requires the element to satisfy the given condition.
     * <p>
     * Note that conditions are evaluated in order
     * after the element is selected by its attributes.
     *
     * @param condition
     *            the condition for the element to satisfy
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withCondition(Predicate<T> condition) {
        conditions.add(condition);
        return this;
    }

    /**
     * Requires the element to have the given property getter return the supplied value.
     *
     * @param getter
     *            the function to get the value of the property of the element,
     *            not null
     * @param propertyValue
     *            value to be compared with the one obtained from the
     *            getter function of the element
     * @return this element query instance for chaining
     */
    public <V> ElementQuery<T> withPropertyValue(Function<T, V> getter, V propertyValue) {
        Objects.requireNonNull(getter, "getter function must not be null");
        return withCondition(element -> Objects.equals(getter.apply(element), propertyValue));
    }

    /**
     * Requires the element's label to satisfy the given comparison
     * with the supplied text.
     * <p>
     * For matching a label exactly, see {@link #withLabel(String)},
     * and for matching a label partially, see {@link #withLabelContaining(String)}.
     * <p>
     * This method can be used for other label matching needs,
     * such as performing a case-insensitive match:
     * {@code withLabel("name", String::equalsIgnoreCase)}
     * <br> a label-ending match:
     * {@code withLabel(" Name", String::endsWith)}
     * <br> or a regular expression match:
     * {@code withLabel("(First|Last) Name", String::matches)}
     *
     * @param text
     *          the text to compare with the label
     * @param comparison
     *          the comparison to use when comparing the text with the label
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withLabel(String text, BiPredicate<String, String> comparison) {
        return withCondition(element -> (element instanceof HasLabel hasLabel) &&
                Optional.ofNullable(hasLabel.getLabel())
                        .filter(label -> comparison.test(label, text))
                        .isPresent());
    }

    /**
     * Requires the element's label to exactly match the given label value.
     * <p>
     * For partially matching text within the label, see
     * {@link #withLabelContaining(String)}.
     *
     * @param label
     *            the label to match
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withLabel(String label) {
        return withLabel(label, String::equals);
    }

    /**
     * Requires the element's label to partially match the given text value.
     * <p>
     * For exactly matching the label, see
     * {@link #withLabel(String)}.
     *
     * @param text
     *          the text to match
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withLabelContaining(String text) {
        return withLabel(text, String::contains);
    }

    /**
     * Requires the element's placeholder to satisfy the given comparison
     * with the supplied text.
     * <p>
     * For matching a placeholder exactly, see {@link #withPlaceholder(String)},
     * and for matching a placeholder partially, see {@link #withPlaceholderContaining(String)}.
     * <p>
     * This method can be used for other placeholder matching needs,
     * such as performing a case-insensitive match:
     * {@code withPlaceholder("name", String::equalsIgnoreCase)}
     * <br> a placeholder-ending match:
     * {@code withPlaceholder(" Name", String::endsWith)}
     * <br> or a regular expression match:
     * {@code withPlaceholder("(First|Last) Name", String::matches)}
     *
     * @param text
     *          the text to compare with the placeholder
     * @param comparison
     *          the comparison to use when comparing the text with the placeholder
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withPlaceholder(String text, BiPredicate<String, String> comparison) {
        return withCondition(element -> (element instanceof HasPlaceholder hasPlaceholder) &&
                Optional.ofNullable(hasPlaceholder.getPlaceholder())
                        .filter(placeholder -> comparison.test(placeholder, text))
                        .isPresent());
    }

    /**
     * Requires the element's placeholder to exactly match the given placeholder value.
     * <p>
     * For partially matching text within the placeholder, see
     * {@link #withPlaceholderContaining(String)}.
     *
     * @param placeholder
     *            the placeholder to match
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withPlaceholder(String placeholder) {
        return withPlaceholder(placeholder, String::equals);
    }

    /**
     * Requires the element's placeholder to partially match the given text value.
     * <p>
     * For exactly matching the placeholder, see
     * {@link #withPlaceholder(String)}.
     *
     * @param text
     *          the text to match
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withPlaceholderContaining(String text) {
        return withPlaceholder(text, String::contains);
    }

    /**
     * Requires the element's caption (either label or placeholder)
     * to satisfy the given comparison with the supplied text.
     * <p>
     * For matching a caption exactly, see {@link #withCaption(String)},
     * and for matching a caption partially, see {@link #withCaptionContaining(String)}.
     * <p>
     * This method can be used for other caption matching needs,
     * such as performing a case-insensitive match:
     * {@code withCaption("name", String::equalsIgnoreCase)}
     * <br> a caption-ending match:
     * {@code withCaption(" Name", String::endsWith)}
     * <br> or a regular expression match:
     * {@code withCaption("(First|Last) Name", String::matches)}
     *
     * @param text
     *          the text to compare with the caption
     * @param comparison
     *          the comparison to use when comparing the text with the caption
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withCaption(String text, BiPredicate<String, String> comparison) {
        return withCondition(element ->
                ((element instanceof HasLabel hasLabel) &&
                        (Optional.ofNullable(hasLabel.getLabel())
                                .filter(label -> comparison.test(label, text))
                                .isPresent())) ||
                ((element instanceof HasPlaceholder hasPlaceholder) &&
                        (Optional.ofNullable(hasPlaceholder.getPlaceholder())
                                .filter(placeholder -> comparison.test(placeholder, text))
                                .isPresent())));
    }

    /**
     * Requires the element's caption (label or placeholder)
     * to exactly match the given caption value.
     * <p>
     * For partially matching text within the caption, see
     * {@link #withCaptionContaining(String)}.
     *
     * @param caption
     *            the caption to match
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withCaption(String caption) {
        return withCaption(caption, String::equals);
    }

    /**
     * Requires the element's caption (label or placeholder)
     * to partially match the given text value.
     * <p>
     * For exactly matching the caption, see
     * {@link #withCaption(String)}.
     *
     * @param text
     *          the text to match
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withCaptionContaining(String text) {
        return withCaption(text, String::contains);
    }

    /**
     * Requires the element's text
     * to satisfy the given comparison with the supplied text.
     * <p>
     * For matching the element's text exactly, see {@link #withText(String)},
     * and for matching the element's text partially, see {@link #withTextContaining(String)}.
     * <p>
     * This method can be used for other text matching needs,
     * such as performing a case-insensitive match:
     * {@code withText("name", String::equalsIgnoreCase)}
     * <br> a text-ending match:
     * {@code withText(" Name", String::endsWith)}
     * <br> or a regular expression match:
     * {@code withText("(First|Last) Name", String::matches)}
     *
     * @param text
     *          the text to compare with the element's text
     * @param comparison
     *          the comparison to use when comparing the text with the element's text
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withText(String text, BiPredicate<String, String> comparison) {
        return withCondition(element -> Optional.ofNullable(element.getText())
                .filter(et -> comparison.test(et, text))
                .isPresent());
    }

    /**
     * Requires the element's text
     * to exactly match the given text value.
     * <p>
     * For partially matching text within the element's text, see
     * {@link #withTextContaining(String)}.
     *
     * @param text
     *            the text to match
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withText(String text) {
        return withText(text, String::equals);
    }

    /**
     * Requires the element's text
     * to partially match the given text value.
     * <p>
     * For exactly matching the text, see
     * {@link #withText(String)}.
     *
     * @param text
     *          the text to match
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withTextContaining(String text) {
        return withText(text, String::contains);
    }

    /**
     * Sets the context to search inside.
     *
     * @param searchContext
     *            a {@link SearchContext}; either a {@link TestBenchElement} or
     *            {@link WebDriver} (to search from the root) instance
     * @return this element query instance for chaining
     */
    public ElementQuery<T> context(SearchContext searchContext) {
        this.searchContext = searchContext;
        return this;
    }

    /**
     * Defines that the query should start the search from the root of the page,
     * in practice from the {@code <body>} tag.
     *
     * @return this element query instance for chaining
     */
    public ElementQuery<T> onPage() {
        return context(getDriver());
    }

    /**
     * Returns the context (element or driver) to search inside.
     *
     * @return a {@link SearchContext} instance
     */
    protected SearchContext getContext() {
        return searchContext;
    }

    /**
     * Executes the search and returns the first result.
     *
     * @return The element of the type specified in the constructor
     * @throws NoSuchElementException
     *             if no element is found
     */
    public T first() {
        return get(0);
    }

    /**
     * Executes the search and returns the first result once at least once
     * result is available.
     * <p>
     * This method is identical to {@link #first()} if at least one element is
     * present. If no element is found, this method will keep searching until an
     * element is found or if 10 seconds has elapsed.
     *
     * @return The element of the type specified in the constructor
     */
    public T waitForFirst() {
        return waitForFirst(DEFAULT_WAIT_TIME_OUT_IN_SECONDS);
    }

    /**
     * Executes the search and returns the first result once at least once
     * result is available.
     * <p>
     * This method is identical to {@link #first()} if at least one element is
     * present. If no element is found, this method will keep searching until an
     * element is found or {@code timeOutInSeconds} seconds has elapsed.
     *
     * @param timeOutInSeconds
     *            timeout in seconds before this method throws a
     *            {@link NoSuchElementException} exception
     * @return The element of the type specified in the constructor
     */
    public T waitForFirst(long timeOutInSeconds) {
        Object result = new WebDriverWait(getDriver(),
                Duration.ofSeconds(timeOutInSeconds)).until(driver -> {
                    try {
                        return first();
                    } catch (NoSuchElementException e) {
                        return null;
                    }
                });
        if (result == null) {
            throw new NoSuchElementException(getNoSuchElementMessage(null));
        } else {
            return (T) result;
        }

    }

    private WebDriver getDriver() {
        if (getContext() instanceof WebDriver) {
            return (WebDriver) getContext();
        } else {
            return ((TestBenchElement) getContext()).getDriver();
        }
    }

    /**
     * Executes the search and returns the last result.
     *
     * @return The element of the type specified in the constructor
     *
     * @throws NoSuchElementException
     *             if no element is found
     */
    public T last() {
        List<T> all = all();
        return all.get(all.size() - 1);
    }

    /**
     * Executes the search and returns the requested element.
     *
     * @param index
     *            the index of the element to return
     * @return The element of the type specified in the constructor
     * @throws NoSuchElementException
     *             if no element is found
     */
    public T get(int index) {
        List<T> elements = executeSearch(index);
        if (elements.isEmpty()) {
            throw new NoSuchElementException(getNoSuchElementMessage(index));
        }
        return elements.get(0);
    }

    private String getNoSuchElementMessage(Integer index) {
        String msg = "No element with tag <" + tagName + "> found";
        String attrPairs = getAttributePairs();
        if (!attrPairs.isEmpty()) {
            msg += " with the attributes " + attrPairs;
        }
        if (index != null) {
            msg += " using index " + index;
        }
        return msg;
    }

    /**
     * Checks if this ElementQuery describes existing elements. Same as
     * .all().isEmpty().
     *
     * @return true if elements exists. false if not
     */
    public boolean exists() {
        return !all().isEmpty();
    }

    /**
     * Search the open Vaadin application for a list of matching components
     * relative to given context.
     *
     * @return Components as a list of corresponding elements
     */
    public List<T> all() {
        return executeSearch(null);
    }

    /**
     * Executes the search operation with the given conditions and returns a
     * list of matching elements.
     *
     * @param index
     *            the index of the element to return or <code>null</code> to
     *            return all matching elements
     * @return a list of matching elements or an empty list if no matches were
     *         found
     */
    private List<T> executeSearch(Integer index) {
        String indexSuffix = "";
        if (index != null) {
            indexSuffix = "[" + index + "]";
        }

        StringBuilder script = new StringBuilder();
        TestBenchElement elementContext;
        JavascriptExecutor executor;

        if (getContext() instanceof TestBenchElement) {
            script.append("var result = [];" //
                    + "if (arguments[0].shadowRoot) {" //
                    + "  var shadow = arguments[0].shadowRoot.querySelectorAll(arguments[1]+arguments[2]);" //
                    + "  result = result.concat(Array.prototype.slice.call(shadow));" //
                    + "}" //
                    + "var light = arguments[0].querySelectorAll(arguments[1]+arguments[2]);" //
                    + "result = result.concat(Array.prototype.slice.call(light));" //
                    + "return result" //
            );
            elementContext = (TestBenchElement) getContext();
            executor = elementContext.getCommandExecutor().getDriver();
        } else if (getContext() instanceof WebDriver) {
            // Search the whole document
            script.append("var result = [];" //
                    + "const queryResult = document.querySelectorAll(arguments[1]+arguments[2]);"
                    + "result = result.concat(Array.prototype.slice.call(queryResult));"
                    + "return result");
            elementContext = null;
            executor = (JavascriptExecutor) getContext();
        } else {
            if (getContext() == null) {
                throw new IllegalStateException("Context cannot be null");
            } else {
                throw new IllegalStateException("Unknown context type: "
                        + getContext().getClass().getName());
            }
        }
        if (indexSuffix != null) {
            script.append(indexSuffix);
        }

        return executeSearchScript(script.toString(), elementContext, tagName,
                getAttributePairs(), executor).stream()
                .filter(this::satisfiesAllConditions)
                .toList();
    }

    private boolean satisfiesAllConditions(T element) {
        return conditions.stream().allMatch(condition -> condition.test(element));
    }

    private static String getTagName(Class<?> elementClass) {
        Element annotation = elementClass.getAnnotation(Element.class);
        if (annotation == null) {
            throw new IllegalStateException("The given element class "
                    + elementClass.getName() + " must be annotated using @"
                    + Element.class.getName());
        }
        return annotation.value();
    }

    static Set<AttributeMatch> getAttributes(
            Class<? extends TestBenchElement> elementClass) {
        Attribute[] attrs = elementClass.getAnnotationsByType(Attribute.class);
        if (attrs == null) {
            return Collections.emptySet();
        }
        Set<AttributeMatch> classAttributes = new HashSet<>();
        for (Attribute attr : attrs) {
            String toMatch;
            if (!Attribute.DEFAULT_VALUE.equals(attr.value())) {
                if (!Attribute.DEFAULT_VALUE.equals(attr.contains())) {
                    throw new RuntimeException(
                            "You can only define either 'contains' or 'value' for an @"
                                    + Attribute.class.getSimpleName());
                }
                String value;
                if (attr.value().equals(Attribute.SIMPLE_CLASS_NAME)) {
                    value = getClassConventionValue(elementClass);
                } else {
                    value = attr.value();
                }
                // [label='my-text']
                classAttributes
                        .add(new AttributeMatch(attr.name(), "=", value));
            } else if (!Attribute.DEFAULT_VALUE.equals(attr.contains())) {
                // [class~='js-card-name']
                String value;
                if (attr.contains().equals(Attribute.SIMPLE_CLASS_NAME)) {
                    value = getClassConventionValue(elementClass);
                } else {
                    value = attr.contains();
                }
                classAttributes
                        .add(new AttributeMatch(attr.name(), "~=", value));
            } else {
                // [disabled]
                classAttributes.add(new AttributeMatch(attr.name()));
            }
        }
        return classAttributes;
    }

    private static String getClassConventionValue(Class<?> elementClass) {
        String value = elementClass.getSimpleName();
        value = value.replaceAll("(Element|PageObject)$", "");
        value = SharedUtil.camelCaseToDashSeparated(value).replaceAll("^-*",
                "");
        return value;
    }

    private String getAttributePairs() {
        // [id='username'][label='Email'][special='foo\\'bar']
        return attributes.stream().map(AttributeMatch::getExpression)
                .collect(Collectors.joining());
    }

    /**
     * Executes the given search script.
     * <p>
     * Package private to enable testing
     *
     * @param script
     *            the script to execute
     * @param context
     *            the element to start the search from or <code>null</code> for
     *            a whole document search
     * @param tagName
     *            the tag name to look for
     * @param attributePairs
     *            the attribute pairs to match
     * @return a list of matching elements of the type defined in the
     *         constructor
     */
    List<T> executeSearchScript(String script, Object context, String tagName,
                                  String attributePairs, JavascriptExecutor executor) {
        var result = executor.executeScript(script, context, tagName, attributePairs);

        // Wrap as the correct type
        if (result instanceof TestBenchElement testBenchElement) {
            return List.of(TestBench.wrap(testBenchElement, elementClass));
        } else if (result instanceof List<?> elements) {
            return elements.stream()
                    .filter(TestBenchElement.class::isInstance)
                    .map(TestBenchElement.class::cast)
                    .map(testBenchElement -> TestBench.wrap(testBenchElement, elementClass))
                    .toList();
        } else {
            return List.of();
        }
    }

}
