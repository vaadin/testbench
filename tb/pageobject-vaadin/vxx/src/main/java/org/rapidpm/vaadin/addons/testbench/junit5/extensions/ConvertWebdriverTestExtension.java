/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.vaadin.addons.testbench.junit5.extensions;

import static com.vaadin.testbench.TestBench.createDriver;
import static org.rapidpm.vaadin.addons.webdriver.junit5.WebdriverExtensionFunctions.*;
import static org.rapidpm.vaadin.addons.webdriver.junit5.WebdriverExtensionFunctions.removeWebDriver;
import static org.rapidpm.vaadin.addons.webdriver.junit5.WebdriverExtensionFunctions.storeWebDriver;
import static org.rapidpm.vaadin.addons.webdriver.junit5.WebdriverExtensionFunctions.webdriver;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.frp.model.Result;
import com.vaadin.testbench.TestBenchDriverProxy;

public class ConvertWebdriverTestExtension implements BeforeEachCallback, AfterEachCallback, HasLogger {

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    logger().info("beforeEach  -> convert WebDriver to VaadinWebDriver");

    Result
        .ofNullable(webdriver().apply(context))
        .ifPresentOrElse(
            webDriver -> {
              logger().info("webDriver will be converted now to TestBenchDriverProxy");
              removeWebDriver().accept(context);
              storeWebDriver().accept(context, createDriver(webDriver));
            },
            failed -> logger().warning(failed)
        );
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    logger().info("afterEach  -> convert VaadinWebDriver to WebDriver");

    Result
        .ofNullable(webdriver().apply(context))
        .ifPresentOrElse(
            webDriver -> {
              //TODO not a clean life cycle -> compat tests
              if (webDriver instanceof TestBenchDriverProxy) {
                logger().info("webDriver is !! instanceof !! TestBenchDriverProxy");
                removeWebDriver().accept(context);
                storeWebDriver().accept(context, ((TestBenchDriverProxy) webDriver).getWrappedDriver());
              } else {
                logger().info("webDriver is NOT instanceof TestBenchDriverProxy");
              }
            },
            failed -> logger().warning(failed)
        );
  }

}
