package com.vaadin.testbench.addons.junit5.extensions;

import static com.vaadin.testbench.TestBench.createDriver;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.vaadin.frp.model.Result;
import com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions;
import com.vaadin.testbench.proxy.TestBenchDriverProxy;

public class ConvertWebdriverTestExtension implements BeforeEachCallback, AfterEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
//    logger().info("beforeEach  -> convert WebDriver to VaadinWebDriver");

    Result
        .ofNullable(WebdriverExtensionFunctions.webdriver().apply(context))
        .ifPresentOrElse(
            webDriver -> {
//              logger().info("webDriver will be converted now to TestBenchDriverProxy");
              WebdriverExtensionFunctions.removeWebDriver().accept(context);
              WebdriverExtensionFunctions.storeWebDriver().accept(context, createDriver(webDriver));
            },
            failed -> {}/*logger().warning(failed)*/
        );
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
//    logger().info("afterEach  -> convert VaadinWebDriver to WebDriver");

    Result
        .ofNullable(WebdriverExtensionFunctions.webdriver().apply(context))
        .ifPresentOrElse(
            webDriver -> {
              //TODO not a clean life cycle -> compat tests
              if (webDriver instanceof TestBenchDriverProxy) {
//                logger().info("webDriver is !! instanceof !! TestBenchDriverProxy");
                WebdriverExtensionFunctions.removeWebDriver().accept(context);
                WebdriverExtensionFunctions.storeWebDriver().accept(context, ((TestBenchDriverProxy) webDriver).getWrappedDriver());
              } else {
//                logger().info("webDriver is NOT instanceof TestBenchDriverProxy");
              }
            },
            failed -> {} /*logger().warning(failed)*/
        );
  }

}
