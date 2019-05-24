package com.vaadin.testbench.tests.ui;

import com.vaadin.testbench.addons.junit5.extensions.unittest.VaadinTest;
import com.vaadin.testbench.addons.junit5.pageobject.VaadinPageObject;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import static com.vaadin.testbench.addons.webdriver.BrowserTypes.SAFARI;
import static com.vaadin.testbench.tests.ui.SVGView.ROUTE;

@VaadinTest(navigateTo = ROUTE)
class SVGIT {

    @VaadinTest
    @SkipBrowsers(SAFARI)
    void click(VaadinPageObject po) {
        po.findElement(By.id("ball")).click();
        Assertions.assertEquals("clicked",
                po.findElement(By.tagName("body")).getText());
    }
}
