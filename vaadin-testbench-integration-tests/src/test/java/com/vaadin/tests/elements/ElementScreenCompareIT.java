package com.vaadin.tests.elements;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.AbstractTB6Test;

public class ElementScreenCompareIT extends AbstractTB6Test {

    @Override
    protected Class<ElementQueryView> getTestView() {
        return ElementQueryView.class;
    }

    @Override
    public List<DesiredCapabilities> getBrowserConfiguration() {
        List<DesiredCapabilities> browsers = new ArrayList<>(
                super.getBrowserConfiguration());
        // Resize viewport does not work in Safari
        browsers.remove(BrowserUtil.safari());
        return browsers;
    }

    @Override
    public void setup() throws Exception {
        super.setup();
        testBench().resizeViewPortTo(SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
    }

    @Test
    public void elementCompareScreen() throws Exception {
        openTestURL();
        TestBenchElement button4 = $(NativeButtonElement.class).get(4);

        Assert.assertTrue(button4.compareScreen("button4"));
        TestBenchElement layout = button4.findElement(By.xpath("../.."));
        Assert.assertTrue(layout.compareScreen("layout"));
    }

}
