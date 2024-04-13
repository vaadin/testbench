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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
     * Selects on elements having the given attribute.
     * <p>
     *     Note, this attribute need not have a value.
     * </p>
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
     * For matching a token within the attribute, see
     * {@link #withAttributeContaining(String, String)}.
     *
     * @param name
     *            the attribute name
     * @param value
     *            the attribute value
     * @return this element query instance for chaining
     *
     * @see #withAttributeContaining(String, String)
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
     *
     * @param name
     *            the attribute name
     * @param token
     *            the token to look for
     * @return this element query instance for chaining
     *
     * @see #withAttribute(String, String)
     *
     * @deprecated use {@link #withAttributeContaining(String, String)}
     */
    @Deprecated(since = "9.3")
    public ElementQuery<T> attributeContains(String name, String token) {
        return withAttributeContaining(name, token);
    }

    /**
     * Selects on elements having the given attribute.
     * <p>
     *     Note, this attribute need not have a value.
     * </p>
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
     * Selects on elements with the given attribute having the given value.
     * <p>
     * For matching a token within the attribute, see
     * {@link #withAttributeContaining(String, String)}.
     * </p>
     *
     * @param attribute
     *            the attribute name
     * @param value
     *            the attribute value
     * @return this element query instance for chaining
     *
     * @see #withAttributeContaining(String, String)
     */
    public ElementQuery<T> withAttribute(String attribute, String value) {
        attributes.add(new AttributeMatch(attribute, value));
        return this;
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
     *
     * @param attribute
     *            the attribute name
     * @param value
     *            the token to look for
     * @return this element query instance for chaining
     *
     * @see #withAttribute(String, String)
     */
    public ElementQuery<T> withAttributeContaining(String attribute, String value) {
        attributes.add(new AttributeMatch(attribute, "~=", value));
        return this;
    }

    /**
     * Selects on elements not having the given attribute.
     * <p>
     *     Note, attributes both with and without values are skipped.
     * </p>
     *
     * @param attribute
     *            the attribute name
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withoutAttribute(String attribute) {
        attributes.add(new AttributeMatch(attribute, "!", null));
        return this;
    }

    /**
     * Selects on elements not having the given attribute with the given value.
     * <p>
     * For skipping elements having a token within the attribute, see
     * {@link #withoutAttributeContaining(String, String)}.
     * </p>
     *
     * @param attribute
     *            the attribute name
     * @param value
     *            the attribute value
     * @return this element query instance for chaining
     *
     * @see #withoutAttributeContaining(String, String)
     */
    public ElementQuery<T> withoutAttribute(String attribute, String value) {
        attributes.add(new AttributeMatch(attribute, "!=", value));
        return this;
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
     *
     * @param attribute
     *            the attribute name
     * @param value
     *            the token to look for
     * @return this element query instance for chaining
     *
     * @see #withoutAttribute(String, String)
     */
    public ElementQuery<T> withoutAttributeContaining(String attribute, String value) {
        attributes.add(new AttributeMatch(attribute, "!~=", value));
        return this;
    }

    /**
     * Selects on elements having the given id.
     * <p>
     *     This selector does not require the id to be unique.
     *     To obtain the unique id, chain with <code>{@link #single()}</code>
     *     or use <code>{@link #id(String)}</code> instead of this selector.
     *     If you legitimately have duplicate ids and just want the first one,
     *     chain with <code>{@link #first()}</code>.
     * </p>
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
                .forEach(className -> withAttributeContaining("class", className));
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
                .forEach(className -> withoutAttributeContaining("class", className));
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
     * Requires the element to satisfy the given condition.
     * <p>
     * For example, to select only enabled elements, you could use
     * <br>
     * {@code withCondition(TestBenchElement::isEnabled)}
     * <br>
     * or to select only those having a non-zero height, you could use
     * <br>
     * {@code withCondition(element -> element.getSize().getHeight() != 0)}
     * <p>
     * Note that conditions are evaluated in order
     * after the element is selected by its attributes.
     *
     * @param condition
     *            the condition for the element to satisfy
     * @return this element query instance for chaining
     */
    public ElementQuery<T> withCondition(Predicate<T> condition) {
        Objects.requireNonNull(condition, "condition must not be null");
        conditions.add(condition);
        return this;
    }

    /**
     * Requires the element's given property getter return value
     * to satisfy the given comparison with the supplied value.
     * <p>
     * For example, to select {@code TextFieldElement}s having helper text
     * containing the word "person",
     * you could use
     * <pre>
     * {@code withPropertyValue(TextFieldElement::getHelperText, "person", String::contains)}
     * </pre>
     *
     * @param getter
     *            the function to get the value of the property of the element,
     *            not null
     * @param propertyValue
     *            value to be compared with the one obtained from the
     *            getter function of the element
     * @param comparison
     *            the comparison to use when comparing the getter's property value
     *            against the supplied property value
     *            (i.e., {@code comparison.test(elementPropertyValue, propertyValue)}
     * @param <V>
     *            the type of the property values
     * @return this element query instance for chaining
     *
     * @see #withPropertyValue(Function, Object)
     */
    public <V> ElementQuery<T> withPropertyValue(Function<T, V> getter, V propertyValue,
                                                 BiPredicate<V, V> comparison) {
        Objects.requireNonNull(getter, "getter function must not be null");
        Objects.requireNonNull(comparison, "comparison must not be null");
        return withCondition(element -> comparison.test(getter.apply(element), propertyValue));
    }

    /**
     * Requires the element's given property getter return value
     * to equal the supplied value.
     *
     * @param getter
     *            the function to get the value of the property of the element,
     *            not null
     * @param propertyValue
     *            value to be compared with the one obtained from the
     *            getter function of the element
     * @param <V>
     *            the type of the property values
     * @return this element query instance for chaining
     *
     * @see #withPropertyValue(Function, Object, BiPredicate)
     */
    public <V> ElementQuery<T> withPropertyValue(Function<T, V> getter, V propertyValue) {
        return withPropertyValue(getter, propertyValue, Objects::equals);
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
     *          the comparison to use when comparing element's label
     *          against the supplied text
     *          (i.e., {@code comparison.test(elementLabel, text)}
     * @return this element query instance for chaining
     *
     * @see #withLabel(String)
     * @see #withLabelContaining(String)
     */
    public ElementQuery<T> withLabel(String text, BiPredicate<String, String> comparison) {
        Objects.requireNonNull(comparison, "comparison must not be null");
        return withCondition(element -> (element instanceof HasLabel hasLabel) &&
                comparison.test(hasLabel.getLabel(), text));
    }

    /**
     * Requires the element's label to exactly match the given label value.
     * <p>
     * For partially matching text within the label,
     * see {@link #withLabelContaining(String)}.
     *
     * @param label
     *            the label to match
     * @return this element query instance for chaining
     *
     * @see #withLabelContaining(String)
     * @see #withLabel(String, BiPredicate)
     */
    public ElementQuery<T> withLabel(String label) {
        return withLabel(label, String::equals);
    }

    /**
     * Requires the element's label to partially match the given text value.
     * <p>
     * For exactly matching the label,
     * see {@link #withLabel(String)}.
     *
     * @param text
     *          the text to match
     * @return this element query instance for chaining
     *
     * @see #withLabel(String)
     * @see #withLabel(String, BiPredicate)
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
     *          the comparison to use when comparing the placeholder
     *          against the supplied text
     *          (i.e., {@code comparison.test(elementPlaceholder, text)}
     * @return this element query instance for chaining
     *
     * @see #withPlaceholder(String)
     * @see #withPlaceholderContaining(String)
     */
    public ElementQuery<T> withPlaceholder(String text, BiPredicate<String, String> comparison) {
        Objects.requireNonNull(comparison, "comparison must not be null");
        return withCondition(element -> (element instanceof HasPlaceholder hasPlaceholder) &&
                comparison.test(hasPlaceholder.getPlaceholder(), text));
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
     *
     * @see #withPlaceholderContaining(String)
     * @see #withPlaceholder(String, BiPredicate)
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
     *
     * @see #withPlaceholder(String)
     * @see #withPlaceholder(String, BiPredicate)
     */
    public ElementQuery<T> withPlaceholderContaining(String text) {
        return withPlaceholder(text, String::contains);
    }

    /**
     * Requires the element's caption (i.e., label, placeholder, or text label)
     * to satisfy the given comparison with the supplied text.
     * <p>
     * If the element has a label, the label value is used.
     * If the element can have a label, but its value is empty and it has a placeholder,
     * the placeholder value is used.
     * If the element does not support labels nor placeholders
     * but does support labels via its text (such as a button),
     * the text is used.
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
     *          the comparison to use when comparing the caption
     *          against the supplied text
     *          (i.e., {@code comparison.test(elementCaption, text)}
     * @return this element query instance for chaining
     *
     * @see #withCaption(String)
     * @see #withCaptionContaining(String)
     */
    public ElementQuery<T> withCaption(String text, BiPredicate<String, String> comparison) {
        Objects.requireNonNull(comparison, "comparison must not be null");
        return withCondition(element -> {
            if (element instanceof HasLabel hasLabel) {
                var label = hasLabel.getLabel();
                if (!label.isEmpty()) {
                    return comparison.test(label, text);
                }
            }
            if (element instanceof HasPlaceholder hasPlaceholder) {
                var placeholder = hasPlaceholder.getPlaceholder();
                if (!placeholder.isEmpty()) {
                    return comparison.test(placeholder, text);
                }
            }
            if (element instanceof HasLabelAsText labelAsText) {
                var label = Objects.requireNonNullElse(labelAsText.getText(), "");
                if (!label.isEmpty()) {
                    return comparison.test(label, text);
                }
            }
            return false;
        });
    }

    /**
     * Requires the element's caption (i.e., label, placeholder, or text label)
     * to exactly match the given caption value.
     * <p>
     * For partially matching text within the caption, see
     * {@link #withCaptionContaining(String)}.
     *
     * @param caption
     *            the caption to match
     * @return this element query instance for chaining
     *
     * @see #withCaptionContaining(String)
     * @see #withCaption(String, BiPredicate)
     */
    public ElementQuery<T> withCaption(String caption) {
        return withCaption(caption, String::equals);
    }

    /**
     * Requires the element's caption (i.e., label, placeholder, or text label)
     * to partially match the given text value.
     * <p>
     * For exactly matching the caption, see
     * {@link #withCaption(String)}.
     *
     * @param text
     *          the text to match
     * @return this element query instance for chaining
     *
     * @see #withCaption(String)
     * @see #withCaption(String, BiPredicate)
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
     *          the comparison to use when comparing the element's text
     *          against the supplied text
     *          (i.e., {@code comparison.test(elementText, text)}
     * @return this element query instance for chaining
     *
     * @see #withText(String)
     * @see #withTextContaining(String)
     */
    public ElementQuery<T> withText(String text, BiPredicate<String, String> comparison) {
        Objects.requireNonNull(comparison, "comparison must not be null");
        return withCondition(element -> comparison.test(Objects.requireNonNullElse(element.getText(), ""), text));
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
     *
     * @see #withTextContaining(String)
     * @see #withText(String, BiPredicate)
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
     *
     * @see #withText(String)
     * @see #withText(String, BiPredicate)
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
     * Executes the search and returns an element having the given unique id.
     * <p>
     *     This selector expects the id to be unique.
     *     If there are duplicate ids, this selector will
     *     throw an exception. If you legitimately have duplicate ids,
     *     use <code>{@link #withId(String)}.{@link #first()}</code> instead.
     *     (Note, this alternate usage is the former behavior of this selector.)
     * </p>
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
        Objects.checkIndex(index, Integer.MAX_VALUE);
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
