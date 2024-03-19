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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
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
 * searched for and also the type of element returned.
 */
public class ElementQuery<T extends TestBenchElement> {

    private static final int DEFAULT_WAIT_TIME_OUT_IN_SECONDS = 10;

    public static class AttributeMatch {
        private final String name;
        private final String operator;
        private final String value;
        private final boolean negate;

        public AttributeMatch(String name, String operator, String value) {
            this.name = name;

            String oper = null;
            boolean neg = false;
            if (operator != null) {
                neg = operator.startsWith("!");
                oper = neg ? operator.substring(1) : operator;
                oper = oper.isEmpty() ? null : oper;
            }

            this.operator = oper;
            this.value = value;
            this.negate = neg;
        }

        public AttributeMatch(String name, String value) {
            this(name, "=", value);
        }

        public AttributeMatch(String name) {
            this(name, null, null);
        }

        @Override
        public String toString() {
            return getExpression();
        }

        public String getExpression() {
            String expression = name;

            if (value != null) {
                expression += operator + "'" + escapeAttributeValue(value) + "'";
            }

            expression = "[" + expression + "]";

            if (negate) {
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
        return withId(id).single();
    }

    /**
     * Requires the given attribute to be set.
     *
     * @param name
     *            the attribute name
     * @return this element query instance for chaining
     *
     * @deprecated use {@link #withAttribute}
     */
    @Deprecated(since = "9.2")
    public ElementQuery<T> hasAttribute(String name) {
        return withAttribute(name);
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
     *
     * @deprecated use {@link #withAttribute}
     */
    @Deprecated(since = "9.2")
    public ElementQuery<T> attribute(String name, String value) {
        return withAttribute(name, value);
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
     *
     * @deprecated use {@link #withAttributeContaining}
     */
    @Deprecated(since = "9.2")
    public ElementQuery<T> attributeContains(String name, String token) {
        return withAttributeContaining(name, token);
    }

//    public ElementQuery<T> withCondition(Predicate<T> condition) {
//        // TODO implement
//        return this;
//    }

    /**
     * Requires the given attribute to be set.
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
     * Requires the given attribute to match the given value.
     * <p>
     * For matching a token in the attribute, see
     * {@link #attributeContains(String, String)}.
     *
     * @param attribute
     *            the attribute name
     * @param value
     *            the attribute value
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withAttribute(String attribute, String value) {
        attributes.add(new AttributeMatch(attribute, value));
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
     * @param attribute
     *            the attribute name
     * @param value
     *            the value to look for
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withAttributeContaining(String attribute, String value) {
        attributes.add(new AttributeMatch(attribute, "~=", value));
        return this;
    }

    public ElementQuery<T> withoutAttribute(String attribute) {
        attributes.add(new AttributeMatch(attribute, "!"));
        return this;
    }

    public ElementQuery<T> withoutAttribute(String attribute, String value) {
        attributes.add(new AttributeMatch(attribute, "!=", value));
        return this;
    }

    public ElementQuery<T> withoutAttributeContaining(String attribute, String value) {
        attributes.add(new AttributeMatch(attribute, "!~=", value));
        return this;
    }

    public ElementQuery<T> withId(String id) {
        return withAttribute("id", id);
    }

    public ElementQuery<T> withClassName(String... classNames) {
        Arrays.stream(classNames)
                .forEach(className -> withAttributeContaining("class", className));
        return this;
    }

    public ElementQuery<T> withoutClassName(String... classNames) {
        Arrays.stream(classNames)
                .forEach(className -> withoutAttributeContaining("class", className));
        return this;
    }

    public ElementQuery<T> withTheme(String theme) {
        return withAttribute("theme", theme);
    }

    public ElementQuery<T> withoutTheme(String theme) {
        return withoutAttribute("theme", theme);
    }

    public ElementQuery<T> withCaption(String caption) {
        return withAttribute("label", caption);
    }

    public ElementQuery<T> withCaptionContaining(String text) {
        return withAttributeContaining("label", text);
    }

//    public ElementQuery<T> withText(String text) {
//        // TODO implement
//        return this;
//    }
//
//    public ElementQuery<T> withTextContaining(String text) {
//        // TODO implement
//        return this;
//    }
//
//    public <V> ElementQuery<T> withValue(V value) {
//        // TODO implement
//        return this;
//    }

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
     * Executes the search and returns the sole result.
     *
     * @return The element of the type specified in the constructor
     * @throws NoSuchElementException
     *             if no element is found
     */
    public T single() {
        List<T> all = all();
        if (all.size() != 1) {
            throw new NoSuchElementException(getNoSuchElementMessage(null));
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

    private WebDriver getDriver() {
        var context = getContext();
        if (context instanceof WebDriver webDriver) {
            return webDriver;
        } else {
            return ((TestBenchElement) context).getDriver();
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
            String toMatch;
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
                        .add(new AttributeMatch(attr.name(), "=", value));
            } else if (!Attribute.DEFAULT_VALUE.equals(attr.contains())) {
                // [class~='js-card-name']
                String value = attr.contains().equals(Attribute.SIMPLE_CLASS_NAME)
                        ? getClassConventionValue(elementClass)
                        : attr.contains();
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
