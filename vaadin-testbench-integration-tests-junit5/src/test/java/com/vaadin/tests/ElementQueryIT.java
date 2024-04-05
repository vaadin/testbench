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
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.elements.CaptionElement;
import com.vaadin.tests.elements.NativeButtonElement;
import com.vaadin.tests.elements.TemplateViewElement;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class ElementQueryIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return TemplateView.class;
    }

    @BrowserTest
    public void ensureElementListWrapped() {
        openTestURL();
        List<TemplateViewElement> elements = $(TemplateViewElement.class).all();
        Assertions.assertTrue(elements.get(0) instanceof TemplateViewElement);
    }

    @BrowserTest
    public void ensureElementListFromOnPageWrapped() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).first();
        TemplateViewElement view2 = view.$(TemplateViewElement.class).onPage()
                .first();
        Assertions.assertEquals(view, view2);
    }

    @BrowserTest
    public void findLightDomElementById() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).first();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("light-button-1");
        Assertions.assertEquals("Button 1", button.getText());
    }

    @BrowserTest
    public void findShadowDomElementById() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("shadow-button-1");
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @BrowserTest
    public void findAllShadowDomElements() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        Assertions.assertEquals(10,
                view.$(NativeButtonElement.class).all().size());
    }

    @BrowserTest
    public void searchShadowDomBeforeLight() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("special-button");
        Assertions.assertEquals("Special Button (in Shadow DOM)",
                button.getText());
    }

    @BrowserTest
    public void mergeLightAndShadowDomResults() {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                .all();
        Assertions.assertEquals(10, buttons.size());
    }

    @BrowserTest
    public void findTestBenchElementUsingTag() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$("button").id("shadow-button-2");
        Assertions.assertEquals("Shadow Button 2", button.getText());

    }

    @BrowserTest
    public void findTestBenchElement() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$(TestBenchElement.class).id("shadow-button-2");
        Assertions.assertNotNull(button);
    }

    @BrowserTest
    public void findTestBenchElementChild() {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$(TestBenchElement.class).first().$(TestBenchElement.class)
                .first();
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @BrowserTest
    public void specialCharactersInId() {
        openTestURL();
        NativeButtonElement button = $(TemplateViewElement.class).waitForFirst()
                .$(NativeButtonElement.class).id("foo'*+bar'");
        Assertions.assertEquals("Button with special id", button.getText());
    }

    @BrowserTest
    public void attributeContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .attributeContains("class", "button-1").all();
        Assertions.assertEquals(1, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .attributeContains("class", "button").all();
        Assertions.assertEquals(10, allButtons.size());
    }

    @BrowserTest
    public void getSetElementsProperty() {
        openTestURL();
        TemplateViewElement template = $(TemplateViewElement.class).first();

        Assertions.assertEquals(6,
                template.getPropertyElements("children").size());
    }

    @BrowserTest
    public void labelMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withLabel("one")
                .all();
        Assertions.assertEquals(2, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withLabel("two")
                .all();
        Assertions.assertEquals(2, captionElements.size());
    }

    @BrowserTest
    public void labelContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withLabelContaining("n")
                .all();
        Assertions.assertEquals(2, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withLabelContaining("o")
                .all();
        Assertions.assertEquals(4, captionElements.size());
    }

    @BrowserTest
    public void placeholderMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withPlaceholder("one")
                .all();
        Assertions.assertEquals(2, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withPlaceholder("two")
                .all();
        Assertions.assertEquals(2, captionElements.size());
    }

    @BrowserTest
    public void placeholderContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withPlaceholderContaining("w")
                .all();
        Assertions.assertEquals(2, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withPlaceholderContaining("o")
                .all();
        Assertions.assertEquals(4, captionElements.size());
    }

    @BrowserTest
    public void labelAndPlaceholderMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withLabel("one")
                .withPlaceholder("two")
                .all();
        Assertions.assertEquals(1, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withLabel("two")
                .withPlaceholder("one")
                .all();
        Assertions.assertEquals(1, captionElements.size());
    }

    @BrowserTest
    public void labelAndPlaceholderContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withLabelContaining("n")
                .withPlaceholderContaining("w")
                .all();
        Assertions.assertEquals(1, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withLabelContaining("o")
                .withPlaceholderContaining("o")
                .all();
        Assertions.assertEquals(2, captionElements.size());
    }

    @BrowserTest
    public void captionElementsExist() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .all();
        Assertions.assertEquals(7, captionElements.size());
    }

    @BrowserTest
    public void captionMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withCaption("one")
                .all();
        Assertions.assertEquals(4, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withCaption("two")
                .all();
        Assertions.assertEquals(4, captionElements.size());
    }

    @BrowserTest
    public void captionContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withCaptionContaining("n")
                .all();
        Assertions.assertEquals(4, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withCaptionContaining("o")
                .all();
        Assertions.assertEquals(6, captionElements.size());
    }

}
