/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import org.openqa.selenium.WebDriver;

/**
 * Class implementing this interface can provide own {@link WebDriver} to be
 * used during test execution.
 */
public interface DriverSupplier {

    /**
     * @return {@link WebDriver} to be used during test execution.
     */
    WebDriver createDriver();

}
