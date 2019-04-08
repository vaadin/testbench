package com.vaadin.testbench.addons.testbench;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
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

import com.vaadin.testbench.addons.webdriver.WebDriverFunctions;
import com.vaadin.testbench.proxy.TestBenchDriverProxy;
import org.openqa.selenium.WebDriver;

public interface TestbenchFunctions {

    static String webdriverName(WebDriver webDriver) {
        return WebDriverFunctions.webdriverName(unproxy(webDriver));
    }

    static WebDriver unproxy(WebDriver proxyedDriver) {
        return (proxyedDriver instanceof TestBenchDriverProxy)
                ? ((TestBenchDriverProxy) proxyedDriver).getWrappedDriver()
                : proxyedDriver;
    }
}
