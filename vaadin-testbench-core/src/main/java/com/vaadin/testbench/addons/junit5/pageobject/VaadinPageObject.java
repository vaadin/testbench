package com.vaadin.testbench.addons.junit5.pageobject;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.vaadin.testbench.proxy.TestBenchDriverProxy;
import org.openqa.selenium.WebDriver;

import static com.vaadin.testbench.addons.testbench.TestbenchFunctions.unproxy;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;

public interface VaadinPageObject extends GenericVaadinAppSpecific {

    String NO_DRIVER = "NoDriver";

    default String driverName() {
        final WebDriver driver = getDriver();
        return driver == null ? NO_DRIVER : webdriverName(
                driver instanceof TestBenchDriverProxy ? unproxy(driver) : driver);
    }
}
