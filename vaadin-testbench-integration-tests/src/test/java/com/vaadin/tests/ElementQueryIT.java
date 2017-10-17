package com.vaadin.tests;

import com.vaadin.testUI.PolymerTemplateView;
import com.vaadin.tests.elements.NativeButtonElement;
import com.vaadin.tests.elements.PolymerTemplateViewElement;
import com.vaadin.ui.Component;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ElementQueryIT extends MultiBrowserTest {

    @Override
    protected Class<? extends Component> getTestView() {
        return PolymerTemplateView.class;
    }

    @Test
    public void findLightDomElementById() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class).first();
        NativeButtonElement button = view.$(NativeButtonElement.class).id("light-button-1");
        Assert.assertEquals("Button 1", button.getText());
    }

    @Test
    public void findShadowDomElementById() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class).id("shadow-button-1");
        Assert.assertEquals("Shadow Button 1", button.getText());
    }

    @Test
    public void searchShadowDomBeforeLight() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class).id("special-button");
        Assert.assertEquals("Special Button (in Shadow DOM)", button.getText());
    }

    @Test
    public void mergeLightAndShadowDomResults() throws Exception {
        openTestURL();

        PolymerTemplateViewElement view = $(PolymerTemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class).all();
        Assert.assertEquals(9, buttons.size());
    }
}
