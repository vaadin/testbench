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

import java.util.function.Supplier;

import org.openqa.selenium.WebDriver;

public interface DriverSupplier {

    default Supplier<WebDriver> driverSupplier() {
        return () -> null;
    }

}
