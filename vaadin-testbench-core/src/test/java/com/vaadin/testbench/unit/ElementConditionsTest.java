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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;

import static com.vaadin.testbench.unit.ElementConditions.containsText;

class ElementConditionsTest {

    @Test
    void containsText_null_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> containsText(null));
    }

    @Test
    void containsText_textNode_checksElementGetText() {
        TextComponent component = new TextComponent("this is the content");
        Assertions.assertTrue(containsText("").test(component));
        Assertions.assertTrue(containsText("is").test(component));
        Assertions.assertTrue(containsText("this").test(component));
        Assertions.assertTrue(containsText("content").test(component));
        Assertions.assertTrue(containsText(" is the ").test(component));
        Assertions.assertTrue(
                containsText("this is the content").test(component));

        Assertions.assertFalse(containsText("some text").test(component));
        Assertions.assertFalse(containsText("CONTENT").test(component));
    }

    @Test
    void containsText_HasText_checksHasTextGetText() {
        HasTextComponent component = new HasTextComponent(
                "this is the content");
        Assertions.assertTrue(containsText("").test(component));

        Assertions.assertTrue(containsText("IS").test(component));
        Assertions.assertTrue(containsText("THIS").test(component));
        Assertions.assertTrue(containsText("CONTENT").test(component));
        Assertions.assertTrue(containsText(" IS THE ").test(component));
        Assertions.assertTrue(
                containsText("THIS IS THE CONTENT").test(component));

        Assertions.assertFalse(containsText("is").test(component));
        Assertions.assertFalse(containsText("this").test(component));
        Assertions.assertFalse(containsText("content").test(component));
        Assertions.assertFalse(containsText(" is the ").test(component));
        Assertions.assertFalse(
                containsText("this is the content").test(component));
    }

    @Test
    void containsText_nonTextNode_alwaysFalse() {
        NonTextComponent component = new NonTextComponent(
                "this is the content");
        Assertions.assertFalse(containsText("").test(component));
        Assertions.assertFalse(containsText("is").test(component));
        Assertions.assertFalse(containsText("this").test(component));
        Assertions.assertFalse(containsText("content").test(component));
        Assertions.assertFalse(containsText(" is the ").test(component));
        Assertions.assertFalse(
                containsText("this is the content").test(component));
        Assertions.assertFalse(containsText("some text").test(component));
        Assertions.assertFalse(containsText("CONTENT").test(component));
    }

    @Test
    void containsText_ignoreCase() {
        TextComponent component = new HasTextComponent("this IS the CONTENT");
        Assertions.assertTrue(containsText("", true).test(component));

        Assertions.assertTrue(containsText("IS", true).test(component));
        Assertions.assertTrue(containsText("THIS", true).test(component));
        Assertions.assertTrue(containsText("CONTENT", true).test(component));
        Assertions.assertTrue(containsText(" IS THE ", true).test(component));
        Assertions.assertTrue(
                containsText("THIS IS THE CONTENT", true).test(component));

        Assertions.assertTrue(containsText("is", true).test(component));
        Assertions.assertTrue(containsText("this", true).test(component));
        Assertions.assertTrue(containsText("content", true).test(component));
        Assertions.assertTrue(containsText(" is the ", true).test(component));
        Assertions.assertTrue(
                containsText("this is the content", true).test(component));

        Assertions.assertTrue(
                containsText("this IS the CONTENT", true).test(component));
        Assertions.assertTrue(
                containsText("THIS is THE content", true).test(component));

    }

    @Tag("span")
    static class NonTextComponent extends Component {
        public NonTextComponent(String text) {
            getElement().setText(text);
        }
    }

    @Tag("span")
    static class TextComponent extends Component {
        public TextComponent(String text) {
            super(Element.createText(text));
        }

        @Override
        public boolean isVisible() {
            // Workaround to bypass BasicUtils.kt _isVisible method that fails
            // with UnsupportedOperationException if text node is not of type
            // Text
            return true;
        }
    }

    @Tag("span")
    static class HasTextComponent extends TextComponent implements HasText {

        public HasTextComponent(String text) {
            super(text);
        }

        @Override
        public String getText() {
            return HasText.super.getText().toUpperCase();
        }
    }
}
