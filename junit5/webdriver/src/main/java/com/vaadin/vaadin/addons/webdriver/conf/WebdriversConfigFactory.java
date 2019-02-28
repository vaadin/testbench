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
package com.vaadin.vaadin.addons.webdriver.conf;

import static com.github.webdriverextensions.WebDriverProperties.CHROME_DRIVER_PROPERTY_NAME;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toSet;
//import static WebdriversConfig.UNITTESTING_HOST;
//import static WebdriversConfig.UNITTESTING_PORT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.vaadin.addons.webdriver.BrowserDriverFunctions;
import com.vaadin.vaadin.addons.webdriver.conf.GridConfig.Type;

public class WebdriversConfigFactory implements HasLogger {

  public static final String DEFAULT_UNITTESTING_BROWSER = "chrome";

  public WebdriversConfig createFromProperies(Properties configProperties) {

    final String chromeBinaryPath =
        configProperties.getProperty(WebdriversConfig.CHROME_BINARY_PATH , null);
    if(chromeBinaryPath != null) {
      System.setProperty(CHROME_DRIVER_PROPERTY_NAME, chromeBinaryPath);

    }

    //TODO check if compat test should run on local Browser
    final List<GridConfig> gridConfigs = unmodifiableList(createGridConfigs(configProperties));

//    logger().info("Browser for unittests is: " + unittestingBrowser.getBrowserName() + " on "
//                  + unittestingTarget);

    logger().info("Loaded " + gridConfigs.size() + " grid configuration(s)");
//    return new WebdriversConfig(unittestingTarget , gridConfigs);
    return new WebdriversConfig( gridConfigs);
  }

//  private String getUnitTestingTarget(Properties configProperties) {
//    final String host =
//        configProperties.getProperty(UNITTESTING_HOST , SELENIUM_GRID_PROPERTIES_LOCALE_BROWSER);
//    final String port =
//        configProperties.getProperty(UNITTESTING_PORT , "4444");
//    if (SELENIUM_GRID_PROPERTIES_LOCALE_BROWSER.equals(host)) {
//      return host;
//    } else {
//      return "http://" + host + ":" + port + "/wd/hub";
//    }
//  }

//  private DesiredCapabilities addCapabilities(DesiredCapabilities capabilities , Map<String, ?> capabilitiesToAdd) {
//    if (capabilities == null && capabilitiesToAdd == null) {
//      return new DesiredCapabilities();
//    }
//
//    if (capabilities == null) {
//      return new DesiredCapabilities(capabilitiesToAdd);
//    }
//
//    if (capabilitiesToAdd == null) {
//      return capabilities;
//    }
//
//    return new DesiredCapabilities(capabilities , new DesiredCapabilities(capabilitiesToAdd));
//  }

  private List<GridConfig> createGridConfigs(Properties configProperties) {
    List<GridConfig> grids = new ArrayList<>();
    Set<String> gridNames = configProperties.stringPropertyNames()
                                            .stream()
                                            .filter(key -> key.startsWith(WebdriversConfig.COMPATTESTING_GRID))
                                            .map(key -> key.substring(WebdriversConfig.COMPATTESTING_GRID.length() + 1))
                                            .map(key -> key.substring(0 , key.indexOf('.'))).collect(toSet());
    if (gridNames.isEmpty()) {
      grids.add(createDefaultGrid());
    }
    for (String gridName : gridNames) {
      if (isActive(configProperties , gridName)) {
        Type type = getGridType(configProperties , gridName);
        String target;

        switch (type) {
          case BROWSERSTACK:
            target = getGridTargetBrowserStack(configProperties , gridName);
            break;
          case SAUCELABS:
            target = getGridTargetSauceLabs(configProperties , gridName);
            break;
          default:
            target = getGridTarget(configProperties , gridName);
        }

        grids.add(new GridConfig(type , gridName , target ,
                                 getDesiredCapabilities(configProperties , gridName , type)
        ));
      }
    }
    return grids;
  }

  private GridConfig createDefaultGrid() {
    DesiredCapabilities desiredCapability = new DesiredCapabilities();
    desiredCapability.setPlatform(Platform.ANY);
    desiredCapability.setBrowserName(DEFAULT_UNITTESTING_BROWSER);
    return new GridConfig(Type.GENERIC , "autoConfGrid" ,
                          "http://localhost:4444/wd/hub" , singletonList(desiredCapability));
  }

  private boolean isActive(Properties configProperties , String gridName) {
    return Boolean.valueOf(getProperty(configProperties , gridName , "active" , "true"));
  }

  private Type getGridType(Properties configProperties , String gridName) {
    String stringType = configProperties.getProperty(getGridNameKey(gridName) + ".type" , "generic");
    return Type.valueOf(stringType.toUpperCase());
  }

