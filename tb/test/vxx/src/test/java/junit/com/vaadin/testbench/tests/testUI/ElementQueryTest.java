package junit.com.vaadin.testbench.tests.testUI;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.tests.testUI.ElementQueryView;
import junit.com.vaadin.testbench.tests.testUI.elements.NativeButtonElement;
import junit.com.vaadin.testbench.tests.testUI.elements.PolymerTemplateViewElement;

@Disabled
@VaadinTest
public class ElementQueryTest {

  private void openTestURL(GenericTestPageObject po) {
    po.loadPage(ElementQueryView.ROUTE);
  }


  @VaadinTest
  public void ensureElementListWrapped(GenericTestPageObject po) {
    openTestURL(po);
    List<PolymerTemplateViewElement> elements = po.$(PolymerTemplateViewElement.class).all();
    Assertions.assertTrue(
        elements.get(0) instanceof PolymerTemplateViewElement);
  }


  @VaadinTest
  public void ensureElementListFromOnPageWrapped(GenericTestPageObject po) {
    openTestURL(po);
    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                                        .first();
    PolymerTemplateViewElement view2 = view
        .$(PolymerTemplateViewElement.class)
        .onPage()
        .first();
    Assertions.assertEquals(view , view2);
  }

  @VaadinTest
  public void findLightDomElementById(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).first();
    NativeButtonElement button = view
        .$(NativeButtonElement.class)
        .id("light-button-1");

    Assertions.assertEquals("Button 1" , button.getText());
  }

  @VaadinTest
  public void findShadowDomElementById(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).waitForFirst();
    NativeButtonElement button = view.$(NativeButtonElement.class)
                                     .id("shadow-button-1");
    Assertions.assertEquals("Shadow Button 1" , button.getText());
  }

  @VaadinTest
  public void findAllShadowDomElements(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                                        .waitForFirst();
    Assertions.assertEquals(10 , view.$(NativeButtonElement.class).all().size());
  }

  @VaadinTest
  public void searchShadowDomBeforeLight(GenericTestPageObject po) throws Exception {
    openTestURL(po);
    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                                        .waitForFirst();
    NativeButtonElement button = view.$(NativeButtonElement.class)
                                     .id("special-button");
    Assertions.assertEquals("Special Button (in Shadow DOM)" , button.getText());
  }

  @VaadinTest
  public void mergeLightAndShadowDomResults(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                                        .waitForFirst();
    List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                                            .all();
    Assertions.assertEquals(10 , buttons.size());
  }

  @VaadinTest
  public void findTestBenchElementUsingTag(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                                .waitForFirst().$("button").id("shadow-button-2");
    Assertions.assertEquals("Shadow Button 2" , button.getText());

  }

  @VaadinTest
  public void findTestBenchElement(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                                .waitForFirst().$(TestBenchElement.class).id("shadow-button-2");
    Assertions.assertNotNull(button);
  }

  @VaadinTest
  public void findTestBenchElementChild(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                                .waitForFirst().$(TestBenchElement.class).first()
                                .$(TestBenchElement.class).first();
    Assertions.assertEquals("Shadow Button 1" , button.getText());
  }

  @VaadinTest
  public void specialCharactersInId(GenericTestPageObject po) {
    openTestURL(po);
    NativeButtonElement button = po.$(PolymerTemplateViewElement.class)
                                   .waitForFirst().$(NativeButtonElement.class).id("foo'*+bar'");
    Assertions.assertEquals("Button with special id" , button.getText());
  }

  @VaadinTest
  public void attributeContains(GenericTestPageObject po) {
    openTestURL(po);
    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                                        .waitForFirst();
    List<NativeButtonElement> button1s = view.$(NativeButtonElement.class)
                                             .attributeContains("class" , "button-1").all();
    Assertions.assertEquals(1 , button1s.size());
    List<NativeButtonElement> allButtons = view.$(NativeButtonElement.class)
                                               .attributeContains("class" , "button").all();
    Assertions.assertEquals(10 , allButtons.size());
  }

  @VaadinTest
  public void getSetElementsProperty(GenericTestPageObject po) {
    openTestURL(po);
    PolymerTemplateViewElement template = po.$(
        PolymerTemplateViewElement.class).first();

    Assertions.assertEquals(6 , template.getPropertyElements("children").size());
  }

}
