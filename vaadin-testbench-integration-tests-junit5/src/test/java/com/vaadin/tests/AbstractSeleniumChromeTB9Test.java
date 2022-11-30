/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import io.github.bonigarcia.seljup.DriverCapabilities;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Example of how to use SeleniumJupiter together with TestBench 9+ features.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractSeleniumChromeTB9Test
        extends AbstractSeleniumTB9Test {

    @DriverCapabilities
    ChromeOptions options = new ChromeOptions();

    @BeforeEach
    public void setDriver(ChromeDriver driver) {
        super.setDriver(driver);
    }

}
