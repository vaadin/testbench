package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testUI.ElementQueryUI;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

public class ElementScreenCompareTest extends MultiBrowserTest {

    @Override
    protected String getDeploymentPath() {
        return "/" + ElementQueryUI.class.getSimpleName();
    }

    @Test
    public void elementCompareScreen() throws Exception {
        openTestURL();
        TestBenchElement button4 = (TestBenchElement) findElements(
                By.className("v-button")).get(4);

        Assert.assertTrue(button4.compareScreen("button4"));
        TestBenchElement layout = (TestBenchElement) button4
                .findElement(By.xpath("../.."));
        Assert.assertTrue(layout.compareScreen("layout"));
    }

}
