package junit.com.vaadin.testbench.tests.testUI;

import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import static com.vaadin.testbench.addons.webdriver.BrowserTypes.SAFARI;
import static com.vaadin.testbench.tests.testUI.SVGView.ROUTE;

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
