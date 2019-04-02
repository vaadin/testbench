package junit.com.vaadin.testbench.tests.testUI.elements;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import junit.com.vaadin.testbench.tests.testUI.GenericTestPageObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;

import static com.vaadin.testbench.tests.testUI.ElementQueryView.ROUTE;

@VaadinTest
public class ElementScreenCompareTest {

    public static final int SCREENSHOT_HEIGHT = 850;
    public static final int SCREENSHOT_WIDTH = 1500;

    @BeforeEach
    void setUp(GenericTestPageObject po) {
        po.getCommandExecutor().resizeViewPortTo(SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
    }

    @VaadinTest(navigateAsString = ROUTE)
    public void elementCompareScreen(GenericTestPageObject po) throws Exception {
        TestBenchElement button4 = po.$(NativeButtonElement.class).get(4);

        final byte[] screenshot = button4.getScreenshotAs(OutputType.BYTES);

        Assertions.assertTrue(button4.compareScreen("button4"));

        TestBenchElement layout = button4.findElement(By.xpath("../.."));

        Assertions.assertTrue(layout.compareScreen("layout"));
    }
}
