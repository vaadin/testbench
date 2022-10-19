/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parameters;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

/**
 * {@code TestBenchTestInfo} is used to inject information about the current
 * {@code Capabilities} and {@code WebDriver} into to test method.
 */
public interface TestBenchTestInfo {

    /**
     * @return {@link Capabilities} used in currently running test instance
     */
    Capabilities getCapabilities();

    /**
     * @return {@link WebDriver} used in currently running test instance
     */
    WebDriver getDriver();

}
