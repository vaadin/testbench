package com.vaadin.testbench.addons.junit5.extensions.unitest;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import static com.vaadin.testbench.addons.junit5.extensions.unitest.PageObjectFunctions.removePageObject;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.removeWebDriver;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.webdriver;

public class PageObjectWebDriverCleanerExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
//    logger().info("PageObjectWebDriverCleanerExtension -> remove PageObject");
        removePageObject().accept(context);

//    logger().info("PageObjectWebDriverCleanerExtension -> remove Webdriver");
        final WebDriver webDriver = webdriver().apply(context);
//    logger().info("close webdriver of type " + webdriverName().apply(webDriver));
        webDriver.quit();
        removeWebDriver().accept(context);
    }
}
