package junit.com.vaadin.testbench.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import junit.com.vaadin.testbench.tests.elements.NativeButtonElement;
import junit.com.vaadin.testbench.tests.elements.PolymerTemplateViewElement;

@Disabled
public class ElementQueryIT {

  private void openTestURL(GenericTestPageObject po) {
    po.loadPage(ElementQueryView.ROUTE);
  }


  @VaadinWebUnitTest
  public void ensureElementListWrapped(GenericTestPageObject po) {
    openTestURL(po);
    List<PolymerTemplateViewElement> elements = po.$(PolymerTemplateViewElement.class).all();
    Assertions.assertTrue(
        elements.get(0) instanceof PolymerTemplateViewElement);
  }


  @VaadinWebUnitTest
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

  @VaadinWebUnitTest
  public void findLightDomElementById(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).first();
    NativeButtonElement button = view
        .$(NativeButtonElement.class)
        .id("light-button-1");

    Assertions.assertEquals("Button 1" , button.getText());
  }

  @VaadinWebUnitTest
  public void findShadowDomElementById(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class).waitForFirst();
    NativeButtonElement button = view.$(NativeButtonElement.class)
                                     .id("shadow-button-1");
    Assertions.assertEquals("Shadow Button 1" , button.getText());
  }

  @VaadinWebUnitTest
  public void findAllShadowDomElements(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                                        .waitForFirst();
    Assertions.assertEquals(10 , view.$(NativeButtonElement.class).all().size());
  }

  @VaadinWebUnitTest
  public void searchShadowDomBeforeLight(GenericTestPageObject po) throws Exception {
    openTestURL(po);
    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                                        .waitForFirst();
    NativeButtonElement button = view.$(NativeButtonElement.class)
                                     .id("special-button");
    Assertions.assertEquals("Special Button (in Shadow DOM)" , button.getText());
  }

  @VaadinWebUnitTest
  public void mergeLightAndShadowDomResults(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    PolymerTemplateViewElement view = po.$(PolymerTemplateViewElement.class)
                                        .waitForFirst();
    List<NativeButtonElement> buttons = view.$(NativeButtonElement.class)
                                            .all();
    Assertions.assertEquals(10 , buttons.size());
  }

  @VaadinWebUnitTest
  public void findTestBenchElementUsingTag(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                                .waitForFirst().$("button").id("shadow-button-2");
    Assertions.assertEquals("Shadow Button 2" , button.getText());

  }

  @VaadinWebUnitTest
  public void findTestBenchElement(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                                .waitForFirst().$(TestBenchElement.class).id("shadow-button-2");
    Assertions.assertNotNull(button);
  }

  @VaadinWebUnitTest
  public void findTestBenchElementChild(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    TestBenchElement button = po.$(PolymerTemplateViewElement.class)
                                .waitForFirst().$(TestBenchElement.class).first()
                                .$(TestBenchElement.class).first();
    Assertions.assertEquals("Shadow Button 1" , button.getText());
  }

  @VaadinWebUnitTest
  public void specialCharactersInId(GenericTestPageObject po) {
    openTestURL(po);
    NativeButtonElement button = po.$(PolymerTemplateViewElement.class)
                                   .waitForFirst().$(NativeButtonElement.class).id("foo'*+bar'");
    Assertions.assertEquals("Button with special id" , button.getText());
  }

  @VaadinWebUnitTest
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

  @VaadinWebUnitTest
  public void getSetElementsProperty(GenericTestPageObject po) {
    openTestURL(po);
    PolymerTemplateViewElement template = po.$(
        PolymerTemplateViewElement.class).first();

    Assertions.assertEquals(6 , template.getPropertyElements("children").size());
  }

}
