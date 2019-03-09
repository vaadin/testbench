/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.testbench.addons.junit5.extensions.container;

import static com.vaadin.dependencies.core.properties.PropertiesResolver.propertyReader;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.freePort;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.localeIP;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.function.Supplier;

import org.junit.jupiter.api.extension.ExtensionContext;
import com.vaadin.dependencies.core.logger.HasLogger;

public interface ContainerInitializer extends HasLogger {

  String CONFIG_FOLDER = ".testbenchextensions/";
  String CONFIG_FILE = "container-config";

  void beforeAll(Class<?> testClass , ExtensionContext context) throws Exception;

  void beforeEach(Method testMethod , ExtensionContext context) throws Exception;

  void afterEach(Method testMethod , ExtensionContext context) throws Exception;

  void afterAll(Class<?> testClass , ExtensionContext context) throws Exception;

  default Supplier<Properties> properties() {
    return () -> propertyReader()
        .apply(CONFIG_FOLDER + CONFIG_FILE)
        .ifFailed(failed -> logger().warning(failed))
        .ifAbsent(() -> logger().warning("no properties file was loaded.."))
        .getOrElse(Properties::new);
  }

  @Deprecated
  default String prepareIP(ExtensionContext context) {
    final String serverIP = localeIP().get();
    storeMethodPlain().apply(context).put(SERVER_IP , serverIP);
    logger().info(
        "IP ServletContainerExtension - will be -> " + serverIP);
    return serverIP;
  }

  @Deprecated
  default int preparePort(ExtensionContext context) {
    final int port = freePort().get()
                               .ifAbsent(() -> {
                                 throw new RuntimeException("no free Port available...");
                               })
                               .get();
    storeMethodPlain().apply(context).put(SERVER_PORT , port);
    logger().info(
        "Port ServletContainerExtension - will be -> " + port);
    return port;
  }

  @Deprecated
  default void cleanUpPort(ExtensionContext context) {
    storeMethodPlain().apply(context).remove(SERVER_PORT);
  }

  @Deprecated
  default void cleanUpIP(ExtensionContext context) {
    storeMethodPlain().apply(context).remove(SERVER_IP);
  }

}
