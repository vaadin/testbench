package com.vaadin.tests.elements;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.vaadin.testUI.ElementQueryView;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.AbstractTB6Test;

public class ElementScreenCompareIT extends AbstractTB6Test {

    @Override
    protected Class<ElementQueryView> getTestView() {
        return ElementQueryView.class;
    }

    @Override
    public void setup() throws Exception {
        super.setup();
        testBench().resizeViewPortTo(SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
    }

    @Test
    public void elementCompareScreen() throws Exception {
        openTestURL();
        TestBenchElement button4 = $(NativeButtonElement.class).get(4);

        Assertions.assertTrue(button4.compareScreen("button4"));
        TestBenchElement layout = button4.findElement(By.xpath("../.."));
        Assertions.assertTrue(layout.compareScreen("layout"));
    }

}
