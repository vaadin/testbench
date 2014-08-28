/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
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
