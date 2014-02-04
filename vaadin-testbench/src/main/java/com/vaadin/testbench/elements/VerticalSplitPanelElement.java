package com.vaadin.testbench.elements;

import org.openqa.selenium.By;

import com.vaadin.testbench.TestBenchElement;

@ServerClass("com.vaadin.ui.VerticalSplitPanel")
public class VerticalSplitPanelElement extends AbstractSplitPanelElement {
    
    private static By bySplit = By.className("v-splitpanel-vsplitter");

    public TestBenchElement getSplitter() {
        return wrapElement(findElement(bySplit), getCommandExecutor());
    }
}
