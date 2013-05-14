package com.vaadin.testbench;

import org.openqa.selenium.WebDriver;

/**
 * An interface that provides an instance of a WebDriver.
 */
public interface HasDriver {
    /**
     * @return the contained WebDriver instance
     */
    WebDriver getDriver();
}
