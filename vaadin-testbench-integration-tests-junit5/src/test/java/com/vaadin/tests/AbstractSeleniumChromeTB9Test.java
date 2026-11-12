/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import io.github.bonigarcia.seljup.Options;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.vaadin.testbench.Parameters;

/**
 * Example of how to use SeleniumJupiter together with TestBench 9+ features.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractSeleniumChromeTB9Test
        extends AbstractSeleniumTB9Test {

    @Options
    ChromeOptions options = createOptions();

    // Selenium-Jupiter builds the ChromeDriver itself, so TestBench's
    // headless parameter reaches it only through these options. A local
    // driver takes them from an @Options field; @DriverCapabilities is read
    // for a remote one only
    private static ChromeOptions createOptions() {
        ChromeOptions options = new ChromeOptions();
        if (Parameters.isHeadless()) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    @BeforeEach
    public void setDriver(ChromeDriver driver) {
        super.setDriver(driver);
    }

}
