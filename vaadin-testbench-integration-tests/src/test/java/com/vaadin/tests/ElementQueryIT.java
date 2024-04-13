/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.TemplateView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.elements.LabelPlaceholderElement;
import com.vaadin.tests.elements.NativeButtonElement;
import com.vaadin.tests.elements.TemplateViewElement;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ElementQueryIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return TemplateView.class;
    }

    @Test
    public void ensureElementListWrapped() {
        openTestURL();
        List<TemplateViewElement> elements = $(TemplateViewElement.class).all();
        assertNotNull(elements.get(0));
    }

    @Test
    public void ensureElementListFromOnPageWrapped() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).first();
        TemplateViewElement view2 = view.$(TemplateViewElement.class).onPage()
                .first();
        assertEquals(view, view2);
    }

    @Test
    public void findLightDomElementById() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).first();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("light-button-1");
        assertEquals("Button 1", button.getText());
    }

    @Test
    public void findShadowDomElementById() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("shadow-button-1");
        assertEquals("Shadow Button 1", button.getText());
    }

    @Test
    public void findAllShadowDomElements() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        assertEquals(10, view.$(NativeButtonElement.class).all().size());
    }

    @Test
    public void searchShadowDomBeforeLight() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .withId("special-button")
                .first();
        assertEquals("Special Button (in Shadow DOM)", button.getText());
    }

    @Test
    public void mergeLightAndShadowDomResults() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                .all();
        assertEquals(10, buttons.size());
    }

    @Test
    public void findTestBenchElementUsingTag() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$("button").id("shadow-button-2");
        assertEquals("Shadow Button 2", button.getText());

    }

    @Test
    public void findTestBenchElement() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$(TestBenchElement.class).id("shadow-button-2");
        assertNotNull(button);
    }

    @Test
    public void findTestBenchElementChild() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$(TestBenchElement.class).first().$(TestBenchElement.class)
                .first();
        assertEquals("Shadow Button 1", button.getText());
    }

    @Test
    public void specialCharactersInId() {
        openTestURL();
        NativeButtonElement button = $(TemplateViewElement.class).waitForFirst()
                .$(NativeButtonElement.class).id("foo'*+bar'");
        assertEquals("Button with special id", button.getText());
    }

    @Test
    public void hasAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> slottedButtons = view.$(NativeButtonElement.class)
                .withAttribute("slot")
                .all();
        assertEquals(1, slottedButtons.size());
    }

    @Test
    public void withAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> specialSlottedButtons = view.$(NativeButtonElement.class)
                .withAttribute("slot", "special-slot")
                .all();
        assertEquals(1, specialSlottedButtons.size());

        List<NativeButtonElement> noSlottedButtons = view.$(NativeButtonElement.class)
                .withAttribute("slot", "nonexistent")
                .all();
        assertEquals(0, noSlottedButtons.size());
    }

    @Test
    public void withAttributeContaining() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .withAttributeContaining("class", "button-1").all();
        assertEquals(1, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withAttributeContaining("class", "button").all();
        assertEquals(10, allButtons.size());
    }

    @Test
    public void withoutHasAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> nonSlottedButtons = view.$(NativeButtonElement.class)
                .withoutAttribute("slot")
                .all();
        assertEquals(9, nonSlottedButtons.size());
    }

    @Test
    public void withoutAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> nonSlottedButtons = view.$(NativeButtonElement.class)
                .withoutAttribute("slot", "special-slot")
                .all();
        assertEquals(9, nonSlottedButtons.size());

        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withoutAttribute("class", "nonexistent").all();
        assertEquals(10, allButtons.size());
    }

    @Test
    public void withoutAttributeContaining() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withoutAttributeContaining("class", "button-special-slot").all();
        assertEquals(9, allButtons.size());
    }

    @Test
    public void singleWithId() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        TestBenchElement button = view
                .$(TestBenchElement.class)
                .withId("shadow-button-2")
                .single();
        assertNotNull(button);
    }

    @Test
    public void allWithClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withClassName("button")
                .all();
        assertEquals(10, allButtons.size());
    }

    @Test
    public void firstWithClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button1 = view.$(NativeButtonElement.class)
                .withClassName("button-1")
                .first();
        assertNotNull(button1);
    }

    @Test
    public void firstWithClassNames() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement buttonButton1 = view.$(NativeButtonElement.class)
                .withClassName("button")
                .withClassName("button-1")
                .first();
        assertNotNull(buttonButton1);
    }

    @Test
    public void noneWithoutClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> noButtons = view.$(NativeButtonElement.class)
                .withoutClassName("button")
                .all();
        assertEquals(0, noButtons.size());
    }

    @Test
    public void allWithoutClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> notButton1s = view.$(NativeButtonElement.class)
                .withoutClassName("button-1")
                .all();
        assertEquals(9, notButton1s.size());
    }

    @Test
    public void allWithoutClassNames() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> notButton1or2s = view.$(NativeButtonElement.class)
                .withoutClassName("button-1")
                .withoutClassName("button-2")
                .all();
        assertEquals(8, notButton1or2s.size());
    }

    @Test
    public void getSetElementsProperty() {
        openTestURL();
        TemplateViewElement template = $(TemplateViewElement.class).first();

        assertEquals(8, template.getPropertyElements("children").size());
    }

    @Test
    public void allWithLightTheme() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withTheme("light-theme")
                .all();
        assertEquals(6, allButtons.size());
    }

    @Test
    public void allWithoutLightTheme() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withoutTheme("light-theme")
                .all();
        assertEquals(4, allButtons.size());
    }

    @Test
    public void labelMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("one")
                .all();
        assertEquals(3, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("two")
                .all();
        assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("Flow")
                .all();
        assertEquals(1, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("nonexistent")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void labelContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("n")
                .all();
        assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("o")
                .all();
        assertEquals(6, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("nonexistent")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void placeholderMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("one")
                .all();
        assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("two")
                .all();
        assertEquals(3, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("flow component")
                .all();
        assertEquals(1, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholder("nonexistent")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void placeholderContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholderContaining("w")
                .all();
        assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholderContaining("o")
                .all();
        assertEquals(6, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withPlaceholderContaining("nonexistent")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void labelAndPlaceholderMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("one")
                .withPlaceholder("two")
                .all();
        assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("two")
                .withPlaceholder("one")
                .all();
        assertEquals(1, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabel("one")
                .withPlaceholder("one")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void labelAndPlaceholderContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("n")
                .withPlaceholderContaining("w")
                .all();
        assertEquals(2, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("o")
                .withPlaceholderContaining("o")
                .all();
        assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withLabelContaining("Flow")
                .withPlaceholderContaining("two")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void captionMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaption("one")
                .all();
        assertEquals(4, labelPlaceholderElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaption("two")
                .all();
        assertEquals(3, labelPlaceholderElements.size());

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withCaption("Button with special id")
                .all();
        assertEquals(1, nativeButtonElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaption("nonexistent")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void captionContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaptionContaining("Special")
                .all();
        assertEquals(2, labelPlaceholderElements.size());

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withCaptionContaining("Special")
                .all();
        assertEquals(2, nativeButtonElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withCaptionContaining("nonexistent")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void textMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withText("")
                .all();
        assertEquals(9, labelPlaceholderElements.size());

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withText("Button with special id")
                .all();
        assertEquals(1, nativeButtonElements.size());

        labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withText("nonexistent")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

    @Test
    public void textContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> nativeButtonElements = view.$(NativeButtonElement.class)
                .withTextContaining("Special")
                .all();
        assertEquals(2, nativeButtonElements.size());

        List<LabelPlaceholderElement> labelPlaceholderElements = view.$(LabelPlaceholderElement.class)
                .withText("nonexistent")
                .all();
        assertEquals(0, labelPlaceholderElements.size());
    }

}
