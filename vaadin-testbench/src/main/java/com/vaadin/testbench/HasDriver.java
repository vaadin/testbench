/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
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
