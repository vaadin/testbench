package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;

public class ElementScreenCompareIT extends MultiBrowserTest {

    @Override
    protected Class<ElementQueryView> getTestView() {
        return ElementQueryView.class;
    }

    @Test
    public void elementCompareScreen() throws Exception {
        openTestURL();
        TestBenchElement button4 = $(NativeButtonElement.class).get(4);

        Assert.assertTrue(button4.compareScreen("button4"));
        TestBenchElement layout = (TestBenchElement) button4
                .findElement(By.xpath("../.."));
        Assert.assertTrue(layout.compareScreen("layout"));
    }

}
