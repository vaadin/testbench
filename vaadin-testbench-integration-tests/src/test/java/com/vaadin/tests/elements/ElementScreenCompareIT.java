/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testUI.ElementQueryUI;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

public class ElementScreenCompareIT extends MultiBrowserTest {

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
