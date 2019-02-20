/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import com.vaadin.testbench.internal.SharedUtil;

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

    /**
     * Linked to ensure that elements are always returned in the same order.
     */
    private Set<AttributeMatch> attributes = new LinkedHashSet<>();
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
        return attribute("id", id).first();
    }

    /**
     * Requires the given attribute to match the given value.
     * <p>
     * For matching a token in the attribute, see
     * {@link #attributeContains(String , String)}.
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
     * {@link #attribute(String , String)}.
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
     * Return the context (element or driver) to search inside.
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
     * @throws TimeoutException
     *             if no element is found after 10 seconds has elapsed
     */
    public T waitForFirst() {
        Object result = new WebDriverWait(getDriver(), 10).until(driver -> {
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
     * list of matchin elements.
     *
     * @param index
     *            the index of the element to return or <code>null</code> to
     *            return all matching elements
     * @return a list of macthing elements or an empty list if no matches were
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
                    + CyclicObjectWorkaround.get("result") //
                    + "return result" //
            );
            elementContext = (TestBenchElement) getContext();
            executor = elementContext.getCommandExecutor().getDriver();
        } else if (getContext() instanceof WebDriver) {
            // Search the whole document
            script.append("var result = [];" //
                    + "const queryResult = document.querySelectorAll(arguments[1]+arguments[2]);"
                    + "result = result.concat(Array.prototype.slice.call(queryResult));"
                    + CyclicObjectWorkaround.get("result") //
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
        Object result = executor.executeScript(script, context, tagName,
                                               attributePairs);
        if (result == null) {
            return Collections.emptyList();
        } else if (result instanceof TestBenchElement) {
            return Collections.singletonList(
                    TestBench.wrap((TestBenchElement) result, elementClass));
        } else {
            List<TestBenchElement> elements = (List<TestBenchElement>) result;
            // Wrap as the correct type
            for (int i = 0; i < elements.size(); i++) {
                T wrapped = TestBench.wrap(elements.get(i), elementClass);
                elements.set(i, wrapped);
            }
            return (List) elements;
        }
    }

}
