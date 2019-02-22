package junit.com.vaadin.testbench.tests;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.openqa.selenium.By;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.unittest.VaadinWebUnitTest;
import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;
import com.vaadin.testUI.SVGView;
import com.vaadin.testbench.parallel.BrowserUtil;

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
