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
import com.vaadin.tests.elements.NativeButtonElement;
import com.vaadin.tests.elements.TemplateViewElement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ElementQueryIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return TemplateView.class;
    }

    @Test
    public void ensureElementListWrapped() {
        openTestURL();
        List<TemplateViewElement> elements = $(TemplateViewElement.class).all();
        Assert.assertNotNull(elements.get(0));
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
                .withId("special-button")
                .first();
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
    public void withAttributeContaining() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .withAttributeContaining("class", "button-1").all();
        Assert.assertEquals(1, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withAttributeContaining("class", "button").all();
        Assert.assertEquals(10, allButtons.size());
    }

    @Test
    public void withoutAttribute() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();

        List<NativeButtonElement> nonSlottedButtons = view.$(NativeButtonElement.class)
                .withoutAttribute("slot", "special-slot")
                .all();
        Assert.assertEquals(9, nonSlottedButtons.size());

        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withoutAttribute("class", "nonexistent").all();
        Assert.assertEquals(10, allButtons.size());
    }

    @Test
    public void singleWithId() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        TestBenchElement button = view
                .$(TestBenchElement.class)
                .withId("shadow-button-2")
                .single();
        Assert.assertNotNull(button);
    }

    @Test
    public void allWithClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withClassName("button")
                .all();
        Assert.assertEquals(10, allButtons.size());
    }

    @Test
    public void firstWithClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement button1 = view.$(NativeButtonElement.class)
                .withClassName("button-1")
                .first();
        Assert.assertNotNull(button1);
    }

    @Test
    public void firstWithClassNames() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement buttonButton1 = view.$(NativeButtonElement.class)
                .withClassName("button")
                .withClassName("button-1")
                .first();
        Assert.assertNotNull(buttonButton1);
    }

    @Test
    public void noneWithoutClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> noButtons = view.$(NativeButtonElement.class)
                .withoutClassName("button")
                .all();
        Assert.assertEquals(0, noButtons.size());
    }

    @Test
    public void allWithoutClassName() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> notButton1s = view.$(NativeButtonElement.class)
                .withoutClassName("button-1")
                .all();
        Assert.assertEquals(9, notButton1s.size());
    }

    @Test
    public void allWithoutClassNames() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> notButton1or2s = view.$(NativeButtonElement.class)
                .withoutClassName("button-1")
                .withoutClassName("button-2")
                .all();
        Assert.assertEquals(8, notButton1or2s.size());
    }

    @Test
    public void getSetElementsProperty() {
        openTestURL();
        TemplateViewElement template = $(TemplateViewElement.class).first();

        Assert.assertEquals(6, template.getPropertyElements("children").size());
    }

}
