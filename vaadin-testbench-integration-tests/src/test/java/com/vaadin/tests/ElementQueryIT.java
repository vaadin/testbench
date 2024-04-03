/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.TemplateView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.elements.CaptionElement;
import com.vaadin.tests.elements.NativeButtonElement;
import com.vaadin.tests.elements.TemplateViewElement;

import org.junit.Assert;
import org.junit.Test;

public class ElementQueryIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return TemplateView.class;
    }

    @Test
    public void ensureElementListWrapped() {
        openTestURL();
        List<TemplateViewElement> elements = $(TemplateViewElement.class).all();
        Assert.assertTrue(elements.get(0) instanceof TemplateViewElement);
    }

    @Test
    public void ensureElementListFromOnPageWrapped() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).first();
        TemplateViewElement view2 = view.$(TemplateViewElement.class).onPage()
                .first();
        Assert.assertEquals(view, view2);
    }

    @Test
    public void findLightDomElementById() throws Exception {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).first();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("light-button-1");
        Assert.assertEquals("Button 1", button.getText());
    }

    @Test
    public void findShadowDomElementById() throws Exception {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("shadow-button-1");
        Assert.assertEquals("Shadow Button 1", button.getText());
    }

    @Test
    public void findAllShadowDomElements() throws Exception {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        Assert.assertEquals(10, view.$(NativeButtonElement.class).all().size());
    }

    @Test
    public void searchShadowDomBeforeLight() throws Exception {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("special-button");
        Assert.assertEquals("Special Button (in Shadow DOM)", button.getText());
    }

    @Test
    public void mergeLightAndShadowDomResults() throws Exception {
        openTestURL();

        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                .all();
        Assert.assertEquals(10, buttons.size());
    }

    @Test
    public void findTestBenchElementUsingTag() throws Exception {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$("button").id("shadow-button-2");
        Assert.assertEquals("Shadow Button 2", button.getText());

    }

    @Test
    public void findTestBenchElement() throws Exception {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$(TestBenchElement.class).id("shadow-button-2");
        Assert.assertNotNull(button);
    }

    @Test
    public void findTestBenchElementChild() throws Exception {
        openTestURL();

        TestBenchElement button = $(TemplateViewElement.class).waitForFirst()
                .$(TestBenchElement.class).first().$(TestBenchElement.class)
                .first();
        Assert.assertEquals("Shadow Button 1", button.getText());
    }

    @Test
    public void specialCharactersInId() {
        openTestURL();
        NativeButtonElement button = $(TemplateViewElement.class).waitForFirst()
                .$(NativeButtonElement.class).id("foo'*+bar'");
        Assert.assertEquals("Button with special id", button.getText());
    }

    @Test
    public void attributeContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .attributeContains("class", "button-1").all();
        Assert.assertEquals(1, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .attributeContains("class", "button").all();
        Assert.assertEquals(10, allButtons.size());
    }

    @Test
    public void getSetElementsProperty() {
        openTestURL();
        TemplateViewElement template = $(TemplateViewElement.class).first();

        Assert.assertEquals(6, template.getPropertyElements("children").size());
    }

    @Test
    public void labelMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withLabel("one")
                .all();
        Assert.assertEquals(2, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withLabel("two")
                .all();
        Assert.assertEquals(2, captionElements.size());
    }

    @Test
    public void labelContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withLabelContaining("n")
                .all();
        Assert.assertEquals(2, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withLabelContaining("o")
                .all();
        Assert.assertEquals(6, captionElements.size());
    }

    @Test
    public void placeholderMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withPlaceholder("one")
                .all();
        Assert.assertEquals(2, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withPlaceholder("two")
                .all();
        Assert.assertEquals(2, captionElements.size());
    }

    @Test
    public void placeholderContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withPlaceholderContaining("w")
                .all();
        Assert.assertEquals(2, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withPlaceholderContaining("o")
                .all();
        Assert.assertEquals(6, captionElements.size());
    }

    @Test
    public void labelAndPlaceholderMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withLabel("one")
                .withPlaceholder("two")
                .all();
        Assert.assertEquals(1, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withLabel("two")
                .withPlaceholder("one")
                .all();
        Assert.assertEquals(1, captionElements.size());
    }

    @Test
    public void labelAndPlaceholderContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withLabelContaining("n")
                .withPlaceholderContaining("w")
                .all();
        Assert.assertEquals(1, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withLabelContaining("o")
                .withPlaceholderContaining("o")
                .all();
        Assert.assertEquals(2, captionElements.size());
    }

    @Test
    public void captionElementsExist() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .all();
        Assert.assertEquals(7, captionElements.size());
    }

    @Test
    public void captionMatches() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withCaption("one")
                .all();
        Assert.assertEquals(4, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withCaption("two")
                .all();
        Assert.assertEquals(4, captionElements.size());
    }

    @Test
    public void captionContains() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<CaptionElement> captionElements = view.$(CaptionElement.class)
                .withCaptionContaining("n")
                .all();
        Assert.assertEquals(4, captionElements.size());

        captionElements = view.$(CaptionElement.class)
                .withCaptionContaining("o")
                .all();
        Assert.assertEquals(2, captionElements.size());
    }

}
