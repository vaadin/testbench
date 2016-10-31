package com.vaadin.tests.testbenchapi;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;

public class ElementScreenCompareIT extends MultiBrowserTest {

    @Override
    protected String getDeploymentPath() {
        return "/ElementQueryUI";
    }

    @Test
    public void elementCompareScreen() throws Exception {
        openTestURL();
        ButtonElement button4 = $(ButtonElement.class).all().get(4);

        Assert.assertTrue(button4.compareScreen("button4"));
        TestBenchElement layout = (TestBenchElement) button4
                .findElement(By.xpath("../.."));
        Assert.assertTrue(layout.compareScreen("layout"));
    }

}
