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
package org.rapidpm.vaadin.addons.webdriver.junit5;

import static org.rapidpm.vaadin.addons.junit5.extensions.ExtensionFunctions.store;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;


/**
 *
 */
public interface WebdriverExtensionFunctions {

  String WEBDRIVER = "webdriver";

  static Function<ExtensionContext, WebDriver> webdriver() {
    return (context) -> store().apply(context).get(WEBDRIVER , WebDriver.class);
  }

  static BiConsumer<ExtensionContext, WebDriver> storeWebDriver() {
    return (context , webDriver) -> store().apply(context).put(WEBDRIVER , webDriver);
  }

  static Consumer<ExtensionContext> removeWebDriver() {
    return (context) -> store().apply(context).remove(WEBDRIVER);
  }

}
