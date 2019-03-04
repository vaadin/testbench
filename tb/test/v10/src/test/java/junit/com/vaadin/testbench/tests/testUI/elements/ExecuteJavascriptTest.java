package junit.com.vaadin.testbench.tests.testUI.elements;

import org.junit.jupiter.api.Assertions;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testbench.tests.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;

@VaadinWebUnitTest
public class ExecuteJavascriptTest {

  private void openTestURL(GenericTestPageObject po) {
    po.loadPage(ElementQueryView.ROUTE);
  }

  @VaadinWebUnitTest
  public void getProperty(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    TestBenchElement button = po.$(NativeButtonElement.class).first();
    Long offsetTop = button.getPropertyDouble("offsetTop").longValue();
    Assertions.assertEquals(Long.valueOf(0) , offsetTop);
  }
}
