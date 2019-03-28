package junit.com.vaadin.testbench.tests.testUI;

import static com.vaadin.testbench.addons.webdriver.BrowserTypes.SAFARI;
import static com.vaadin.testbench.tests.testUI.SVGView.ROUTE;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;

@VaadinTest
public class SVGTest {

    @VaadinTest(navigateAsString = ROUTE)
    @SkipBrowsers(SAFARI)
    public void click(GenericTestPageObject po) {
        po.findElement(By.id("ball")).click();
        Assertions.assertEquals("clicked",
                                po.findElement(By.tagName("body")).getText());
    }
}
