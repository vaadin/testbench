package junit.com.vaadin.testbench.tests.elements;

import org.junit.Assert;
import org.junit.Test;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import junit.com.vaadin.testbench.tests.GenericTestPageObject;

public class ExecuteJavascriptIT {

  private void openTestURL(GenericTestPageObject po) {
    po.loadPage(ElementQueryView.ROUTE);
  }

  @VaadinWebUnitTest
  public void getProperty(GenericTestPageObject po) throws Exception {
    openTestURL(po);

    TestBenchElement button = po.$(NativeButtonElement.class).first();
    Long offsetTop = button.getPropertyDouble("offsetTop").longValue();
    Assert.assertEquals(Long.valueOf(0) , offsetTop);
  }
}
