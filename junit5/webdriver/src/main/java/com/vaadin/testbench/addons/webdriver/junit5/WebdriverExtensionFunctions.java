package com.vaadin.testbench.addons.webdriver.junit5;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;


/**
 *
 */
public interface WebdriverExtensionFunctions {

  String WEBDRIVER_STORAGE_KEY = "webdriver";

  static Function<ExtensionContext, WebDriver> webdriver() {
    return (context) -> storeMethodPlain().apply(context).get(WEBDRIVER_STORAGE_KEY , WebDriver.class);
  }

  static BiConsumer<ExtensionContext, WebDriver> storeWebDriver() {
    return (context , webDriver) -> storeMethodPlain().apply(context).put(WEBDRIVER_STORAGE_KEY , webDriver);
  }

  static Consumer<ExtensionContext> removeWebDriver() {
    return (context) -> storeMethodPlain().apply(context).remove(WEBDRIVER_STORAGE_KEY);
  }

}
