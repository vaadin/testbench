/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
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
package junit.org.rapidpm.dependencies.core.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.dependencies.core.properties.PropertiesResolver;

public class PropertiesResolverTest implements HasLogger {

  private PropertiesResolver propertiesResolver;

  @BeforeEach
  void setup() {
    propertiesResolver = new PropertiesResolver();
  }

  @Test
  @DisplayName("load from resource")
  void test001() {
    Properties properties = propertiesResolver.get("test001");

    assertNotNull(properties);

    assertEquals("20", properties.get("a.a"));
    assertEquals("Hello", properties.getProperty("a.b"));
  }


  @Test
  @DisplayName("load from resource and working dir")
  void test002() {
    Properties properties = propertiesResolver.get("test002");

    assertNotNull(properties);

    assertEquals("20", properties.get("a.a"));
    assertEquals("Hello", properties.getProperty("a.b"));
  }


  @Test
  @DisplayName("load from home dir")
  void test003() throws IOException {
    String homeDir = System.getProperty("user.home");
    File file = new File(homeDir, "test003.properties");
    createPropertiesFile(file);
    Properties properties = propertiesResolver.get("test003");

    assertNotNull(properties);

    assertEquals("20", properties.get("a.a"));
    assertEquals("Hello", properties.getProperty("a.b"));
  }

  @Test
  @DisplayName("load from dir specified in enviroment")
  void test004() throws IOException {
    Path tempDir = Files.createTempDirectory(getClass().getSimpleName());
    System.setProperty(PropertiesResolver.CONFIG_LOCATION_PROPERTY, tempDir.toString());

    File file = tempDir.resolve("test004.properties").toFile();

    createPropertiesFile(file);
    Properties properties = propertiesResolver.get("test004");

    assertNotNull(properties);

    assertEquals("20", properties.get("a.a"));
    assertEquals("Hello", properties.getProperty("a.b"));
  }

  private void createPropertiesFile(File file) throws IOException {
    logger().info("Create properties file: " + file);
    try (InputStream is = getClass().getResourceAsStream("/test001.properties");
         OutputStream os = new FileOutputStream(file)) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = is.read(buffer)) > 0) {
        os.write(buffer, 0, length);
      }
    }
  }
}
