package com.vaadin.testbench.addons.junit5.extensions;

import com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions;
import com.vaadin.testbench.proxy.TestBenchDriverProxy;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import static com.vaadin.testbench.TestBench.createDriver;

public class ConvertWebdriverTestExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
//    logger().info("beforeEach  -> convert WebDriver to TestBenchDriverProxy");
        final WebDriver driver = WebdriverExtensionFunctions.webdriver(context);
        if (driver == null) {
//            logger().warning(failed)
            return;
        }

        WebdriverExtensionFunctions.removeWebDriver(context);
        WebdriverExtensionFunctions.storeWebDriver(context, createDriver(driver));
    }

    @Override
    public void afterEach(ExtensionContext context) {
//    logger().info("afterEach  -> convert VaadinWebDriver to WebDriver");
        // TODO(sven): Not a clean life cycle -> compat tests.

        final WebDriver driver = WebdriverExtensionFunctions.webdriver(context);
        if (!(driver instanceof TestBenchDriverProxy)) {
//                logger().info("webDriver is NOT instanceof TestBenchDriverProxy");
            return;
        }
//            logger().info("webDriver is !! instanceof !! TestBenchDriverProxy");

        WebdriverExtensionFunctions.removeWebDriver(context);
        WebdriverExtensionFunctions.storeWebDriver(context,
                ((TestBenchDriverProxy) driver).getWrappedDriver());
    }
}
