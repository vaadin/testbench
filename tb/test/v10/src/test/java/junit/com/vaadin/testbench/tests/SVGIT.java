package junit.com.vaadin.testbench.tests;

import org.junit.Assert;
import org.openqa.selenium.By;
import com.vaadin.vaadin.addons.testbench.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.testUI.SVGView;

public class SVGIT  {



    private void openTestURL(GenericTestPageObject po) {
        po.loadPage(SVGView.ROUTE);
    }

    @VaadinWebUnitTest
    //@DisabledOnOs()
    public void click(GenericTestPageObject po) {
//        if (BrowserUtil.isSafari(po.getDriver().getDesiredCapabilities())) {
//            return; // Skip for Safari 11.
//        }
        openTestURL(po);
        po.findElement(By.id("ball")).click();
        Assert.assertEquals("clicked",
                po.findElement(By.tagName("body")).getText());
    }
}
