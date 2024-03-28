/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
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

public class ExecuteJavascriptIT extends MultiBrowserTest {

    @Override
    protected String getDeploymentPath() {
        return "/" + ElementQueryUI.class.getSimpleName();
    }

    @Test
    public void elementCompareScreen() throws Exception {
        openTestURL();

        TestBenchElement button = (TestBenchElement) findElements(
                By.className("v-button")).get(0);

        Long offsetTop = (Long) executeScript("return arguments[0].offsetTop",
                button);
        Assert.assertEquals(Long.valueOf(0), offsetTop);
    }
}
