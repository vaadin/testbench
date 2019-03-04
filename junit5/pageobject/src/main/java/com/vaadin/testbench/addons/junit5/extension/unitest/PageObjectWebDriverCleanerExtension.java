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
package com.vaadin.testbench.addons.junit5.extension.unitest;

import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.removeWebDriver;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.webdriver;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import com.vaadin.dependencies.core.logger.HasLogger;

/**
 *
 */
public class PageObjectWebDriverCleanerExtension implements AfterEachCallback, HasLogger {

//  public static final String SESSION_ID = "SESSION_ID";

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    logger().info("PageObjectWebDriverCleanerExtension -> remove Webdriver");
    final WebDriver webDriver = webdriver().apply(context);
    logger().info("close webdriver of type " + webdriverName().apply(webDriver));
//    match(
//        matchCase(() -> failure("could not map driver to impl class " + TestbenchFunctions.webdrivername().apply(webDriver))),
//        matchCase(() -> webDriver instanceof RemoteWebDriver, () -> success(((RemoteWebDriver) webDriver).getSessionId().toString())),
//        matchCase(() -> webDriver instanceof TestBenchDriverProxy,
//                  () -> success(
//                      ((RemoteWebDriver)
//                          ((TestBenchDriverProxy) webDriver).getActualDriver())
//                          .getSessionId().toString())
//        )
//    ).ifPresentOrElse(
//        success -> storeMethodPlain().apply(context).put(SESSION_ID, success),
//        message -> {
//          logger().warning(message);
//          storeMethodPlain().apply(context).remove(SESSION_ID);
//        }
//    );
    webDriver.quit();
    removeWebDriver().accept(context);
  }
}
