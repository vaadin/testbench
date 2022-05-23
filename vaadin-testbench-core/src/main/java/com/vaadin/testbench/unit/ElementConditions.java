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

import java.util.Locale;
import java.util.function.Predicate;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
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
     * {@link HasText#getText()} or {@link Element#getText()}, if element is a
     * text node. In all other cases the predicate returns {@literal false}.
     * Comparison is case-sensitive.
     *
     * @param text
     *            the text the component is expected to have as its content. Not
     *            {@literal null}.
     * @return this element query instance for chaining
     * @see HasText#getText()
     * @see Element#getText()
     * @see Element#isTextNode()
     */
    public static <T extends Component> Predicate<T> containsText(String text) {
        return containsText(text, false);
    }

    /**
     * Checks if text content of the component contains the given text.
     *
     * Input text is compared with value obtained either by
     * {@link HasText#getText()} or {@link Element#getText()}, if element is a
     * text node. In all other cases the predicate returns {@literal false}.
     *
     * @param text
     *            the text the component is expected to have as its content. Not
     *            {@literal null}.
     * @param ignoreCase
     *            flag to indicate if comparison must be case-insensitive.
     * @return this element query instance for chaining
     * @see HasText#getText()
     * @see Element#getText()
     * @see Element#isTextNode()
     */
    public static <T extends Component> Predicate<T> containsText(String text,
            boolean ignoreCase) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        return new TextContainsPredicate<>(text, ignoreCase);
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
            } else if (component.getElement().isTextNode()) {
                componentText = component.getElement().getText();
            } else {
                return false;
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
