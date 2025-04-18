/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.TemplateView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.elements.LabelPlaceholderElement;
import com.vaadin.tests.elements.NativeButtonElement;
import com.vaadin.tests.elements.TemplateViewElement;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static com.vaadin.testbench.ElementQuery.AttributeMatch.Comparison.BEGINS_WITH;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Comparison.ENDS_WITH;
import static com.vaadin.testbench.ElementQuery.AttributeMatch.Comparison.NOT_CONTAINS_PREFIX;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ElementQueryIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return TemplateView.class;
    }

    @BrowserTest
    void ensureElementListWrapped() {
        openTestURL();
        List<TemplateViewElement> elements = $(TemplateViewElement.class).all();
        assertNotNull(elements.get(0));
    }

    @BrowserTest
    void ensureElementListFromOnPageWrapped() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).first();
        TemplateViewElement view2 = view.$(TemplateViewElement.class).onPage()
                .first();
        Assertions.assertEquals(view, view2);
    }

    @BrowserTest
    void findLightDomElementById() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).first();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("light-button-1");
        Assertions.assertEquals("Button 1", button.getText());
    }

    @BrowserTest
    void findShadowDomElementById() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("shadow-button-1");
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @BrowserTest
    void findAllShadowDomElements() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        Assertions.assertEquals(10, view.$(NativeButtonElement.class).all().size());
    }

    @BrowserTest
    void searchShadowDomBeforeLight() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .withId("special-button")
                .first();
        Assertions.assertEquals("Special Button (in Shadow DOM)", button.getText());
    }

    @BrowserTest
    void mergeLightAndShadowDomResults() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                .all();
        Assertions.assertEquals(10, buttons.size());
    }

    @BrowserTest
    void findTestBenchElementUsingTag() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$("button").id("shadow-button-2");
        Assertions.assertEquals("Shadow Button 2", button.getText());

    }

    @BrowserTest
    void findTestBenchElement() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$(TestBenchElement.class).id("shadow-button-2");
        assertNotNull(button);
    }

    @BrowserTest
    void findTestBenchElementChild() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$(TestBenchElement.class).first().$(TestBenchElement.class)
                .first();
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @BrowserTest
    void specialCharactersInId() {
        openTestURL();
        NativeButtonElement button = $(TemplateViewElement.class).waitForFirst()
                .$(NativeButtonElement.class).id("foo'*+bar'");
        Assertions.assertEquals("Button with special id", button.getText());
    }

    @BrowserTest
    void hasAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> slottedButtons = view.$(NativeButtonElement.class)
                .withAttribute("slot")
                .all();
        Assertions.assertEquals(1, slottedButtons.size());
    }

    @BrowserTest
    void withAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> specialSlottedButtons = view.$(NativeButtonElement.class)
                .withAttribute("slot", "special-slot")
                .all();
        Assertions.assertEquals(1, specialSlottedButtons.size());

        List<NativeButtonElement> noSlottedButtons = view.$(NativeButtonElement.class)
                .withAttribute("slot", "nonexistent")
                .all();
        Assertions.assertEquals(0, noSlottedButtons.size());
    }

    @BrowserTest
    void withAttributeOperator() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withAttribute("id", "shadow", BEGINS_WITH)
                .all();
        Assertions.assertEquals(2, nativeButtonElements.size());

        nativeButtonElements = view.$(NativeButtonElement.class)
                .withAttribute("class", "1", ENDS_WITH)
                .all();
        Assertions.assertEquals(2, nativeButtonElements.size());

        nativeButtonElements = view.$(NativeButtonElement.class)
                .withAttribute("id", "shadow-button", NOT_CONTAINS_PREFIX)
                .all();
        Assertions.assertEquals(8, nativeButtonElements.size());
    }

    @BrowserTest
    void withAttributeContaining() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .withAttributeContaining("id", "light-button-").all();
        Assertions.assertEquals(5, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withAttributeContaining("class", "button-shadow-").all();
        Assertions.assertEquals(3, allButtons.size());
    }

    @BrowserTest
    void withAttributeContainingWord() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .withAttributeContainingWord("class", "button-1").all();
        Assertions.assertEquals(1, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withAttributeContainingWord("class", "button").all();
        Assertions.assertEquals(10, allButtons.size());
    }

    @BrowserTest
    void withoutHasAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> nonSlottedButtons = view.$(NativeButtonElement.class)
                .withoutAttribute("slot")
                .all();
        Assertions.assertEquals(9, nonSlottedButtons.size());
    }

    @BrowserTest
    void withoutAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> nonSlottedButtons = view.$(NativeButtonElement.class)
                .withoutAttribute("slot", "special-slot")
                .all();
        Assertions.assertEquals(9, nonSlottedButtons.size());

        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withoutAttribute("class", "nonexistent").all();
        Assertions.assertEquals(10, allButtons.size());
    }

    @BrowserTest
    void withoutAttributeContaining() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withoutAttributeContaining("class", "button-").all();
        Assertions.assertEquals(1, allButtons.size());
    }

    @BrowserTest
    void withoutAttributeContainingWord() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withoutAttributeContainingWord("class", "button-special-slot").all();
        Assertions.assertEquals(9, allButtons.size());
    }

    @BrowserTest
    void singleWithId() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        TestBenchElement button = view
                .$(TestBenchElement.class)
                .withId("shadow-button-2")
                .single();
        assertNotNull(button);
    }

    @BrowserTest
    void allWithClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withClassName("button")
                .all();
        Assertions.assertEquals(10, allButtons.size());
    }

    @BrowserTest
    void firstWithClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button1 = view.$(NativeButtonElement.class)
                .withClassName("button-1")
                .first();
        assertNotNull(button1);
    }

    @BrowserTest
    void firstWithClassNames() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement buttonButton1 = view.$(NativeButtonElement.class)
                .withClassName("button")
                .withClassName("button-1")
                .first();
        assertNotNull(buttonButton1);
    }

    @BrowserTest
    void noneWithoutClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> noButtons = view.$(NativeButtonElement.class)
                .withoutClassName("button")
                .all();
        Assertions.assertEquals(0, noButtons.size());
    }

    @BrowserTest
    void allWithoutClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> notButton1s = view.$(NativeButtonElement.class)
                .withoutClassName("button-1")
                .all();
        Assertions.assertEquals(9, notButton1s.size());
    }

    @BrowserTest
    void allWithoutClassNames() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> notButton1or2s = view.$(NativeButtonElement.class)
                .withoutClassName("button-1")
                .withoutClassName("button-2")
                .all();
        Assertions.assertEquals(8, notButton1or2s.size());
    }

    @BrowserTest
    void getSetElementsProperty() {
        openTestURL();
        TemplateViewElement template = $(TemplateViewElement.class).first();

        Assertions.assertEquals(8, template.getPropertyElements("children").size());
    }

    @BrowserTest
    void allWithLightTheme() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withTheme("light-theme")
                .all();
        Assertions.assertEquals(6, allButtons.size());
    }

    @BrowserTest
    void allWithoutLightTheme() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withoutTheme("light-theme")
                .all();
        Assertions.assertEquals(4, allButtons.size());
    }

    @BrowserTest
    void labelMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("one")
                .all();
        Assertions.assertEquals(3, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("two")
                .all();
        Assertions.assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("Flow")
                .all();
        Assertions.assertEquals(1, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("")
                .all();
        Assertions.assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("nonexistent")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void labelContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("n")
                .all();
        Assertions.assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("o")
                .all();
        Assertions.assertEquals(6, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("nonexistent")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void placeholderMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("one")
                .all();
        Assertions.assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("two")
                .all();
        Assertions.assertEquals(3, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("flow component")
                .all();
        Assertions.assertEquals(1, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("")
                .all();
        Assertions.assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("nonexistent")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void placeholderContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholderContaining("w")
                .all();
        Assertions.assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholderContaining("o")
                .all();
        Assertions.assertEquals(6, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholderContaining("nonexistent")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void labelAndPlaceholderMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("one")
                .withPlaceholder("two")
                .all();
        Assertions.assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("two")
                .withPlaceholder("one")
                .all();
        Assertions.assertEquals(1, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("one")
                .withPlaceholder("one")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void labelAndPlaceholderContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("n")
                .withPlaceholderContaining("w")
                .all();
        Assertions.assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("o")
                .withPlaceholderContaining("o")
                .all();
        Assertions.assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("Flow")
                .withPlaceholderContaining("two")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void captionMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaption("one")
                .all();
        Assertions.assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaption("two")
                .all();
        Assertions.assertEquals(3, labelPlaceholderElements.size());

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withCaption("Button with special id")
                .all();
        Assertions.assertEquals(1, nativeButtonElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaption("")
                .all();
        Assertions.assertEquals(1, labelPlaceholderElements.size());

        nativeButtonElements = view.$(NativeButtonElement.class)
                .withCaption("")
                .all();
        Assertions.assertEquals(0, nativeButtonElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaption("nonexistent")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void captionContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaptionContaining("Special")
                .all();
        Assertions.assertEquals(2, labelPlaceholderElements.size());

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withCaptionContaining("Special")
                .all();
        Assertions.assertEquals(2, nativeButtonElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaptionContaining("nonexistent")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void textMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withText("")
                .all();
        Assertions.assertEquals(9, labelPlaceholderElements.size());

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withText("Button with special id")
                .all();
        Assertions.assertEquals(1, nativeButtonElements.size());

        nativeButtonElements = view.$(NativeButtonElement.class)
                .withText("")
                .all();
        Assertions.assertEquals(0, nativeButtonElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withText("nonexistent")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void textContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withTextContaining("Special")
                .all();
        Assertions.assertEquals(2, nativeButtonElements.size());

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withText("nonexistent")
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

    @BrowserTest
    void propertyValueMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPropertyValue(LabelPlaceholderElement::getLabel, "")
                .all();
        Assertions.assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPropertyValue(LabelPlaceholderElement::getLabel, "one")
                .all();
        Assertions.assertEquals(3, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPropertyValue(LabelPlaceholderElement::getLabel, "one")
                .withPropertyValue(LabelPlaceholderElement::getPlaceholder, "two")
                .all();
        Assertions.assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPropertyValue(labelPlaceholderElement ->
                        labelPlaceholderElement.getSize().getHeight(), 0)
                .all();
        Assertions.assertEquals(0, labelPlaceholderElements.size());
    }

}
