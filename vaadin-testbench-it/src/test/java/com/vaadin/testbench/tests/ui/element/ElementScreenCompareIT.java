package com.vaadin.testbench.tests.ui.element;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;

import static com.vaadin.testbench.tests.ui.element.ElementQueryView.ROUTE;

@VaadinTest(navigateTo = ROUTE)
class ElementScreenCompareIT {

    private static final int SCREENSHOT_HEIGHT = 600;
    private static final int SCREENSHOT_WIDTH = 600;

    @BeforeEach
    void setUp(VaadinPageObject po) {
        po.getCommandExecutor().resizeViewPortTo(SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
    }

    @VaadinTest
    void elementCompareScreen(VaadinPageObject po) throws Exception {
        TestBenchElement button4 = po.$(NativeButtonElement.class).get(4);
        button4.getScreenshotAs(OutputType.BYTES);

        Assertions.assertTrue(button4.compareScreen("button4"));

        TestBenchElement layout = button4.findElement(By.xpath("../.."));
        Assertions.assertTrue(layout.compareScreen("layout"));
    }
}
