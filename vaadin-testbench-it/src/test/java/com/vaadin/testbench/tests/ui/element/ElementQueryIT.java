package com.vaadin.testbench.tests.ui.element;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static com.vaadin.testbench.tests.ui.element.PolymerTemplateView.ROUTE;

@VaadinTest(navigateTo = ROUTE)
class ElementQueryIT {

    @VaadinTest
    void ensureElementListWrapped(VaadinPageObject po) {
        List<PolymerTemplateViewElement> elements = po.$(PolymerTemplateViewElement.class).all();
        Assertions.assertNotNull(elements.get(0));
    }

    @VaadinTest
    void ensureElementListFromOnPageWrapped(VaadinPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .first();
        PolymerTemplateViewElement view2 = view
                .$(PolymerTemplateViewElement.class)
                .onPage()
                .first();
        Assertions.assertEquals(view, view2);
    }

    @VaadinTest
    void findLightDomElementById(VaadinPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).first();
        NativeButtonElement button = view
                .$(NativeButtonElement.class)
                .id("light-button-1");

        Assertions.assertEquals("Button 1", button.getText());
    }

    @VaadinTest
    void findShadowDomElementById(VaadinPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("shadow-button-1");
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @VaadinTest(navigateTo = ROUTE)
    void findAllShadowDomElements(VaadinPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        Assertions.assertEquals(10, view.$(NativeButtonElement.class).all().size());
    }

    @VaadinTest
    void searchShadowDomBeforeLight(VaadinPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("special-button");
        Assertions.assertEquals("Special Button (in Shadow DOM)", button.getText());
    }

    @VaadinTest
    void mergeLightAndShadowDomResults(VaadinPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                .all();
        Assertions.assertEquals(10, buttons.size());
    }

    @VaadinTest
    void findTestBenchElementUsingTag(VaadinPageObject po) {
        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$("button").id("shadow-button-2");
        Assertions.assertEquals("Shadow Button 2", button.getText());

    }

    @VaadinTest
    void findTestBenchElement(VaadinPageObject po) {
        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(TestBenchElement.class).id("shadow-button-2");
        Assertions.assertNotNull(button);
    }

    @VaadinTest
    void findTestBenchElementChild(VaadinPageObject po) {
        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(TestBenchElement.class).first()
                .$(TestBenchElement.class).first();
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @VaadinTest
    void specialCharactersInId(VaadinPageObject po) {
        NativeButtonElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(NativeButtonElement.class).id("foo'*+bar'");
        Assertions.assertEquals("Button with special id", button.getText());
    }

    @VaadinTest
    void attributeContains(VaadinPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .attributeContains("class", "button-1").all();
        Assertions.assertEquals(1, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .attributeContains("class", "button").all();
        Assertions.assertEquals(10, allButtons.size());
    }

    @VaadinTest
    void getSetElementsProperty(VaadinPageObject po) {
        PolymerTemplateViewElement template = po.$(
                PolymerTemplateViewElement.class).first();

        Assertions.assertEquals(6, template.getPropertyElements("children").size());
    }
}
