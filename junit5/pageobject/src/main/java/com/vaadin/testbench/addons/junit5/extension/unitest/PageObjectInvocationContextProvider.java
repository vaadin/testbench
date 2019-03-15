/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.testbench.addons.junit5.extension.unitest;

import static com.vaadin.testbench.addons.webdriver.BrowserDriverFunctions.webDriverInstances;
import static com.vaadin.testbench.addons.webdriver.junit5.WebdriverExtensionFunctions.storeWebDriver;
import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.testbench.addons.webdriver.BrowserTypes;
import com.vaadin.testbench.addons.webdriver.SkipBrowsers;

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


    final List<BrowserTypes> typesList = context
        .getTestMethod()
        .filter(method -> method.getAnnotation(SkipBrowsers.class) != null)
        .map((method) -> {
          final SkipBrowsers annotation = method.getAnnotation(SkipBrowsers.class);
          final BrowserTypes[] browserTypes = annotation.value();
          return (browserTypes == null)
                 ? SkipBrowsers.ALL_BROWSERS
                 : browserTypes;
        })
        .map(Arrays::asList)
        .orElse(emptyList());

    return webDriverInstances(typesList)
        .map(e -> new WebDriverTemplateInvocationContextImpl(this , e))
        .peek(po -> {
          logger().info("peek - page object -> setting as webDriver into Store ");
          storeWebDriver().accept(context , po.webdriver());
        })
        .map(e -> e);
  }

}
