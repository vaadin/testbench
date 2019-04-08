package junit.com.vaadin.testbench.tests.testui;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testui.elements.NativeButtonElement;
import junit.com.vaadin.testbench.tests.testui.elements.PolymerTemplateViewElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;

import java.util.List;

import static com.vaadin.testbench.tests.testUI.ElementQueryView.ROUTE;

@Disabled
@VaadinTest
public class ElementQueryTest {

    @VaadinTest(navigateAsString = ROUTE)
    public void ensureElementListWrapped(GenericTestPageObject po) {
        List<PolymerTemplateViewElement> elements = po.$(PolymerTemplateViewElement.class).all();
        Assertions.assertTrue(
                elements.get(0) instanceof PolymerTemplateViewElement);
    }

    @VaadinTest
    public void ensureElementListFromOnPageWrapped(GenericTestPageObject po) {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .first();
        PolymerTemplateViewElement view2 = view
                .$(PolymerTemplateViewElement.class)
                .onPage()
                .first();
        Assertions.assertEquals(view, view2);
    }

    @VaadinTest
    public void findLightDomElementById(GenericTestPageObject po) throws Exception {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).first();
        NativeButtonElement button = view
                .$(NativeButtonElement.class)
                .id("light-button-1");

        Assertions.assertEquals("Button 1", button.getText());
    }

    @VaadinTest
    public void findShadowDomElementById(GenericTestPageObject po) throws Exception {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("shadow-button-1");
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @VaadinTest
    public void findAllShadowDomElements(GenericTestPageObject po) throws Exception {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        Assertions.assertEquals(10, view.$(NativeButtonElement.class).all().size());
    }

    @VaadinTest
    public void searchShadowDomBeforeLight(GenericTestPageObject po) throws Exception {
        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        NativeButtonElement button = view.$(NativeButtonElement.class)
                .id("special-button");
        Assertions.assertEquals("Special Button (in Shadow DOM)", button.getText());
    }

    @VaadinTest
    public void mergeLightAndShadowDomResults(GenericTestPageObject po) throws Exception {

        PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                .waitForFirst();
        List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                .all();
        Assertions.assertEquals(10, buttons.size());
    }

    @VaadinTest
    public void findTestBenchElementUsingTag(GenericTestPageObject po) throws Exception {

        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$("button").id("shadow-button-2");
        Assertions.assertEquals("Shadow Button 2", button.getText());

    }

    @VaadinTest
    public void findTestBenchElement(GenericTestPageObject po) throws Exception {

        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(TestBenchElement.class).id("shadow-button-2");
        Assertions.assertNotNull(button);
    }

    @VaadinTest
    public void findTestBenchElementChild(GenericTestPageObject po) throws Exception {

        TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(TestBenchElement.class).first()
                .$(TestBenchElement.class).first();
        Assertions.assertEquals("Shadow Button 1", button.getText());
    }

    @VaadinTest
    public void specialCharactersInId(GenericTestPageObject po) {
        NativeButtonElement button = po.$(PolymerTemplateViewElement.class)
                .waitForFirst().$(NativeButtonElement.class).id("foo'*+bar'");
        Assertions.assertEquals("Button with special id", button.getText());
    }

    @VaadinTest
    public void attributeContains(GenericTestPageObject po) {
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
    public void getSetElementsProperty(GenericTestPageObject po) {
        PolymerTemplateViewElement template = po.$(
                PolymerTemplateViewElement.class).first();

        Assertions.assertEquals(6, template.getPropertyElements("children").size());
    }
}
