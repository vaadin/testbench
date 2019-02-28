package junit.com.vaadin.testbench.tests;

import org.junit.Assert;
import com.vaadin.vaadin.addons.testbench.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testUI.PerformanceView;
import junit.com.vaadin.testbench.tests.elements.NativeButtonElement;

public class PerformanceIT {


  private void openTestURL(GenericTestPageObject po) {
    po.loadPage(PerformanceView.ROUTE);
  }

  @VaadinWebUnitTest
  public void serverTime(GenericTestPageObject po) {
    openTestURL(po);
    po.$(NativeButtonElement.class).first().click();

    Assert.assertEquals(1250.0 ,
                        po.getCommandExecutor().timeSpentServicingLastRequest() ,
                        250.0);
    po.$(NativeButtonElement.class).first().click();
    Assert.assertEquals(2500 ,
                        po.getCommandExecutor().totalTimeSpentServicingRequests() ,
                        500.0);
  }

  @VaadinWebUnitTest
  public void renderingTime(GenericTestPageObject po) {
    openTestURL(po);

    long initialRendering = po.getCommandExecutor().timeSpentRenderingLastRequest();
    // Assuming initial rendering is done in 5-295ms
    Assert.assertEquals(150 , initialRendering , 145);
    Assert.assertEquals(initialRendering ,
                        po.getCommandExecutor().totalTimeSpentRendering());
    po.$(NativeButtonElement.class).first().click();
    po.$(NativeButtonElement.class).first().click();
    po.$(NativeButtonElement.class).first().click();

    // Assuming rendering three poll responses is done in 50ms
    Assert.assertTrue("totalTimeSpentRendering() > initialRendering" ,
                      po.getCommandExecutor().totalTimeSpentRendering() > initialRendering);
    Assert.assertEquals(initialRendering ,
                        po.getCommandExecutor().totalTimeSpentRendering() , 50);
  }

}
