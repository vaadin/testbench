package junit.com.vaadin.testbench.tests.testUI;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinWebUnitTest;

@VaadinWebUnitTest
public class SVGTest {



    private void openTestURL(GenericTestPageObject po) {
        po.loadPage(com.vaadin.testbench.tests.testUI.SVGView.ROUTE);
    }

    @VaadinWebUnitTest
    //@DisabledOnOs()
    public void click(GenericTestPageObject po) {
//        if (BrowserUtil.isSafari(po.getDriver().getDesiredCapabilities())) {
//            return; // Skip for Safari 11.
//        }
        openTestURL(po);
        po.findElement(By.id("ball")).click();
        Assertions.assertEquals("clicked",
                                po.findElement(By.tagName("body")).getText());
    }
}
