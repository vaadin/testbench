package junit.com.vaadin.testbench.tests.uitest;

import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.uitest.elements.NativeButtonElement;
import org.junit.jupiter.api.Assertions;

import static com.vaadin.testbench.tests.testUI.PerformanceView.ROUTE;

@VaadinTest
class PerformanceTest {

    @VaadinTest(navigateAsString = ROUTE)
    public void serverTime(GenericTestPageObject po) {
        po.$(NativeButtonElement.class).first().click();

        Assertions.assertEquals(1250.0,
                po.getCommandExecutor().timeSpentServicingLastRequest(),
                250.0);
        po.$(NativeButtonElement.class).first().click();
        Assertions.assertEquals(2500,
                po.getCommandExecutor().totalTimeSpentServicingRequests(),
                500.0);
    }

    @VaadinTest(navigateAsString = ROUTE)
    public void renderingTime(GenericTestPageObject po) {
        long initialRendering = po.getCommandExecutor().timeSpentRenderingLastRequest();
        // Assuming initial rendering is done in 5-295ms
        Assertions.assertEquals(150, initialRendering, 145);
        Assertions.assertEquals(initialRendering,
                po.getCommandExecutor().totalTimeSpentRendering());
        po.$(NativeButtonElement.class).first().click();
        po.$(NativeButtonElement.class).first().click();
        po.$(NativeButtonElement.class).first().click();

        // Assuming rendering three poll responses is done in 50ms
        Assertions.assertTrue(
                po.getCommandExecutor().totalTimeSpentRendering() > initialRendering,
                "totalTimeSpentRendering() > initialRendering");
        Assertions.assertEquals(initialRendering,
                po.getCommandExecutor().totalTimeSpentRendering(), 50);
    }
}
