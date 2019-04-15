package junit.com.vaadin.testbench.tests.ui.element;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.ui.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;

import static com.vaadin.testbench.tests.ui.element.ElementQueryView.ROUTE;

@VaadinTest
@Disabled("Throws an exception related to file I/O. Should be fixed.")
class ElementScreenCompareIT {

    private static final int SCREENSHOT_HEIGHT = 850;
    private static final int SCREENSHOT_WIDTH = 1500;

    @BeforeEach
    void setUp(GenericTestPageObject po) {
        po.getCommandExecutor().resizeViewPortTo(SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
    }

    @VaadinTest(navigateTo = ROUTE)
    void elementCompareScreen(GenericTestPageObject po) throws Exception {
        TestBenchElement button4 = po.$(NativeButtonElement.class).get(4);

        button4.getScreenshotAs(OutputType.BYTES);

        Assertions.assertTrue(button4.compareScreen("button4"));

        TestBenchElement layout = button4.findElement(By.xpath("../.."));

        Assertions.assertTrue(layout.compareScreen("layout"));
    }
}
