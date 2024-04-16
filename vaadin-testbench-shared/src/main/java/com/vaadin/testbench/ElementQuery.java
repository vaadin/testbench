/*
 * Copyright (C) 2000-2024 Vaadin Ltd
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vaadin.testbench.ElementQuery.AttributeMatch.Operator.CONTAINS;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Operator.CONTAINS_TOKEN;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Operator.EXISTS;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Operator.MATCHES_EXACTLY;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Operator.NOT_CONTAINS;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Operator.NOT_CONTAINS_TOKEN;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Operator.NOT_EXISTS;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Operator.NOT_MATCHES_EXACTLY;

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
 * searched for and also the type of element returned.
 */
public class ElementQuery<T extends TestBenchElement> {

    private static final int DEFAULT_WAIT_TIME_OUT_IN_SECONDS = 10;

    public static class AttributeMatch {

        public enum Operator {
            /**
             * Attribute exists (with or without a value).
             */
            EXISTS(""),
            /**
             * Attribute value and given value match exactly.
             */
            MATCHES_EXACTLY("="),
            /**
             * Attribute value contains the given value.
             */
            CONTAINS("*="),
            /**
             * Attribute value contains a space-separated token
             * that matches the given value.
             */
            CONTAINS_TOKEN("~="),
            /**
             * Attribute value begins with a space/hyphen-separated prefix
             * that matches the given value.
             * The prefix must be either the entire attribute value or
             * the leading hyphen-separated segments of the attribute value.
             */
            CONTAINS_PREFIX("|="),
            /**
             * Attribute value begins with the given value.
             */
            BEGINS_WITH("^="),
            /**
             * Attribute value ends with the given value.
             */
            ENDS_WITH("$="),
            /**
             * Attribute does not exist (with or without a value).
             */
            NOT_EXISTS(EXISTS.op, true),
            /**
             * Attribute value and given value do not match exactly.
             */
            NOT_MATCHES_EXACTLY(MATCHES_EXACTLY.op, true),
            /**
             * Attribute value does not contain the given value.
             */
            NOT_CONTAINS(CONTAINS.op, true),
            /**
             * Attribute value must not contain a space-separated token
             * that is prefixed with the given value.
             * @see #CONTAINS_TOKEN
             */
            NOT_CONTAINS_TOKEN(CONTAINS_TOKEN.op, true),
            /**
             * Attribute value does not begin with a space/hyphen-separated prefix
             * that matches the given value.
             */
            NOT_CONTAINS_PREFIX(CONTAINS_PREFIX.op, true),
            /**
             * Attribute value does not begin with the given value.
             */
            NOT_BEGINS_WITH(BEGINS_WITH.op, true),
            /**
             * Attribute value does not end with the given value.
             */
            NOT_ENDS_WITH(ENDS_WITH.op, true);

            private final String op;
            private final boolean neg;

            Operator(String op, boolean neg) {
                this.op = op;
                this.neg = neg;
            }

            Operator(String op) {
                this(op, false);
            }

            public String getOp() {
                return op;
            }

            public boolean isNeg() {
                return neg;
            }
        }

        private final String name;
        private final Operator operator;
        private final String value;

