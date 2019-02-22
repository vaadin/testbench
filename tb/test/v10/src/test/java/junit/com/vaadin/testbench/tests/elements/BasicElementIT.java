package junit.com.vaadin.testbench.tests.elements;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.TimeoutException;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import junit.com.vaadin.testbench.tests.GenericTestPageObject;

public class BasicElementIT {


  private void openTestURL(GenericTestPageObject po) {
    po.loadPage(ElementQueryView.ROUTE);
  }

  private static Function<GenericTestPageObject, TestBenchElement> elem() {
    return (po) -> {
      po.loadPage(ElementQueryView.ROUTE);
      return po.$(NativeButtonElement.class).first();
    };
  }

  @VaadinWebUnitTest
  public void getSetStringProperty(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    Assert.assertNull(buttonElement.getPropertyString("foo"));
    buttonElement.setProperty("foo" , "12");
    Assert.assertEquals("12" , buttonElement.getPropertyString("foo"));
    Assert.assertEquals(12.0 , buttonElement.getPropertyDouble("foo") , 0);
    Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
  }

  @VaadinWebUnitTest
  public void getSetBooleanProperty(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    Assert.assertNull(buttonElement.getPropertyBoolean("foo"));
    buttonElement.setProperty("foo" , true);
    Assert.assertEquals("true" , buttonElement.getPropertyString("foo"));
    Assert.assertEquals(1.0 , buttonElement.getPropertyDouble("foo") , 0);
    Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
  }

  @VaadinWebUnitTest
  public void getSetDoubleProperty(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    Assert.assertNull(buttonElement.getPropertyDouble("foo"));
    buttonElement.setProperty("foo" , 12.5);
    Assert.assertEquals("12.5" , buttonElement.getPropertyString("foo"));
    Assert.assertEquals(12.5 , buttonElement.getPropertyDouble("foo") , 0);
    Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
  }

  @VaadinWebUnitTest
  public void getSetIntegerProperty(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    Assert.assertNull(buttonElement.getPropertyInteger("foo"));
    buttonElement.setProperty("foo" , 12);
    Assert.assertEquals("12" , buttonElement.getPropertyString("foo"));
    Assert.assertEquals(12 , buttonElement.getPropertyInteger("foo") , 0);
    Assert.assertTrue(buttonElement.getPropertyBoolean("foo"));
  }

  @VaadinWebUnitTest
  public void getSetPropertyChain(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    po.getCommandExecutor().executeScript("arguments[0].foo = {bar: {baz: 123}};" , buttonElement);

    Assert.assertEquals(123L , buttonElement
        .getPropertyDouble("foo" , "bar" , "baz").longValue());
  }

  @VaadinWebUnitTest
  public void getSetElementProperty(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    Assert.assertEquals(buttonElement , buttonElement
        .getPropertyElement("parentElement" , "firstElementChild"));
    Assert.assertNull(
        buttonElement.getPropertyElement("firstElementChild"));

  }

  @VaadinWebUnitTest
  public void getSetElementsProperty(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    Assert.assertEquals(0 ,
                        buttonElement.getPropertyElements("children").size());
    Assert.assertEquals(1 , buttonElement
        .getPropertyElements("parentElement" , "children").size());

  }

  @VaadinWebUnitTest
  public void getSetPropertyChainMissingValue(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    po.getCommandExecutor().executeScript("arguments[0].foo = {bar: {baz: 123}};" , buttonElement);
    Assert.assertNull(buttonElement.getPropertyDouble("foo" , "baz" , "baz"));
  }

  @VaadinWebUnitTest()
  public void waitForNonExistant(GenericTestPageObject po) {

    final TestBenchElement buttonElement = elem().apply(po);
    Assertions
        .assertThrows(TimeoutException.class , () -> {
          po.$(PolymerTemplateViewElement.class)
            .waitForFirst();
          Assert.fail("Should not have found an element which does not exist");
        });
  }

  @VaadinWebUnitTest
  public void hasAttribute(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    NativeButtonElement withAttributes = po.$(NativeButtonElement.class)
                                           .get(5);
    NativeButtonElement withoutAttributes = po.$(NativeButtonElement.class)
                                              .get(6);

    Assert.assertTrue(withAttributes.hasAttribute("string"));
    Assert.assertTrue(withAttributes.hasAttribute("boolean"));
    Assert.assertFalse(withAttributes.hasAttribute("nonexistant"));

    Assert.assertFalse(withoutAttributes.hasAttribute("string"));
    Assert.assertFalse(withoutAttributes.hasAttribute("boolean"));
    Assert.assertFalse(withoutAttributes.hasAttribute("nonexistant"));
  }

  @VaadinWebUnitTest
  public void dispatchEvent(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    NativeButtonElement withAttributes = po.$(NativeButtonElement.class)
                                           .get(5);
    withAttributes.dispatchEvent("custom123");
    Assert.assertEquals("Event on Button 5" , po.$("div").id("msg").getText());
  }

  @VaadinWebUnitTest
  public void nativeButtonDisabled(GenericTestPageObject po) {
    final TestBenchElement buttonElement = elem().apply(po);
    NativeButtonElement enabled = po.$(NativeButtonElement.class).get(0);
    NativeButtonElement disabled = po.$(NativeButtonElement.class).get(2);
    Assert.assertTrue(enabled.isEnabled());
    Assert.assertFalse(disabled.isEnabled());
  }
}
