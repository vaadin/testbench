package com.vaadin.testbench.addons.testbench;

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
