package com.vaadin.testbench.addons.junit5.pageobject;

import com.vaadin.testbench.proxy.TestBenchDriverProxy;
import org.openqa.selenium.WebDriver;

import static com.vaadin.testbench.addons.testbench.TestbenchFunctions.unproxy;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;

public interface VaadinPageObject extends GenericVaadinAppSpecific {

    String NO_DRIVER = "NoDriver";

    default String driverName() {
        final WebDriver driver = getDriver();
        return driver == null ? NO_DRIVER : webdriverName(
                driver instanceof TestBenchDriverProxy ? unproxy(driver) : driver);
    }
}
