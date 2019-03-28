package com.vaadin.testbench.addons.testbench;

import java.util.function.Function;

import org.openqa.selenium.WebDriver;
import com.vaadin.testbench.addons.webdriver.WebDriverFunctions;
import com.vaadin.testbench.proxy.TestBenchDriverProxy;

/**
 *
 */
public interface TestbenchFunctions {


  static Function<WebDriver, String> webdrivername() {
    return webdriver -> unproxy().andThen(WebDriverFunctions.webdriverName()).apply(webdriver);
  }

  static Function<WebDriver, WebDriver> unproxy() {
    return proxyedDriver -> (proxyedDriver instanceof TestBenchDriverProxy)
                            ? ((TestBenchDriverProxy) proxyedDriver).getWrappedDriver()
                            : proxyedDriver;
  }

//TODO candidate to be removed
//  static Function<Class<? extends AbstractComponent>, Optional<Class<? extends AbstractElement>>> conv() {
//    return (componentClass) -> {
//      final Predicate<Class<? extends AbstractComponent>> is = componentClass::isAssignableFrom;
//
//      if (is.test(Button.class)) return Optional.of(ButtonElement.class);
//      if (is.test(TextField.class)) return Optional.of(TextFieldElement.class);
//
//      return Optional.empty();
//    };
//  }

}
