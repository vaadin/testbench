package com.vaadin.testbench.addons.webdriver.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

public interface WebdriverExtensionFunctions {

    String WEBDRIVER_STORAGE_KEY = "webdriver";

    static WebDriver webdriver(ExtensionContext context) {
        return storeMethodPlain(context).get(WEBDRIVER_STORAGE_KEY, WebDriver.class);
    }

    static void storeWebDriver(ExtensionContext context, WebDriver webDriver) {
        storeMethodPlain(context).put(WEBDRIVER_STORAGE_KEY, webDriver);
    }

    static void removeWebDriver(ExtensionContext context) {
        storeMethodPlain(context).remove(WEBDRIVER_STORAGE_KEY);
    }
}
