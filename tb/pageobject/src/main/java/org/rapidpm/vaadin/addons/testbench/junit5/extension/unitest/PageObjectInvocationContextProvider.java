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
package org.rapidpm.vaadin.addons.testbench.junit5.extension.unitest;

import static org.rapidpm.vaadin.addons.webdriver.BrowserDriverFunctions.webDriverInstances;
import static org.rapidpm.vaadin.addons.webdriver.junit5.WebdriverExtensionFunctions.storeWebDriver;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.rapidpm.dependencies.core.logger.HasLogger;

/**
 *
 */
public class PageObjectInvocationContextProvider implements TestTemplateInvocationContextProvider, HasLogger {


  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {

    logger().info("provideTestTemplateInvocationContexts");

    return webDriverInstances()
//        .stream()
        .map(e -> new WebDriverTemplateInvocationContextImpl(this, e))
        .peek(po -> {
          logger().info("peek - page object -> setting as webDriver into Store ");
          storeWebDriver().accept(context , po.webdriver());
        })
        .map(e -> e);
  }

}
