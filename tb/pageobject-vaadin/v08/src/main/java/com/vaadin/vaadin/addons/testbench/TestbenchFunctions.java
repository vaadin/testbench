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
package com.vaadin.vaadin.addons.testbench;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.openqa.selenium.WebDriver;
import com.vaadin.vaadin.addons.webdriver.WebDriverFunctions;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;

/**
 *
 */
public interface TestbenchFunctions {


  static Function<WebDriver, String> webdrivername() {
    return webdriver -> unproxy().andThen(WebDriverFunctions.webdriverName()).apply(webdriver);
  }

  static Function<WebDriver, WebDriver> unproxy() {
    return proxyedDriver -> (proxyedDriver instanceof TestBenchDriverProxy)
                            ? ((TestBenchDriverProxy) proxyedDriver).getActualDriver()
                            : proxyedDriver;
  }


  static Function<Class<? extends AbstractComponent>, Optional<Class<? extends AbstractElement>>> conv() {
    return (componentClass) -> {
      final Predicate<Class<? extends AbstractComponent>> is = componentClass::isAssignableFrom;

      if (is.test(Button.class)) return Optional.of(ButtonElement.class);
      if (is.test(TextField.class)) return Optional.of(TextFieldElement.class);

      return Optional.empty();
    };
  }

}
