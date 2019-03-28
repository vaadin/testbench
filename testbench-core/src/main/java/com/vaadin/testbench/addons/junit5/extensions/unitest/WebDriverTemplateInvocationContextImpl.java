package com.vaadin.testbench.addons.junit5.extensions.unitest;

import static com.vaadin.testbench.addons.junit5.extensions.unitest.PageObjectFunctions.PAGE_OBJECT_NAVIGATION_TARGET;
import static com.vaadin.testbench.addons.junit5.extensions.unitest.PageObjectFunctions.PAGE_OBJECT_PRELOAD;
import static com.vaadin.testbench.addons.junit5.extensions.unitest.PageObjectFunctions.storePageObject;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.ExtensionContextFunctions.containerInfo;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;
import static java.util.Collections.singletonList;
import static org.openqa.selenium.support.PageFactory.initElements;

import java.lang.reflect.Constructor;
import java.util.List;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebDriver;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.frp.functions.CheckedFunction;
import com.vaadin.frp.model.Result;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.junit5.pageobject.PageObject;
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
          //TODO check if needed
          initElements(new WebDriverExtensionFieldDecorator(webDriver) , page);

          //TODO work on PreLoad features

          final Boolean preLoad = storeMethodPlain().apply(extensionContext).get(PAGE_OBJECT_PRELOAD , Boolean.class);
          if (preLoad) {

            final String nav = storeMethodPlain().apply(extensionContext).get(PAGE_OBJECT_NAVIGATION_TARGET , String.class);
            if (nav != null) page.loadPage(nav);
            else page.loadPage();

          } else logger()
              .info("no preLoading activated for testClass/testMethod "
                    + extensionContext.getTestClass() + " / "
                    + extensionContext.getTestMethod());


          return page;
        })
            .apply(pageObjectClass);

        po.ifPresentOrElse(
            success -> {
              pageObjectInvocationContextProvider.logger().fine("pageobject of type " + pageObjectClass.getSimpleName()
                                                                + " was created with " + webdriverName().apply(webdriver()));
              storePageObject().accept(extensionContext , success);
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
