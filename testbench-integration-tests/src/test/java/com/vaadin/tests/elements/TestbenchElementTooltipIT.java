package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testUI.TestbenchElementTooltipUI;
import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;

public class TestbenchElementTooltipIT extends MultiBrowserTest {

    @Override
    protected String getDeploymentPath() {
        return "/" + TestbenchElementTooltipUI.class.getSimpleName();
    }

    @Test
    public void showTooltip_waitUntilTooltipIsShown() throws Exception {
        openTestURL();
        TestBenchElement elem = (TestBenchElement)findElements(
            By.className("v-button")).get(0);

        elem.showTooltip();
        String tooltipText = driver.findElement(By.className("v-tooltip"))
            .getText();

        Assert.assertEquals("Tooltip",tooltipText);
    }

    @Test
    public void showTooltip_HoveOnDifferentComponents_showsCorrectTooltip() throws Exception {
        openTestURL();
        TestBenchElement elem1 = (TestBenchElement)findElements(
            By.className("v-button")).get(0);
        TestBenchElement elem2 = (TestBenchElement)findElements(
            By.className("v-button")).get(1);

        elem1.showTooltip();
        elem2.showTooltip();
        String tooltipText = driver.findElement(By.className("v-tooltip"))
            .getText();

        Assert.assertEquals("Tooltip2",tooltipText);
    }

}
