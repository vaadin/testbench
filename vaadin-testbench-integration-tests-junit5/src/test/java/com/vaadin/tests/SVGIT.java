package com.vaadin.tests;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.SVGView;
import com.vaadin.testbench.TestBenchTest;
import com.vaadin.testbench.parallel.BrowserUtil;

public class SVGIT extends AbstractJUnit5TB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return SVGView.class;
    }

    @TestBenchTest
    public void click() {
        if (BrowserUtil.isSafari(capabilities)) {
            return; // Skip for Safari 11.
        }
        openTestURL();
        testBenchUtil.findElement(By.id("ball")).click();
        Assertions.assertEquals("clicked",
                testBenchUtil.findElement(By.tagName("body")).getText());
    }
}
