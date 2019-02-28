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
package junit.com.vaadin.vaadin.addons.testbench;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.vaadin.vaadin.addons.webdriver.BrowserDriverFunctions.CONFIG_FOLDER;
import static com.vaadin.vaadin.addons.webdriver.BrowserDriverFunctions.propertyReader;

import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.vaadin.frp.model.Result;
import com.vaadin.vaadin.addons.webdriver.conf.GridConfig;
import com.vaadin.vaadin.addons.webdriver.conf.GridConfig.Type;
import com.vaadin.vaadin.addons.webdriver.conf.WebdriversConfig;
import com.vaadin.vaadin.addons.webdriver.conf.WebdriversConfigFactory;

public class WebdriversConfigTest {
  private WebdriversConfigFactory factory = new WebdriversConfigFactory();

  @Test
  @DisplayName("build default config")
  void test001() {
    WebdriversConfig config = factory.createFromProperies(new Properties());

//    assertEquals(DesiredCapabilities.chrome(), config.getUnittestingBrowser());
//    assertEquals(SELENIUM_GRID_PROPERTIES_LOCALE_BROWSER, config.getUnittestingTarget());

    assertEquals(1, config.getGridConfigs().size());

    final GridConfig autoConfGridConfig = config.getGridConfigs().get(0);

    assertEquals("autoConfGrid", autoConfGridConfig.getName());
    assertEquals("http://localhost:4444/wd/hub", autoConfGridConfig.getTarget());
    assertEquals(Type.GENERIC, autoConfGridConfig.getType());
    assertEquals(1, autoConfGridConfig.getDesiredCapabilities().size());

    assertEquals(Platform.ANY, autoConfGridConfig.getDesiredCapabilities().get(0).getPlatform());
    assertEquals("", autoConfGridConfig.getDesiredCapabilities().get(0).getVersion());
    assertEquals("chrome", autoConfGridConfig.getDesiredCapabilities().get(0).getBrowserName());
  }

  @Test
  @DisplayName("build from properties")
  void test002() {
    Result<Properties> apply = propertyReader().apply(CONFIG_FOLDER + "config-002");
    Properties configProperties = apply.get();

    WebdriversConfig config = factory.createFromProperies(configProperties);

//    assertEquals(DesiredCapabilities.firefox(), config.getUnittestingBrowser());
//    assertEquals("http://localhost:4444/wd/hub", config.getUnittestingTarget());

    assertEquals(2, config.getGridConfigs().size());

    GridConfig genericGridConfig = config.getGridConfigs().stream()
                                         .filter(grid -> grid.getName().equals("generic")).findFirst().get();

    assertEquals("http://localhost:4444/wd/hub", genericGridConfig.getTarget());
    assertEquals(Type.GENERIC, genericGridConfig.getType());

    List<DesiredCapabilities> desiredCapabilities = genericGridConfig.getDesiredCapabilities();

    assertEquals(4, desiredCapabilities.size());

    GridConfig selenoidGridConfig = config.getGridConfigs().stream()
                                          .filter(grid -> grid.getName().equals("selenoid")).findFirst().get();

    List<DesiredCapabilities> slenoidDesiredCapabilities = selenoidGridConfig.getDesiredCapabilities();

    assertEquals(2, slenoidDesiredCapabilities.size());
    assertEquals(Type.SELENOID, selenoidGridConfig.getType());

    for(DesiredCapabilities desiredCapability: slenoidDesiredCapabilities) {
      assertEquals(true, desiredCapability.asMap().get("enableVNC"));
      assertEquals(true, desiredCapability.asMap().get("enableVideo"));
    }
  }

  @Test
  @DisplayName("build browserstack config")
  void test003() {
    Result<Properties> apply = propertyReader().apply(CONFIG_FOLDER + "config-003");
    Properties configProperties = apply.get();

    WebdriversConfig config = factory.createFromProperies(configProperties);

    assertEquals(1, config.getGridConfigs().size());

    GridConfig genericGridConfig = config.getGridConfigs().stream()
                                         .filter(grid -> grid.getName().equals("browserstack")).findFirst().get();

    assertEquals("https://danielnordhoffve1:abc@hub-cloud.browserstack.com/wd/hub",
        genericGridConfig.getTarget());
    assertEquals(Type.BROWSERSTACK, genericGridConfig.getType());
  }

  @Test
  @DisplayName("build saucelabs config")
  void test004() {
    Result<Properties> apply = propertyReader().apply(CONFIG_FOLDER + "config-004");
    Properties configProperties = apply.get();

    WebdriversConfig config = factory.createFromProperies(configProperties);

    assertEquals(1, config.getGridConfigs().size());

    GridConfig sauceLabsGridConfig = config.getGridConfigs().stream()
                                           .filter(grid -> grid.getName().equals("saucelabs")).findFirst().get();

    assertEquals("https://dve81:abc@ondemand.saucelabs.com:443/wd/hub",
        sauceLabsGridConfig.getTarget());
    assertEquals(Type.SAUCELABS, sauceLabsGridConfig.getType());
  }

  @Test
  @DisplayName("build grid config without os and versions")
  void test005() {
    Result<Properties> apply = propertyReader().apply(CONFIG_FOLDER + "config-005");
    Properties configProperties = apply.get();

    WebdriversConfig config = factory.createFromProperies(configProperties);

    assertEquals(1, config.getGridConfigs().size());

    GridConfig genericGridConfig = config.getGridConfigs().stream()
                                         .filter(grid -> grid.getName().equals("generic")).findFirst().get();

    assertEquals(2, genericGridConfig.getDesiredCapabilities().size());
    assertEquals(Platform.ANY, genericGridConfig.getDesiredCapabilities().get(0).getPlatform());
    assertEquals("", genericGridConfig.getDesiredCapabilities().get(0).getVersion());
    assertEquals(Platform.ANY, genericGridConfig.getDesiredCapabilities().get(1).getPlatform());
    assertEquals("", genericGridConfig.getDesiredCapabilities().get(1).getVersion());
  }
}
