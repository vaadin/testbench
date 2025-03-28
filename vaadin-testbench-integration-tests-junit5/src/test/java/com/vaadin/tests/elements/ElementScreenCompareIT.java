/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.By;

import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractBrowserTB9Test;

@Disabled("Viewport resize does not work")
public class ElementScreenCompareIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<ElementQueryView> getTestView() {
        return ElementQueryView.class;
    }

    @BeforeEach
    public void setup() {
        testBench().resizeViewPortTo(SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
    }

    @BrowserTest
    public void elementCompareScreen() throws Exception {
        openTestURL();
        TestBenchElement button4 = $(NativeButtonElement.class).get(4);

        Assertions.assertTrue(button4.compareScreen("button4"));
        TestBenchElement layout = button4.findElement(By.xpath("../.."));
        Assertions.assertTrue(layout.compareScreen("layout"));
    }

}
