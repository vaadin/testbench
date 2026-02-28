/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ScrollIntoViewView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractTB6Test;

public class ScrollIntoViewIT extends AbstractTB6Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return ScrollIntoViewView.class;
    }

    @Before
    public void openView() {
        openTestURL();
    }

    @Test
    public void doubleClickOnElementOutsideScrollableViewport() {
        TestBenchElement target = $(TestBenchElement.class)
                .id("target-element");
        target.doubleClick();
        TestBenchElement result = $(TestBenchElement.class)
                .id("dblclick-result");
        Assert.assertEquals("Double-click received", result.getText());
    }

    @Test
    public void clickOnElementOutsideScrollableViewport() {
        TestBenchElement target = $(TestBenchElement.class)
                .id("target-element");
        target.click();
        TestBenchElement result = $(TestBenchElement.class).id("click-result");
        Assert.assertEquals("Click received", result.getText());
    }

    @Test
    public void clickWithCoordinatesOnElementOutsideScrollableViewport() {
        TestBenchElement target = $(TestBenchElement.class)
                .id("target-element");
        target.click(0, 0);
        TestBenchElement result = waitUntil(
                d -> $(TestBenchElement.class).id("click-result"));
        Assert.assertEquals("Click received", result.getText());
    }
}
