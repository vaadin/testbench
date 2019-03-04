package junit.com.vaadin.testbench.tests.testUI;

import org.junit.jupiter.api.Assertions;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testbench.tests.testUI.PerformanceView;
import junit.com.vaadin.testbench.tests.testUI.elements.NativeButtonElement;

@VaadinWebUnitTest
public class PerformanceTest {


  private void openTestURL(GenericTestPageObject po) {
    po.loadPage(PerformanceView.ROUTE);
  }

  @VaadinWebUnitTest
  public void serverTime(GenericTestPageObject po) {
    openTestURL(po);
    po.$(NativeButtonElement.class).first().click();

    Assertions.assertEquals(1250.0 ,
                            po.getCommandExecutor().timeSpentServicingLastRequest() ,
                            250.0);
    po.$(NativeButtonElement.class).first().click();
    Assertions.assertEquals(2500 ,
                        po.getCommandExecutor().totalTimeSpentServicingRequests() ,
                        500.0);
  }

  @VaadinWebUnitTest
  public void renderingTime(GenericTestPageObject po) {
    openTestURL(po);

    long initialRendering = po.getCommandExecutor().timeSpentRenderingLastRequest();
    // Assuming initial rendering is done in 5-295ms
    Assertions.assertEquals(150 , initialRendering , 145);
    Assertions.assertEquals(initialRendering ,
                        po.getCommandExecutor().totalTimeSpentRendering());
    po.$(NativeButtonElement.class).first().click();
    po.$(NativeButtonElement.class).first().click();
    po.$(NativeButtonElement.class).first().click();

    // Assuming rendering three poll responses is done in 50ms
    Assertions.assertTrue(
        po.getCommandExecutor().totalTimeSpentRendering() > initialRendering, "totalTimeSpentRendering() > initialRendering");
    Assertions.assertEquals(initialRendering ,
                        po.getCommandExecutor().totalTimeSpentRendering() , 50);
  }

}
