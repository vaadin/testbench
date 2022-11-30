/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import com.vaadin.testbench.parallel.BrowserUtil;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.SVGView;

public class SVGIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return SVGView.class;
    }

    @Test
    public void click() {
        if (BrowserUtil.isSafari(this.getDesiredCapabilities())) {
            return; // Skip for Safari 11.
        }
        openTestURL();
        findElement(By.id("ball")).click();
        Assert.assertEquals("clicked",
                findElement(By.tagName("body")).getText());
    }
}
