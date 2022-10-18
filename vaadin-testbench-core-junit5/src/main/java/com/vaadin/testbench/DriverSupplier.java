package com.vaadin.testbench;

import java.util.function.Supplier;

import org.openqa.selenium.WebDriver;

public interface DriverSupplier {

    default Supplier<WebDriver> driverSupplier() {
        return () -> null;
    }

}
