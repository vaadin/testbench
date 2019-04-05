package com.vaadin.testbench.addons.webdriver;

import org.openqa.selenium.WebDriver;

public interface HasDriver {

    WebDriver getDriver();

    void setDriver(WebDriver driver);
}
