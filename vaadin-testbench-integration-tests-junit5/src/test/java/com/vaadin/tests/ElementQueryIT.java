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
        Assertions.assertNotNull(elements.get(0));
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
                .withId("special-button")
                .first();
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
    void withAttributeContaining() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .withAttributeContaining("class", "button-1").all();
        Assertions.assertEquals(1, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .withAttributeContaining("class", "button").all();
        Assertions.assertEquals(10, allButtons.size());
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
    void singleWithId() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        TestBenchElement button = view
                .$(TestBenchElement.class)
                .withId("shadow-button-2")
                .single();
        Assertions.assertNotNull(button);
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
        Assertions.assertNotNull(button1);
    }

    @BrowserTest
    void firstWithClassNames() {
        openTestURL();
        TemplateViewElement view = $(TemplateViewElement.class).waitForFirst();
        NativeButtonElement buttonButton1 = view.$(NativeButtonElement.class)
                .withClassName("button")
                .withClassName("button-1")
                .first();
        Assertions.assertNotNull(buttonButton1);
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

        Assertions.assertEquals(6,
                template.getPropertyElements("children").size());
    }

}
