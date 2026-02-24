/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.browserless;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.dom.Element;

import static com.vaadin.browserless.ElementConditions.containsText;
import static com.vaadin.browserless.ElementConditions.hasAttribute;
import static com.vaadin.browserless.ElementConditions.hasNotAttribute;

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
    void containsText_Html_checksHasTextInInnerHtml() {
        Html component = new Html("<p>this is <b>the content</b></p>");
        Assertions.assertTrue(containsText("").test(component));

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
    void containsText_HtmlContainer_checksHasTextInInnerHtml() {
        Article component = new Article(new TextComponent("this is"),
                new NonTextComponent(" the "), new TextComponent("content"));
        Assertions.assertTrue(containsText("").test(component));

        Assertions.assertTrue(containsText("").test(component));
        Assertions.assertTrue(containsText("is").test(component));
        Assertions.assertTrue(containsText("this").test(component));
        Assertions.assertTrue(containsText("content").test(component));
        Assertions.assertTrue(containsText("is the").test(component));
        Assertions.assertTrue(
                containsText("this is the content").test(component));

        Assertions.assertFalse(containsText("some text").test(component));
        Assertions.assertFalse(containsText("CONTENT").test(component));
    }

    @Test
    void containsText_nonTextNode_checksTextRecursively() {
        NonTextComponent component = new NonTextComponent(
                "this is the content");
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
    void containsText_nonTextNodeWithChildren_checksTextRecursively() {
        NonTextComponent component = new NonTextComponent(
                new NonTextComponent("this is"),
                new TextComponent("the content"));
        Assertions.assertTrue(containsText("").test(component));
        Assertions.assertTrue(containsText("is").test(component));
        Assertions.assertTrue(containsText("this").test(component));
        Assertions.assertTrue(containsText("content").test(component));
        Assertions.assertTrue(containsText("isthe").test(component));
        Assertions
                .assertTrue(containsText("this isthe content").test(component));
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

        Assertions.assertTrue(containsText("THIS is THE content", true)
                .test(new Html("<p>this <b>IS</b> the <b>CONTENT</b></p>")));

    }

    @Test
    void hasAttribute_checksAttributePresence() {
        TestComponent component = new TestComponent();
        component.getElement().setAttribute("string-attribute", "primary");
        component.getElement().setAttribute("empty-attribute", "");
        component.getElement().setAttribute("true-attribute", true);
        component.getElement().setAttribute("false-attribute", false);

        Assertions.assertTrue(hasAttribute("string-attribute").test(component));
        Assertions.assertTrue(hasAttribute("empty-attribute").test(component));
        Assertions.assertTrue(hasAttribute("true-attribute").test(component));

        Assertions.assertFalse(hasAttribute("false-attribute").test(component));

        Assertions
                .assertFalse(hasAttribute("not-set-attribute").test(component));

    }

    @Test
    void hasAttribute_expectedValue_checksAttributeHasExactlyExpectedValue() {
        TestComponent component = new TestComponent();
        component.getElement().setAttribute("string-attribute", "primary");
        component.getElement().setAttribute("empty-attribute", "");
        component.getElement().setAttribute("true-attribute", true);
        component.getElement().setAttribute("false-attribute", false);

        Assertions.assertTrue(
                hasAttribute("string-attribute", "primary").test(component));
        Assertions.assertFalse(
                hasAttribute("string-attribute", "PRIMARY").test(component));
        Assertions.assertFalse(
                hasAttribute("string-attribute", "").test(component));

        Assertions.assertTrue(
                hasAttribute("empty-attribute", "").test(component));

        Assertions
                .assertTrue(hasAttribute("true-attribute", "").test(component));
        Assertions.assertFalse(
                hasAttribute("true-attribute", "true").test(component));
        Assertions.assertFalse(
                hasAttribute("false-attribute", "").test(component));

        Assertions.assertFalse(
                hasAttribute("not-set-attribute", "").test(component));
        Assertions.assertFalse(
                hasAttribute("not-set-attribute", "test").test(component));
    }

    @Test
    void hasAttribute_nullExpectedValue_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> hasAttribute("attr", null));
    }

    @Test
    void hasNotAttribute_checksAttributeAbsence() {
        TestComponent component = new TestComponent();
        component.getElement().setAttribute("string-attribute", "primary");
        component.getElement().setAttribute("empty-attribute", "");
        component.getElement().setAttribute("true-attribute", true);
        component.getElement().setAttribute("false-attribute", false);

        Assertions.assertFalse(
                hasNotAttribute("string-attribute").test(component));
        Assertions.assertFalse(
                hasNotAttribute("empty-attribute").test(component));
        Assertions
                .assertFalse(hasNotAttribute("true-attribute").test(component));

        Assertions
                .assertTrue(hasNotAttribute("false-attribute").test(component));
        Assertions.assertTrue(
                hasNotAttribute("not-set-attribute").test(component));

    }

    @Test
    void hasNotAttribute_expectedValue_checksAttributeValueIsDifferentFromGiven() {
        TestComponent component = new TestComponent();
        component.getElement().setAttribute("string-attribute", "primary");
        component.getElement().setAttribute("empty-attribute", "");
        component.getElement().setAttribute("true-attribute", true);
        component.getElement().setAttribute("false-attribute", false);

        Assertions.assertFalse(
                hasNotAttribute("string-attribute", "primary").test(component));
        Assertions.assertTrue(
                hasNotAttribute("string-attribute", "PRIMARY").test(component));
        Assertions.assertTrue(
                hasNotAttribute("string-attribute", "").test(component));

        Assertions.assertFalse(
                hasNotAttribute("empty-attribute", "").test(component));

        Assertions.assertFalse(
                hasNotAttribute("true-attribute", "").test(component));
        Assertions.assertTrue(
                hasNotAttribute("true-attribute", "true").test(component));
        Assertions.assertTrue(
                hasNotAttribute("false-attribute", "").test(component));

        Assertions.assertTrue(
                hasNotAttribute("not-set-attribute", "").test(component));
        Assertions.assertTrue(
                hasNotAttribute("not-set-attribute", "test").test(component));
    }

    @Test
    void hasNotAttribute_nullExpectedValue_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> hasNotAttribute("attr", null));
    }

    @Tag("span")
    static class NonTextComponent extends Component implements HasComponents {
        public NonTextComponent(String text) {
            getElement().setText(text);
        }

        public NonTextComponent(Component... components) {
            add(components);
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

    @Tag("article")
    public static class Article extends HtmlComponent implements HasComponents {
        public Article(Component... components) {
            add(components);
        }
    }
}
