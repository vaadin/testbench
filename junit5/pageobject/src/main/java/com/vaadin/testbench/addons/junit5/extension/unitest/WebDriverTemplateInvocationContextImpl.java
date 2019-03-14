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

import static com.vaadin.testbench.addons.junit5.extension.unitest.PageObjectFunctions.storePageObject;
import static java.util.Collections.singletonList;
import static com.vaadin.testbench.addons.junit5.extensions.container.ExtensionContextFunctions.containerInfo;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;

import java.lang.reflect.Constructor;
import java.util.List;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.frp.functions.CheckedFunction;
import com.vaadin.frp.model.Result;
import com.vaadin.testbench.addons.junit5.pageobject.PageObject;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import xxx.com.github.webdriverextensions.WebDriverExtensionFieldDecorator;

public final class WebDriverTemplateInvocationContextImpl implements WebDriverTemplateInvocationContext, HasLogger {

  private PageObjectInvocationContextProvider pageObjectInvocationContextProvider;
  private final WebDriver webDriver;

  protected WebDriverTemplateInvocationContextImpl(PageObjectInvocationContextProvider pageObjectInvocationContextProvider ,
                                                   WebDriver webDriver) {
    this.pageObjectInvocationContextProvider = pageObjectInvocationContextProvider;
    this.webDriver = webDriver;
  }

  @Override
  public WebDriver webdriver() {
    pageObjectInvocationContextProvider.logger().info("WebDriverTemplateInvocationContextImpl - webdriver() called");
    return webDriver;
  }

  @Override
  public String getDisplayName(int invocationIndex) {
    return webdriverName().apply(webdriver());
  }

  @Override
  public List<Extension> getAdditionalExtensions() {
    return singletonList(new ParameterResolver() {
      @Override
      public boolean supportsParameter(ParameterContext parameterContext ,
                                       ExtensionContext extensionContext) {
        final Class<?> type = parameterContext.getParameter().getType();
        return PageObject.class.isAssignableFrom(type);
      }

      @Override
      public PageObject resolveParameter(ParameterContext parameterContext ,
                                         ExtensionContext extensionContext) {

        logger().info("resolveParameter called..");
        Class<?> pageObjectClass = parameterContext
            .getParameter()
            .getType();

        final Result<PageObject> po = ((CheckedFunction<Class<?>, PageObject>) aClass -> {
          final Constructor<?> constructor = pageObjectClass.getConstructor(WebDriver.class , ContainerInfo.class);
          WebDriver webDriver = webdriver();
          PageObject page = (PageObject) constructor.newInstance(webDriver , containerInfo().apply(extensionContext));
          PageFactory.initElements(new WebDriverExtensionFieldDecorator(webDriver) , page);
          return page;
        })
            .apply(pageObjectClass);

        po.ifPresentOrElse(
            success -> {
              pageObjectInvocationContextProvider.logger().fine("pageobject of type " + pageObjectClass.getSimpleName() + " was created with " + webdriverName().apply(webdriver()));
              storePageObject().accept(extensionContext, success);
            } ,
            failed -> pageObjectInvocationContextProvider.logger().warning("was not able to create PageObjectInstance " + failed)
        );
        po.ifAbsent(() -> {
          throw new ParameterResolutionException("was not able to create PageObjectInstance of type " + pageObjectClass);
        });
        return po.get();
      }
    });
  }
}
