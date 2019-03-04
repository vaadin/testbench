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
package com.vaadin.testbench.addons.webdriver;

import static com.vaadin.dependencies.core.properties.PropertiesResolver.propertyReader;
import static com.vaadin.frp.matcher.Case.match;
import static com.vaadin.frp.matcher.Case.matchCase;
import static com.vaadin.frp.model.Result.failure;
import static com.vaadin.frp.model.Result.success;

import java.net.URL;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.dependencies.core.logger.Logger;
import com.vaadin.dependencies.core.properties.PropertiesResolver;
import com.vaadin.frp.functions.CheckedFunction;
import com.vaadin.frp.functions.CheckedSupplier;
import com.vaadin.frp.model.Result;
import com.vaadin.frp.model.Triple;
import com.vaadin.testbench.addons.webdriver.conf.WebdriversConfig;
import com.vaadin.testbench.addons.webdriver.conf.WebdriversConfigFactory;
import com.github.webdriverextensions.DriverPathLoader;

/**
 *
 */
public interface BrowserDriverFunctions extends HasLogger {

  String BROWSER_NAME = "browserName";
  String PLATFORM = "platform";
  String UNITTESTING = "unittesting";
  String ENABLE_VNC = "enableVNC";
  String VERSION = "version";
  String ENABLE_VIDEO = "enableVideo";
  String PROJECT = "project";
  String TAGS = "tags";

  String SELENIUM_GRID_PROPERTIES_LOCALE_IP = "locale-ip";
  String SELENIUM_GRID_PROPERTIES_LOCALE_BROWSER = "locale";
  String SELENIUM_GRID_PROPERTIES_NO_GRID = "nogrid";

  String CONFIG_FOLDER = ".testbenchextensions/";


  static Function<DesiredCapabilities, Result<WebDriver>> localWebDriverInstance() {
    return dc -> {
      final String browserType = dc.getBrowserName();
      DriverPathLoader.loadDriverPaths();
      return match(
          matchCase(() -> failure("please define a browser driver...")) ,
          matchCase(browserType::isEmpty , () -> failure("browserType should not be empty")) ,
          matchCase(() -> browserType.equals(BrowserType.FIREFOX) , () -> success(new FirefoxDriver())) ,
          matchCase(() -> browserType.equals(BrowserType.CHROME) , () -> success(new ChromeDriver(new ChromeOptions().merge(dc)))) ,
          matchCase(() -> browserType.equals(BrowserType.SAFARI) , () -> success(new SafariDriver())) ,
          matchCase(() -> browserType.equals(BrowserType.OPERA) , () -> success(new OperaDriver())) ,
          matchCase(() -> browserType.equals(BrowserType.OPERA_BLINK) , () -> success(new OperaDriver())) ,
          matchCase(() -> browserType.equals(BrowserType.IE) , () -> success(new InternetExplorerDriver()))
      );
    };
  }

  static Function<String, Result<DesiredCapabilities>> type2Capabilities() {
    return (browsertype) ->
        match(
            matchCase(() -> failure("browsertype unknown : " + browsertype)) ,
            matchCase(browsertype::isEmpty , () -> failure("browsertype should not be empty")) ,
            matchCase(() -> browsertype.equals(BrowserType.FIREFOX) , () -> success(DesiredCapabilities.firefox())) ,
            matchCase(() -> browsertype.equals(BrowserType.CHROME) , () -> success(DesiredCapabilities.chrome())) ,
            matchCase(() -> browsertype.equals(BrowserType.EDGE) , () -> success(DesiredCapabilities.edge())) ,
            matchCase(() -> browsertype.equals(BrowserType.SAFARI) , () -> success(DesiredCapabilities.safari())) ,
            matchCase(() -> browsertype.equals(BrowserType.OPERA_BLINK) , () -> success(DesiredCapabilities.operaBlink())) ,
            matchCase(() -> browsertype.equals(BrowserType.OPERA) , () -> success(DesiredCapabilities.opera())) ,
            matchCase(() -> browsertype.equals(BrowserType.IE) , () -> success(DesiredCapabilities.internetExplorer()))
        );
  }

  static CheckedSupplier<WebDriver> remoteWebDriverInstance(DesiredCapabilities desiredCapability ,
                                                            final String ip) {
    return () -> {
      Logger
          .getLogger(BrowserDriverFunctions.class)
          .info("Create RemoteWebdriver to " + ip + " for browser: " + desiredCapability);
      final URL url = new URL(ip);
      return new RemoteWebDriver(url , desiredCapability);
    };
  }

  static Stream<WebDriver> webDriverInstances() {
    return readConfig()
        .getGridConfigs()
        .stream()
        .flatMap(gridConfig -> gridConfig
            .getDesiredCapabilities()
            .stream()
            .map(dc -> new Triple<>(gridConfig.getTarget().equals(SELENIUM_GRID_PROPERTIES_LOCALE_BROWSER) ,
                                    dc ,
                                    gridConfig.getTarget()
            ))
        )
        .map(createWebDriverInstance());
  }

  static Function<? super Triple<Boolean, DesiredCapabilities, String>, ? extends WebDriver> createWebDriverInstance() {
    return t -> ((t.getT1())
                 ? localWebDriverInstance().apply(t.getT2())
                 : remoteWebDriverInstance(t.getT2() , t.getT3()).get())
        .ifAbsent(() -> {
          throw new RuntimeException("no WebDriver was created..");
        })
        .get();
  }

  static WebdriversConfig readConfig() {
    final Properties configProperties =
        propertyReader()
            .apply(CONFIG_FOLDER + "config")
            .getOrElse(Properties::new);
    return new WebdriversConfigFactory().createFromProperies(configProperties);
  }
}
