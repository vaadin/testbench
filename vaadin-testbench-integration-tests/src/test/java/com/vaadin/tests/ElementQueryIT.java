package com.vaadin.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PolymerTemplateView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.elements.NativeButtonElement;
import com.vaadin.tests.elements.PolymerTemplateViewElement;

public class ElementQueryIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return PolymerTemplateView.class;
    }

    @Test
    public void ensureElementListWrapped() {
        openTestURL();
        List<PolymerTemplateViewElement> elements = $(
                PolymerTemplateViewElement.class).all();
        Assert.assertTrue(
                elements.get(0) instanceof PolymerTemplateViewElement);
    }

    @Test
    public void ensureElementListFromOnPageWrapped() {
        openTestURL();
        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class)
                .first();
        PolymerTemplateViewElement view2 = view
                .$(PolymerTemplateViewElement.class).onPage().first();
        Assert.assertEquals(view, view2);
    }

    @Test
    public void findLightDomElementById() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class)
                .first();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("light-button-1");
        Assert.assertEquals("Button 1", button.getText());
    }

    @Test
    public void findShadowDomElementById() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class)
                .waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("shadow-button-1");
        Assert.assertEquals("Shadow Button 1", button.getText());
    }

    @Test
    public void findAllShadowDomElements() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class)
                .waitForFirst();
        Assert.assertEquals(10, view.$(NativeButtonElement.class).all().size());
    }

    @Test
    public void searchShadowDomBeforeLight() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class)
                .waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("special-button");
        Assert.assertEquals("Special Button (in Shadow DOM)", button.getText());
    }

    @Test
    public void mergeLightAndShadowDomResults() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class)
                .waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                .all();
        Assert.assertEquals(10, buttons.size());
    }

    @Test
    public void findTestBenchElementUsingTag() throws Exception {
        openTestURL();

        TestBenchElement button = $(PolymerTemplateViewElement.class)
                .waitForFirst().$("button").id("shadow-button-2");
        Assert.assertEquals("Shadow Button 2", button.getText());

    }

    @Test
    public void findTestBenchElement() throws Exception {
        openTestURL();

        TestBenchElement button = $(PolymerTemplateViewElement.class)
                .waitForFirst().$(TestBenchElement.class).id("shadow-button-2");
        Assert.assertNotNull(button);
    }

    @Test
    public void findTestBenchElementChild() throws Exception {
        openTestURL();

        TestBenchElement button = $(PolymerTemplateViewElement.class)
                .waitForFirst().$(TestBenchElement.class).first()
                .$(TestBenchElement.class).first();
        Assert.assertEquals("Shadow Button 1", button.getText());
    }

    @Test
    public void specialCharactersInId() {
        openTestURL();
        NativeButtonElement button = $(PolymerTemplateViewElement.class)
                .waitForFirst().$(NativeButtonElement.class).id("foo'*+bar'");
        Assert.assertEquals("Button with special id", button.getText());
    }

    @Test
    public void attributeContains() {
        openTestURL();
        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class)
                .waitForFirst();
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
        PolymerTemplateViewElement template = $(
                PolymerTemplateViewElement.class).first();

        Assert.assertEquals(6, template.getPropertyElements("children").size());
    }

}
