package junit.com.vaadin.vaadin.v08.tb.demo;

import org.junit.jupiter.api.Assertions;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;

/**
 *
 */

@VaadinTest
class BasicWebUnitTest {

  @VaadinTest
  void testTemplate(BasicTestPageObject pageObject) {

    Assertions.assertEquals("0" , pageObject.counterLabel().getText());
    pageObject.button().click();
    Assertions.assertEquals("1" , pageObject.counterLabel().getText());
    pageObject.screenshot();
    pageObject.screenshot();
  }
}
