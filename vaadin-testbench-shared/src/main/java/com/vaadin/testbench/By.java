/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/**
 * Selenium's {@code By}, the CSS kinds and XPath, so that a test using
 * {@code findElement(By)} ports by changing its import:
 *
 * <pre>{@code
 * element.findElement(By.className("content"));
 * element.$("*").withClassName("content").single(); // the $ form
 * }</pre>
 *
 * The two search different places: {@code findElement(By)} searches the
 * element's light DOM only, {@code $} its shadow root and its light DOM. Each
 * CSS factory names its {@code $} form; XPath has none.
 * <p>
 * Equal when {@link #toString()} is, as Selenium's are. Immutable.
 */
public final class By {

    // Selenium's escaping for By.id and By.className (By.java, selenium-api
    // 4.52.0), so a ported locator selects what it selected before.
    private static final Pattern CSS_ESCAPE = Pattern
            .compile("([\\s'\"\\\\#.:;,!?+<>=~*^$|%&@`{}\\-\\/\\[\\]\\(\\)])");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    private final String description;
    private final @Nullable String cssSelector;
    private final @Nullable String xpath;

    private By(String description, String cssSelector) {
        this.description = description;
        this.cssSelector = cssSelector;
        this.xpath = null;
    }

    private By(String xpath) {
        this.description = "By.xpath: " + xpath;
        this.cssSelector = null;
        this.xpath = xpath;
    }

    /**
     * Elements by an XPath expression, which the browser evaluates, from the
     * element searched in or from the document, as Selenium's does: a
     * {@code ".."} finds the parent, a {@code "//"} searches the whole page.
     * Like CSS, it does not see inside shadow roots.
     */
    public static By xpath(String xpathExpression) {
        Objects.requireNonNull(xpathExpression,
                "Cannot find elements when the XPath is null.");
        return new By(xpathExpression);
    }

    /**
     * The {@code $} form: {@code $(ButtonElement.class).id(id)}, or
     * {@code $("*").id(id)} when no element class fits. Unlike this, {@code id}
     * fails on a duplicate id.
     */
    public static By id(String id) {
        Objects.requireNonNull(id, "Cannot find elements when id is null.");
        return new By("By.id: " + id, "#" + cssEscape(id));
    }

    /**
     * The {@code $} form: {@code $("tag")}.
     */
    public static By tagName(String tagName) {
        Objects.requireNonNull(tagName,
                "Cannot find elements when the tag name is null.");
        if (tagName.isEmpty()) {
            throw new IllegalArgumentException("Tag name must not be blank");
        }
        return new By("By.tagName: " + tagName, tagName);
    }

    /**
     * The {@code $} form:
     * {@code $(ButtonElement.class).withClassName(className)}.
     *
     * @param className
     *            one class name; for several, use {@link #cssSelector(String)}
     * @throws IllegalArgumentException
     *             if {@code className} holds whitespace
     */
    public static By className(String className) {
        Objects.requireNonNull(className,
                "Cannot find elements when the class name expression is null.");
        if (WHITESPACE.matcher(className).find()) {
            throw new IllegalArgumentException(
                    "Compound class names not permitted");
        }
        return new By("By.className: " + className, "." + cssEscape(className));
    }

    /**
     * The {@code $} form: {@code $("tag")} with its filters:
     * {@code By.cssSelector("vaadin-button.primary")} becomes
     * {@code $("vaadin-button").withClassName("primary")}, and attributes take
     * {@code withAttribute}. {@code $} takes a tag name, not a selector, so a
     * selector beyond tag, class and attributes has no {@code $} form.
     */
    public static By cssSelector(String cssSelector) {
        Objects.requireNonNull(cssSelector,
                "Cannot find elements when the selector is null");
        return new By("By.cssSelector: " + cssSelector, cssSelector);
    }

    /**
     * The CSS selector a backend searches with:
     *
     * <pre>{@code
     * By.id("save").toCssSelector(); // => "#save"
     * By.className("a.b").toCssSelector(); // => ".a\.b"
     * }</pre>
     */
    public String toCssSelector() {
        if (cssSelector == null) {
            throw new UnsupportedOperationException(
                    description + " has no CSS form");
        }
        return cssSelector;
    }

    /**
     * What {@code by} finds from {@code context}, or the whole page when it is
     * {@code null}: CSS through the backend, XPath through a script.
     */
    static List<TestBenchElement> find(By by, TestBenchCommandExecutor executor,
            @Nullable TestBenchElement context) {
        if (by.xpath == null) {
            return context == null ? findCss(by.cssSelector, executor)
                    : context.getElementBackend().findElements(by.cssSelector)
                            .stream()
                            .map(found -> TestBench.createElement(
                                    TestBenchElement.class, found, executor))
                            .toList();
        }
        Object found = executor.executeScript(XPATH_SCRIPT, by.xpath, context);
        if (found instanceof String notAnElement) {
            throw new IllegalArgumentException(
                    "The result of the XPath " + by.xpath + " is: "
                            + notAnElement + ". It should be an element.");
        }
        return castToElements(found);
    }

    private static List<TestBenchElement> findCss(String cssSelector,
            TestBenchCommandExecutor executor) {
        return castToElements(executor.executeScript(
                "return Array.from(document.querySelectorAll(arguments[0]))",
                cssSelector));
    }

    @SuppressWarnings("unchecked")
    private static List<TestBenchElement> castToElements(Object found) {
        return (List<TestBenchElement>) found;
    }

    // WebDriver's XPath search: the nodes in document order, each an element
    private static final String XPATH_SCRIPT = """
            var result = document.evaluate(arguments[0], arguments[1] || document,
                null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
            var found = [];
            for (var i = 0; i < result.snapshotLength; i++) {
              var node = result.snapshotItem(i);
              if (node.nodeType !== Node.ELEMENT_NODE) {
                return String(node);
              }
              found.push(node);
            }
            return found;
            """;

    private static String cssEscape(String value) {
        String escaped = CSS_ESCAPE.matcher(value).replaceAll("\\\\$1");
        // A leading ASCII digit needs a code point escape; other Unicode
        // digits are valid at the start of an identifier.
        char first = escaped.isEmpty() ? '\0' : escaped.charAt(0);
        if (first >= '0' && first <= '9') {
            escaped = "\\" + (30 + (first - '0')) + " " + escaped.substring(1);
        }
        return escaped;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof By other && description.equals(other.description);
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }

    /** As Selenium's: {@code "By.id: save"}. */
    @Override
    public String toString() {
        return description;
    }
}
