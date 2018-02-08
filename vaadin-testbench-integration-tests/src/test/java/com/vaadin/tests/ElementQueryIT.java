package com.vaadin.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testUI.PolymerTemplateView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.elements.NativeButtonElement;
import com.vaadin.tests.elements.PolymerTemplateViewElement;
import com.vaadin.ui.Component;

public class ElementQueryIT extends MultiBrowserTest {

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
        Assert.assertEquals(9, buttons.size());
    }

    @Test
    public void findTestBenchElementUsingTag() throws Exception {
        openTestURL();

        TestBenchElement button = $(PolymerTemplateViewElement.class)
                .waitForFirst().$("button").id("shadow-button-2");
        Assert.assertEquals("Shadow Button 2", button.getText());
    }
}
