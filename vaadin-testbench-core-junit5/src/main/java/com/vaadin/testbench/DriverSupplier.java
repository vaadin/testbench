/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
