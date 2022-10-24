/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

import org.jsoup.Jsoup;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.dom.Element;

/**
 * A collection of commons predicates to be used as {@link ComponentQuery}
 * conditions.
 *
 * @see ComponentQuery#withCondition(Predicate)
 */
public final class ElementConditions {

    private ElementConditions() {
        throw new AssertionError("Must not be instantiated");
    }

    /**
     * Checks if text content of the component contains the given text.
     *
     * Input text is compared with value obtained either by
     * {@link HasText#getText()}, {@link Element#getText()} if element is a text
     * node, or the normalized version of {@link Html#getInnerHtml()}. In all
     * other cases {@link Element#getTextRecursively()} is used, but in this
     * case text from nested elements is concatenated without space separators.
     * The comparison is case-sensitive.
     *
     * For {@link Html} components the {@literal innerHTML} tags are stripped
     * and whitespace is normalized and trimmed.
     *
     * For example, given HTML
     *
     * <pre>
     * <p>
     * Hello  <b>there</b> now!
     * </p>
     * </pre>
     *
     * the text that will be checked will be {@literal  Hello there now!}.
     *
     * @param text
     *            the text the component is expected to have as its content. Not
     *            {@literal null}.
     * @return this element query instance for chaining
     * @see HasText#getText()
     * @see Element#isTextNode()
     * @see Element#getText()
     * @see Element#getTextRecursively()
     * @see Html#getInnerHtml()
     */
    public static <T extends Component> Predicate<T> containsText(String text) {
        return containsText(text, false);
    }

    /**
     * Checks if text content of the component contains the given text.
     *
     * Input text is compared with value obtained either by
     * {@link HasText#getText()}, {@link Element#getText()} if element is a text
     * node, or {@link Html#getInnerHtml()}. In all other cases
     * {@link Element#getTextRecursively()} is used, but in this case text from
     * nested elements is concatenated without space separators.
     *
     * For {@link Html} components the {@literal innerHTML} tags are stripped
     * and whitespace is normalized and trimmed.
     *
     * For example, given HTML
     *
     * <pre>
     * <p>
     * Hello  <b>there</b> now!
     * </p>
     * </pre>
     *
     * the text that will be checked will be {@literal  Hello there now!}.
     *
     * @param text
     *            the text the component is expected to have as its content. Not
     *            {@literal null}.
     * @param ignoreCase
     *            flag to indicate if comparison must be case-insensitive.
     * @return this element query instance for chaining
     * @see HasText#getText()
     * @see Element#isTextNode()
     * @see Element#getText()
     * @see Element#getTextRecursively()
     * @see Html#getInnerHtml()
     */
    public static <T extends Component> Predicate<T> containsText(String text,
            boolean ignoreCase) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        return new TextContainsPredicate<>(text, ignoreCase);
    }

    /**
     * Checks if the given attribute has been set on the component.
     *
     * Attribute names are considered case-insensitive and all names will be
     * converted to lower case automatically.
     *
     * @param attribute
     *            the name of the attribute, not {@literal null}
     * @return {@literal true} if the attribute has been set, {@literal false}
     *         otherwise
     */
    public static <T extends Component> Predicate<T> hasAttribute(
            String attribute) {
        return component -> component.getElement().hasAttribute(attribute);
    }

    /**
     * Checks if the given attribute has been set on the component and has
     * exactly the given value.
     *
     * Attribute names are considered case-insensitive and all names will be
     * converted to lower case automatically.
     *
     * @param attribute
     *            the name of the attribute, not {@literal null}
     * @param value
     *            expected value, not {@literal null}
     * @return {@literal true} if the attribute has been set, {@literal false}
     *         otherwise
     */
    public static <T extends Component> Predicate<T> hasAttribute(
            String attribute, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return component -> Objects
                .equals(component.getElement().getAttribute(attribute), value);
    }

    /**
     * Checks if the given attribute has not been set on the component.
     *
     * Attribute names are considered case-insensitive and all names will be
     * converted to lower case automatically.
     *
     * @param attribute
     *            the name of the attribute, not {@literal null}
     * @return {@literal true} if the attribute has not been set,
     *         {@literal false} otherwise
     */
    public static <T extends Component> Predicate<T> hasNotAttribute(
            String attribute) {
        return component -> !component.getElement().hasAttribute(attribute);
    }

    /**
     * Checks if the given attribute has been set on the component or has a
     * value different from given one.
     *
     * Attribute names are considered case-insensitive and all names will be
     * converted to lower case automatically.
     *
     * @param attribute
     *            the name of the attribute, not {@literal null}
     * @param value
     *            value expected not to be set on attribute, not {@literal null}
     * @return {@literal true} if the attribute is not set or has a value
     *         different from given one, {@literal false} otherwise
     */
    public static <T extends Component> Predicate<T> hasNotAttribute(
            String attribute, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return component -> !Objects
                .equals(component.getElement().getAttribute(attribute), value);
    }

    private static class TextContainsPredicate<T extends Component>
            implements Predicate<T> {

        private final String text;
        private final boolean ignoreCase;

        public TextContainsPredicate(String text, boolean ignoreCase) {
            this.text = text;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public boolean test(T component) {
            String componentText;
            if (component instanceof HasText) {
                componentText = ((HasText) component).getText();
            } else if (component instanceof HtmlComponent) {
                componentText = component.getElement().getTextRecursively();
            } else if (component instanceof Html) {
                // Strip tags and normalize text
                componentText = ((Html) component).getInnerHtml();
                if (componentText != null) {
                    componentText = Jsoup.parse(componentText).text();
                }
            } else if (component.getElement().isTextNode()) {
                componentText = component.getElement().getText();
            } else {
                componentText = component.getElement().getTextRecursively();
            }
            if (componentText == null) {
                return false;
            }
            // WARN: may not work correctly with unicode chars
            if (ignoreCase) {
                return componentText.toLowerCase(Locale.ROOT)
                        .contains(text.toLowerCase(Locale.ROOT));
            }
            return componentText.contains(text);
        }
    }
}
