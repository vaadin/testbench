package com.github.webdriverextensions.internal;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.github.webdriverextensions.WebComponent;

public class DefaultWebComponentFactory implements WebComponentFactory {

  @Override
  public <T extends WebComponent> T create(Class<T> webComponentClass, WebElement webElement,
                                           WebDriver webDriver) {
    return createInstanceOf(webComponentClass, webElement, webDriver);
  }

  private <T extends WebComponent> T createInstanceOf(final Class<T> webComponentClass,
                                                      final WebElement webElement, WebDriver webDriver) {
    try {
      T webComponent = webComponentClass.newInstance();
      webComponent.init(webDriver, webElement);
      return webComponent;
    } catch (IllegalArgumentException | SecurityException | InstantiationException
        | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
