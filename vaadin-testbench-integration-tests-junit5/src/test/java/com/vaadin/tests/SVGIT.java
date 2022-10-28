package com.vaadin.tests;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.SVGView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.parallel.BrowserUtil;

public class SVGIT extends AbstractTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return SVGView.class;
    }

    @BrowserTest
    public void click() {
        if (BrowserUtil.isSafari(getCapabilities())) {
            return; // Skip for Safari 11.
        }
        openTestURL();
        findElement(By.id("ball")).click();
        Assertions.assertEquals("clicked",
                findElement(By.tagName("body")).getText());
    }
}
