/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Selenium's {@code By}, the CSS kinds only, so that a test using
 * {@code findElement(By)} ports by changing its import:
 *
 * <pre>{@code
 * element.findElement(By.className("content"));
 * element.$("*").withClassName("content").single(); // the $ form
 * }</pre>
 *
 * The two search different places: {@code findElement(By)} searches the
 * element's light DOM only, {@code $} its shadow root and its light DOM. Each
 * factory names its {@code $} form. XPath has none and is not here; it is
 * Selenium-only.
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
    private final String cssSelector;

    private By(String description, String cssSelector) {
        this.description = description;
        this.cssSelector = cssSelector;
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
        return cssSelector;
    }

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