        /**
         * Instantiates an attribute matching expression
         * having the supplied attribute name, comparison operator, and
         * value to compare with the attribute's value.
         *
         * @param name the name of the attribute
         * @param operator the comparison operator to use in the comparison
         * @param value the value to compare with the attribute's value
         */
        public AttributeMatch(String name, Operator operator, String value) {
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

        /**
         * Instantiates an attribute matching expression
         * having the supplied attribute name, comparison operator, and
         * value to compare with the attribute's value.
         *
         * @param name the name of the attribute
         * @param operator the comparison operator to use in the comparison
         * @param value the value to compare with the attribute's value
         *
         * @deprecated use {@link #AttributeMatch(String, Operator, String)}
         */
        @Deprecated(since = "9.3")
        public AttributeMatch(String name, String operator, String value) {
            this(name,
                    Arrays.stream(Operator.values())
                            .filter(op -> op.getOp().equals(operator))
                            .findFirst()
                            .orElse(EXISTS),
                    value);
        }

        /**
         * Instantiates an attribute exact matching expression
         * having the supplied attribute name and
         * value to compare with the attribute's value.
         *
         * @param name the name of the attribute
         * @param value the value to exactly match against with the attribute's value
         */
        public AttributeMatch(String name, String value) {
            this(name, MATCHES_EXACTLY, value);
        }

        /**
         * Instantiates an attribute exists expression
         * having the supplied attribute name.
         *
         * @param name the name of the attribute
         */
        public AttributeMatch(String name) {
            this(name, EXISTS, null);
        }

        @Override
        public String toString() {
            return getExpression();
        }

        public String getExpression() {
            String expression = name;

            if (value != null) {
                expression += operator.getOp() + "'" + escapeAttributeValue(value) + "'";
            }

            expression = "[" + expression + "]";

            if (operator.isNeg()) {
                expression = ":not(" + expression + ")";
            }

            return expression;
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

    /**
     * Linked to ensure that elements are always returned in the same order.
     */
    private final Set<AttributeMatch> attributes = new LinkedHashSet<>();
    private SearchContext searchContext;
    private final Class<T> elementClass;
    private final String tagName;

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
        attributes.addAll(getAttributes(elementClass));
    }

    /**
     * Selects on elements having the given attribute.
     * <p>
     * Note, this attribute need not have a value.
     *
     * @param name
     *            the attribute name
     * @return this element query instance for chaining
     *
     * @deprecated use {@link #withAttribute(String)}
     */
    @Deprecated(since = "9.3")
    public ElementQuery<T> hasAttribute(String name) {
        return withAttribute(name);
    }

    /**
     * Selects on elements with the given attribute having the given value.
     * <p>
     * For matching a substring of the attribute value, see
     * {@link #withAttributeContaining(String, String)}.
     * For matching a token within the attribute, see
     * {@link #withAttributeContainingToken(String, String)}.
     *
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value
     * @return this element query instance for chaining
     *
     * @see #withAttribute(String, String)
     * @see #withAttributeContaining(String, String)
     * @see #withAttributeContainingToken(String, String)
     *
     * @deprecated use {@link #withAttribute(String, String)}
     */
    @Deprecated(since = "9.3")
    public ElementQuery<T> attribute(String name, String value) {
        return withAttribute(name, value);
    }

    /**
     * Selects on elements with the given attribute containing the given token.
     * <p>
     * Compares with space separated tokens so that e.g.
     * <code>attributeContains("class", "myclass");</code> matches
     * <code>class='someclass myclass'</code>.
     * <p>
     * For matching the full attribute value, see
     * {@link #withAttribute(String, String)}.
     * For matching a substring of the attribute value, see
     * {@link #withAttributeContaining(String, String)}.
     *
     * @param name
     *            the attribute name
     * @param token
     *            the token to look for
     * @return this element query instance for chaining
     *
     * @see #withAttribute(String, String)
     * @see #withAttributeContaining(String, String)
     * @see #withAttributeContainingToken(String, String)
     *
     * @deprecated use {@link #withAttributeContainingToken(String, String)}
     */
    @Deprecated(since = "9.3")
    public ElementQuery<T> attributeContains(String name, String token) {
        return withAttributeContainingToken(name, token);
    }

    /**
     * Selects on elements having the given attribute.
     * <p>
     * Note, the attribute need not have a value--it just needs to exist.
     *
     * @param attribute
     *            the attribute name
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withAttribute(String attribute) {
        attributes.add(new AttributeMatch(attribute));
        return this;
    }

    /**
     * Selects on elements with the given attribute using the given operator and value.
     * <p>
     * The given value must match the attribute value according to the operator.
     *
     * @param attribute
     *            the attribute name
     * @param value
     *            the attribute value
     * @param operator
     *            the comparison operator to use
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withAttribute(String attribute, String value,
                                         AttributeMatch.Operator operator) {
        attributes.add(new AttributeMatch(attribute, operator, value));
        return this;
    }

    /**
     * Selects on elements with the given attribute having the given value.
     * <p>
     * The given value must match the attribute value exactly.
     * <p>
     * For matching a substring of the attribute, see
     * {@link #withAttributeContaining(String, String)}.
     * For matching a token within the attribute, see
     * {@link #withAttributeContainingToken(String, String)}.
     *
     * @param attribute
     *            the attribute name
     * @param value
     *            the attribute value
     * @return this element query instance for chaining
     *
     * @see #withAttributeContaining(String, String)
     * @see #withAttributeContainingToken(String, String)
     */
    public ElementQuery<T> withAttribute(String attribute, String value) {
        return withAttribute(attribute, value, MATCHES_EXACTLY);
    }

    /**
     * Selects on elements with the given attribute containing the given text.
     * <p>
     * The given text must match a substring of the attribute value.
     * <p>
     * For matching the full attribute value, see
     * {@link #withAttribute(String, String)}.
     * For matching a token within the attribute, see
     * {@link #withAttributeContainingToken(String, String)}.
     *
     * @param attribute
     *            the attribute name
     * @param text
     *            the substring to look for
     * @return this element query instance for chaining
     *
     * @see #withAttribute(String, String)
     * @see #withAttributeContainingToken(String, String)
     */
    public ElementQuery<T> withAttributeContaining(String attribute, String text) {
        return withAttribute(attribute, text, CONTAINS);
    }

    /**
     * Selects on elements with the given attribute containing the given token.
     * <p>
     * Compares with space separated tokens so that e.g.
     * <code>withAttributeContaining("class", "myclass");</code> matches
     * <code>class='someclass myclass'</code>.
     * <p>
     * For matching the full attribute value, see
     * {@link #withAttribute(String, String)}.
     * For matching a substring of the attribute value, see
     * {@link #withAttributeContaining(String, String)}.
     *
     * @param attribute
     *            the attribute name
     * @param token
     *            the token to look for
     * @return this element query instance for chaining
     *
     * @see #withAttribute(String, String)
     * @see #withAttributeContaining(String, String)
     */
    public ElementQuery<T> withAttributeContainingToken(String attribute, String token) {
        return withAttribute(attribute, token, CONTAINS_TOKEN);
    }

    /**
     * Selects on elements not having the given attribute.
     * <p>
     * Note, attributes both with and without values are skipped.
     *
     * @param attribute
     *            the attribute name
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withoutAttribute(String attribute) {
        return withAttribute(attribute, null, NOT_EXISTS);
    }

    /**
     * Selects on elements not having the given attribute with the given value.
     * <p>
     * The given value must match the attribute value exactly in order to be skipped.
     * <p>
     * For matching the full attribute value, see
     * {@link #withoutAttributeContaining(String, String)}.
     * For skipping elements having a token within the attribute, see
     * {@link #withoutAttributeContainingToken(String, String)}.
     *
     * @param attribute
     *            the attribute name
     * @param value
     *            the attribute value
     * @return this element query instance for chaining
     *
     * @see #withoutAttributeContaining(String, String)
     * @see #withoutAttributeContainingToken(String, String)
     */
    public ElementQuery<T> withoutAttribute(String attribute, String value) {
        return withAttribute(attribute, value, NOT_MATCHES_EXACTLY);
    }

    /**
     * Selects on elements not having the given attribute containing the given token.
     * <p>
     * The given value must match any substring of the attribute value
     * in order to be skipped.
     * <p>
     * For matching the full attribute value, see
     * {@link #withoutAttribute(String, String)}.
     * For skipping elements having a token within the attribute, see
     * {@link #withoutAttributeContainingToken(String, String)}.
     *
     * @param attribute
     *            the attribute name
     * @param text
     *            the substring to look for
     * @return this element query instance for chaining
     *
     * @see #withoutAttribute(String, String)
     * @see #withoutAttributeContainingToken(String, String)
     */
    public ElementQuery<T> withoutAttributeContaining(String attribute, String text) {
        return withAttribute(attribute, text, NOT_CONTAINS);
    }

    /**
     * Selects on elements not having the given attribute containing the given token.
     * <p>
     * Compares with space separated tokens so that e.g.
     * <code>withoutAttributeContaining("class", "myclass");</code> skips
     * <code>class='someclass myclass'</code>.
     * <p>
     * For matching the full attribute value, see
     * {@link #withoutAttribute(String, String)}.
     * For skipping elements containing a substring of the attribute, see
     * {@link #withoutAttributeContaining(String, String)}.
     *
     * @param attribute
     *            the attribute name
     * @param token
     *            the token to look for
     * @return this element query instance for chaining
     *
     * @see #withoutAttribute(String, String)
     * @see #withoutAttributeContaining(String, String)
     */
    public ElementQuery<T> withoutAttributeContainingToken(String attribute, String token) {
        return withAttribute(attribute, token, NOT_CONTAINS_TOKEN);
    }

    /**
     * Selects on elements having the given id.
     * <p>
     * This selector does not require the id to be unique.
     * To obtain the unique id, chain with <code>{@link #single()}</code>
     * or use <code>{@link #id(String)}</code> instead of this selector.
     * If you legitimately have duplicate ids and just want the first one,
     * chain with <code>{@link #first()}</code>.
     *
     * @param id
     *            the id to look up
     * @return the element with the given id
     *
     * @see #id(String)
     * @see #single()
     * @see #first()
     */
    public ElementQuery<T> withId(String id) {
        return withAttribute("id", id);
    }

    /**
     * Selects on elements having the given class names.
     *
     * @param classNames
     *            the class names
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withClassName(String... classNames) {
        Arrays.stream(classNames)
                .forEach(className -> withAttributeContainingToken("class", className));
        return this;
    }

    /**
     * Selects on elements not having the given class names.
     *
     * @param classNames
     *            the class names
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withoutClassName(String... classNames) {
        Arrays.stream(classNames)
                .forEach(className -> withoutAttributeContainingToken("class", className));
        return this;
    }

    /**
     * Selects on elements having the given theme.
     *
     * @param theme
     *            the theme
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withTheme(String theme) {
        return withAttribute("theme", theme);
    }

    /**
     * Selects on elements not having the given theme.
     *
     * @param theme
     *            the theme
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withoutTheme(String theme) {
        return withoutAttribute("theme", theme);
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
     * Executes the search and returns an element having the given unique id.
     * <p>
     * This selector expects the id to be unique.
     * If there are duplicate ids, this selector will
     * throw an exception. If you legitimately have duplicate ids,
     * use <code>{@link #withId(String)}.{@link #first()}</code> instead.
     * (Note, this alternate usage is the former behavior of this selector.)
     *
     * @param id
     *            the id to look up
     * @return the element with the given id
     *
     * @throws NoSuchElementException
     *             if no unique id element is found
     *
     * @see #withId(String)
     * @see #first()
     */
    public T id(String id) {
        return withId(id).single();
    }

    /**
     * Executes the search and returns the sole result.
     *
     * @return The element of the type specified in the constructor
     * @throws NoSuchElementException
     *             if no unique element is found
     */
    public T single() {
        List<T> all = all();
        if (all.size() != 1) {
            throw new NoSuchElementException(getNoSuchElementMessage(null, all.size()));
        }
        return all.get(0);
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
     * @throws NoSuchElementException
     *             if no element is found
     *
     * @see #first()
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
     * @throws NoSuchElementException
     *             if no element is found
     *
     * @see #first()
     */
    public T waitForFirst(long timeOutInSeconds) {
        T result = new WebDriverWait(getDriver(),
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
            return result;
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

    private String getNoSuchElementMessage(Integer index, int foundCount) {
        String msg = (foundCount == 0 ? "No element" : "Multiple elements (" + foundCount + ")") +
                " with tag <" + tagName + "> found";
        String attrPairs = getAttributePairs();
        if (!attrPairs.isEmpty()) {
            msg += " with the attributes " + attrPairs;
        }
        if (index != null) {
            msg += " using index " + index;
        }
        return msg + ".";
    }

    private String getNoSuchElementMessage(Integer index) {
        return getNoSuchElementMessage(index, 0);
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

    private WebDriver getDriver() {
        var context = getContext();
        if (context instanceof WebDriver webDriver) {
            return webDriver;
        } else {
            return ((TestBenchElement) context).getDriver();
        }
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
        StringBuilder script = new StringBuilder();
        TestBenchElement elementContext;
        JavascriptExecutor executor;

        var context = getContext();
        if (context instanceof TestBenchElement testBenchElement) {
            script.append("var result = [];" //
                    + "if (arguments[0].shadowRoot) {" //
                    + "  var shadow = arguments[0].shadowRoot.querySelectorAll(arguments[1]+arguments[2]);" //
                    + "  result = result.concat(Array.prototype.slice.call(shadow));" //
                    + "}" //
                    + "var light = arguments[0].querySelectorAll(arguments[1]+arguments[2]);" //
                    + "result = result.concat(Array.prototype.slice.call(light));" //
                    + "return result" //
            );
            elementContext = testBenchElement;
            executor = elementContext.getCommandExecutor().getDriver();
        } else if (context instanceof WebDriver webDriver) {
            // Search the whole document
            script.append("var result = [];" //
                    + "const queryResult = document.querySelectorAll(arguments[1]+arguments[2]);"
                    + "result = result.concat(Array.prototype.slice.call(queryResult));"
                    + "return result");
            elementContext = null;
            executor = (JavascriptExecutor) webDriver;
        } else {
            if (context == null) {
                throw new IllegalStateException("Context cannot be null");
            } else {
                throw new IllegalStateException("Unknown context type: "
                        + context.getClass().getName());
            }
        }

        if (index != null) {
            script.append("[").append(index).append("]");
        }

        return executeSearchScript(script.toString(), elementContext, tagName,
                getAttributePairs(), executor);
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
        if (attrs.length == 0) {
            return Collections.emptySet();
        }

        Set<AttributeMatch> classAttributes = new HashSet<>();
        for (Attribute attr : attrs) {
            if (!Attribute.DEFAULT_VALUE.equals(attr.value())) {
                if (!Attribute.DEFAULT_VALUE.equals(attr.contains())) {
                    throw new RuntimeException(
                            "You can only define either 'contains' or 'value' for an @"
                                    + Attribute.class.getSimpleName());
                }
                String value = attr.value().equals(Attribute.SIMPLE_CLASS_NAME)
                        ? getClassConventionValue(elementClass)
                        : attr.value();
                // [label='my-text']
                classAttributes
                        .add(new AttributeMatch(attr.name(), MATCHES_EXACTLY, value));
            } else if (!Attribute.DEFAULT_VALUE.equals(attr.contains())) {
                // [class~='js-card-name']
                String value = attr.contains().equals(Attribute.SIMPLE_CLASS_NAME)
                        ? getClassConventionValue(elementClass)
                        : attr.contains();
                classAttributes
                        .add(new AttributeMatch(attr.name(), CONTAINS_TOKEN, value));
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
        return attributes.stream()
                .map(AttributeMatch::getExpression)
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
    @SuppressWarnings("unchecked")
    List<T> executeSearchScript(String script, Object context, String tagName,
            String attributePairs, JavascriptExecutor executor) {
        Object result = executor.executeScript(script, context, tagName,
                attributePairs);
        if (result == null) {
            return Collections.emptyList();
        } else if (result instanceof TestBenchElement testBenchElement) {
            return Collections.singletonList(TestBench.wrap(testBenchElement, elementClass));
        } else {
            List<TestBenchElement> elements = (List<TestBenchElement>) result;
            // Wrap as the correct type
            elements.replaceAll(element -> TestBench.wrap(element, elementClass));
            return (List<T>) elements;
        }
    }

}