  private List<DesiredCapabilities> getDesiredCapabilities(Properties configProperties ,
                                                           String gridName , Type type) {
    Set<String> oses = getOses(configProperties , gridName);

    Set<String> browsers = getBrowsers(configProperties , gridName);
    List<DesiredCapabilities> desiredCapabilites = new ArrayList<>();
//TODO should work without os as well
    for (String os : oses) {
      for (String browser : browsers) {
        for (String version : getVersions(configProperties , gridName , browser)) {
          DesiredCapabilities desiredCapability = new DesiredCapabilities();
          desiredCapability.setPlatform(Platform.fromString(os));
          desiredCapability.setBrowserName(browser);
          if (! "ANY".equals(version)) {
            desiredCapability.setVersion(version);
          }
          if (type == Type.SELENOID) {
            desiredCapability.setCapability(BrowserDriverFunctions.ENABLE_VIDEO ,
                                            getBoolean(configProperties , gridName , BrowserDriverFunctions.ENABLE_VIDEO)
            );
            desiredCapability.setCapability(BrowserDriverFunctions.ENABLE_VNC ,
                                            getBoolean(configProperties , gridName , BrowserDriverFunctions.ENABLE_VNC)
            );
          } else if (type == Type.BROWSERSTACK) {
            final String project = getProperty(configProperties , gridName , "project");
            if (StringUtils.isNotBlank(project)) {
              desiredCapability.setCapability(BrowserDriverFunctions.PROJECT , project);
            }
          } else if (type == Type.SAUCELABS) {
            final String project = getProperty(configProperties , gridName , "project");
            if (StringUtils.isNotBlank(project)) {
              desiredCapability.setCapability(BrowserDriverFunctions.TAGS , project);
            }
            final String build = System.getenv("SAUCELABS_BUILD");
            if (StringUtils.isNotBlank(build)) {
              desiredCapability.setCapability("build" , build);
            }
          }
          desiredCapabilites.add(desiredCapability);
        }
      }
    }
    //TODO if nothing was added, add default chrome one
    if (desiredCapabilites.isEmpty()) {
      desiredCapabilites.add(DesiredCapabilities.chrome());
    }
    return desiredCapabilites;
  }

  private boolean getBoolean(Properties configProperties , String gridName , String propertieName) {
    String stringValue =
        configProperties.getProperty(getGridNameKey(gridName) + "." + propertieName).trim();
    return StringUtils.isNotBlank(stringValue) ? Boolean.valueOf(stringValue) : false;
  }

  private Set<String> getBrowsers(Properties configProperties , String gridName) {
    return Arrays.stream(configProperties.getProperty(getGridNameKey(gridName) + ".browser").split(",")).map(String::trim).collect(toSet());
  }

  private Set<String> getOses(Properties configProperties , String gridName) {
    String property = configProperties.getProperty(getGridNameKey(gridName) + ".os");
    return (property == null)
           ? Arrays
               .stream(new String[]{"ANY"})
               .collect(toSet())
           : Arrays
               .stream(property.split(","))
               .map(String::trim)
               .collect(toSet());
  }

  private Set<String> getVersions(Properties configProperties , String gridName , String browser) {
    String property = configProperties.getProperty(getGridNameKey(gridName) + ".browser." + browser + ".version");
    return (property == null)
           ? Arrays
               .stream(new String[]{"ANY"})
               .collect(toSet())
           : Arrays
               .stream(property.split(","))
               .map(String::trim)
               .collect(toSet());
  }

  private String getGridTarget(Properties configProperties , String gridName) {
    final String host = getProperty(configProperties , gridName , "target");
    Validate.notBlank(host , "The target for the grid {} may not be blank" , gridName);
    if (host.equals("locale")) {
      return host;
    } else {
      final String proto = getProperty(configProperties , gridName , "proto" , "http");
      final String port = getProperty(configProperties , gridName , "port" , "4444");
      final String path = getProperty(configProperties , gridName , "path" , "wd/hub");

      return proto + "://" + host + ":" + port + "/" + path;
    }

  }

  private String getProperty(Properties configProperties , String gridName , String property) {
    return configProperties.getProperty(getGridNameKey(gridName) + "." + property);
  }

  private String getProperty(Properties configProperties , String gridName , String property , String defaultVaule) {
    return configProperties.getProperty(getGridNameKey(gridName) + "." + property , defaultVaule);
  }

  private String getGridTargetBrowserStack(Properties configProperties , String gridName) {
    final String userName =
        Validate.notBlank(configProperties.getProperty(getGridNameKey(gridName) + ".username"));
    final String key =
        Validate.notBlank(configProperties.getProperty(getGridNameKey(gridName) + ".key"));
    return "https://" + userName + ":" + key + "@hub-cloud.browserstack.com/wd/hub";
  }

  private String getGridTargetSauceLabs(Properties configProperties , String gridName) {
    final String userName =
        Validate.notBlank(configProperties.getProperty(getGridNameKey(gridName) + ".username"));
    final String key =
        Validate.notBlank(configProperties.getProperty(getGridNameKey(gridName) + ".key"));
    return "https://" + userName + ":" + key + "@ondemand.saucelabs.com:443/wd/hub";
  }

  private String getGridNameKey(String gridName) {
    return WebdriversConfig.COMPATTESTING_GRID + "." + gridName;
  }

}
