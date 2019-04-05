package com.vaadin.testbench;

import org.openqa.selenium.WebDriver;

/**
 * An interface for classes that can provide an instance of a {@link WebDriver}
 */
public interface HasDriver {

    /**
     * Return the {@link WebDriver} instance associated with the implementing
     * class instance. This can be simply the class itself re-cast as a
     * WebDriver, or a reference to some WebDriver obtainable by any other
     * means.
     *
     * @return the contained WebDriver instance
     */
    WebDriver getDriver();
}
