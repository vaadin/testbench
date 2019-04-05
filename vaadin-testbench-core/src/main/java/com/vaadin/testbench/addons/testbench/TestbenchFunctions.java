package com.vaadin.testbench.addons.testbench;

import com.vaadin.testbench.addons.webdriver.WebDriverFunctions;
import com.vaadin.testbench.proxy.TestBenchDriverProxy;
import org.openqa.selenium.WebDriver;

import java.util.function.Function;

public interface TestbenchFunctions {

    static Function<WebDriver, String> webdrivername() {
        return webdriver -> unproxy().andThen(WebDriverFunctions.webdriverName()).apply(webdriver);
    }

    static Function<WebDriver, WebDriver> unproxy() {
        return proxyedDriver -> (proxyedDriver instanceof TestBenchDriverProxy)
                ? ((TestBenchDriverProxy) proxyedDriver).getWrappedDriver()
                : proxyedDriver;
    }
}
