package com.vaadin.testbench.addons.junit5.pageobject;

import static com.vaadin.frp.matcher.Case.match;
import static com.vaadin.frp.matcher.Case.matchCase;
import static com.vaadin.frp.model.Result.success;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.takeScreenShot;
import static com.vaadin.testbench.addons.webdriver.WebDriverFunctions.webdriverName;
import static java.lang.System.getProperties;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.frp.functions.CheckedExecutor;
import com.vaadin.testbench.addons.junit5.extensions.container.HasContainerInfo;
import com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions;
import com.vaadin.testbench.addons.webdriver.HasDriver;

public interface PageObject extends HasContainerInfo, HasDriver, HasLogger {

  String BACK_SLASH = "/";

  default void loadPage() {
    final String url = url().get();
    logger().info("Navigate browser to " + url);
    getDriver().get(url);
  }

  default void loadPage(String route) {
    final String url = url().get();
    logger().info("Navigate browser to " + url + route);
    getDriver().get(url + route);
  }

  default String getTitle() {
    return getDriver().getTitle();
  }

  default BiFunction<String, String, String> property() {
    return (key , defaultValue) -> (String) getProperties().getOrDefault(key , defaultValue);
  }

  default Supplier<String> protocol() {
    return () -> property().apply(NetworkFunctions.SERVER_PROTOCOL , NetworkFunctions.DEFAULT_PROTOCOL);
  }

  default Supplier<String> ip() {
    return () -> getContainerInfo().host();
  }

  default Supplier<String> port() {
    return () -> String.valueOf(getContainerInfo().port());
  }

  //TODO per properties
  default Supplier<String> webapp() {
    return () -> property().apply(NetworkFunctions.SERVER_WEBAPP , NetworkFunctions.DEFAULT_SERVLET_WEBAPP);
  }

  default Supplier<String> baseURL() {
    return () -> protocol().get() + "://" + ip().get() + ":" + port().get();
  }

  default Supplier<String> url() {
    return () -> match(
        matchCase(() -> success(BACK_SLASH + webapp().get() + BACK_SLASH)) ,
        matchCase(() -> webapp().get().equals("") , () -> success(BACK_SLASH)) ,
        matchCase(() -> webapp().get().endsWith(BACK_SLASH) && webapp().get().startsWith(BACK_SLASH) , () -> success(webapp().get())) ,
        matchCase(() -> webapp().get().endsWith(BACK_SLASH) && ! webapp().get().startsWith(BACK_SLASH) , () -> success(BACK_SLASH + webapp().get())) ,
//        matchCase(() -> !webapp().get().endsWith("/") && webapp().get().startsWith("/"), () -> success(webapp().get() + "/")),
        matchCase(() -> webapp().get().equals(BACK_SLASH) , () -> success(BACK_SLASH))
    )
        .map(e -> baseURL().get() + e)
        .get();
  }

  default void destroy() {
    ((CheckedExecutor) getDriver()::quit)
        .apply(null)
        .ifPresentOrElse(
            ok -> logger().info("webdriver quit -> OK") ,
            failed -> logger().warning("webdriver quit failed -> " + failed)
        );

    ((CheckedExecutor) getDriver()::close)
        .apply(null)
        .ifPresentOrElse(
            ok -> logger().info("webdriver close -> OK") ,
            failed -> logger().warning("webdriver close failed -> " + failed)
        );
  }

  default void screenshot() {
    takeScreenShot().accept(getDriver());
  }

  default String drivername() {
    return webdriverName().apply(getDriver());
  }
}