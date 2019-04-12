package junit.com.vaadin.testbench.tests.uitest.elements;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.uitest.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static com.vaadin.testbench.tests.uitest.PolymerTemplateView.ROUTE;

@VaadinTest
class ElementQueryIT {

    @VaadinTest(navigateTo = ROUTE)
    void ensureElementListWrapped(GenericTestPageObject po) {
        List<PolymerTemplateViewElement> elements = po.$(PolymerTemplateViewElement.class).all();
        Assertions.assertNotNull(elements.get(0));
    }

    @VaadinTest(navigateTo = ROUTE)
    void ensureElementListFromOnPageWrapped(GenericTestPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .first();
        PolymerTemplateViewElement view2 = view
                .$(PolymerTemplateViewElement.class)
                .onPage()
                .first();
        Assertions.assertEquals(view, view2);
    }

    @VaadinTest(navigateTo = ROUTE)
    void findLightDomElementById(GenericTestPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).first();
        NativeButtonElement button = view
                .$(NativeButtonElement.class)
                .id("light-button-1");

        Assertions.assertEquals("Button 1", button.getText());
    }

    @VaadinTest(navigateTo = ROUTE)
    void findShadowDomElementById(GenericTestPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("shadow-button-1");
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @VaadinTest(navigateTo = ROUTE)
    void findAllShadowDomElements(GenericTestPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        Assertions.assertEquals(10, view.$(NativeButtonElement.class).all().size());
    }

    @VaadinTest(navigateTo = ROUTE)
    void searchShadowDomBeforeLight(GenericTestPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("special-button");
        Assertions.assertEquals("Special Button (in Shadow DOM)", button.getText());
    }

    @VaadinTest(navigateTo = ROUTE)
    void mergeLightAndShadowDomResults(GenericTestPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                .all();
        Assertions.assertEquals(10, buttons.size());
    }

    @VaadinTest(navigateTo = ROUTE)
    void findTestBenchElementUsingTag(GenericTestPageObject po) {
        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$("button").id("shadow-button-2");
        Assertions.assertEquals("Shadow Button 2", button.getText());

    }

    @VaadinTest(navigateTo = ROUTE)
    void findTestBenchElement(GenericTestPageObject po) {
        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(TestBenchElement.class).id("shadow-button-2");
        Assertions.assertNotNull(button);
    }

    @VaadinTest(navigateTo = ROUTE)
    void findTestBenchElementChild(GenericTestPageObject po) {
        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(TestBenchElement.class).first()
                .$(TestBenchElement.class).first();
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @VaadinTest(navigateTo = ROUTE)
    void specialCharactersInId(GenericTestPageObject po) {
        NativeButtonElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(NativeButtonElement.class).id("foo'*+bar'");
        Assertions.assertEquals("Button with special id", button.getText());
    }

    @VaadinTest(navigateTo = ROUTE)
    void attributeContains(GenericTestPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                .attributeContains("class", "button-1").all();
        Assertions.assertEquals(1, button1s.size());
        List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                .attributeContains("class", "button").all();
        Assertions.assertEquals(10, allButtons.size());
    }

    @VaadinTest(navigateTo = ROUTE)
    void getSetElementsProperty(GenericTestPageObject po) {
        PolymerTemplateViewElement template = po.$(
                PolymerTemplateViewElement.class).first();

        Assertions.assertEquals(6, template.getPropertyElements("children").size());
    }
}
