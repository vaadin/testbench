package com.vaadin.testbench.elements;

import org.openqa.selenium.By;

import com.vaadin.testbench.TestBenchElement;

@ServerClass("com.vaadin.ui.HorizontalSplitPanel")
public class HorizontalSplitPanelElement extends AbstractSplitPanelElement {

    private static By bySplit = By.className("v-splitpanel-hsplitter");

    public TestBenchElement getSplitter() {
        return wrapElement(findElement(bySplit), getCommandExecutor());
    }
}
