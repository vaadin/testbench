/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests.elements;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.ClickCountView;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractBrowserTB9Test;

public class ClickCountIT extends AbstractBrowserTB9Test {

    @Override
    protected Class<? extends Component> getTestView() {
        return ClickCountView.class;
    }

    @BeforeEach
    public void open() {
        openTestURL();
    }

    @BrowserTest
    public void click_hasClickCountOne() {
        TestBenchElement button = $(NativeButtonElement.class)
                .id("click-target");
        button.click();
        Assertions.assertEquals("1", $("div").id("click-count").getText());
    }

    @BrowserTest
    public void clickWithCoordinates_hasClickCountOne() {
        TestBenchElement button = $(NativeButtonElement.class)
                .id("click-target");
        button.click(0, 0);
        Assertions.assertEquals("1", $("div").id("click-count").getText());
    }

    @BrowserTest
    public void doubleClick_hasClickCountTwo() {
        TestBenchElement button = $(NativeButtonElement.class)
                .id("click-target");
        button.doubleClick();
        Assertions.assertEquals("2", $("div").id("click-count").getText());
    }
}
