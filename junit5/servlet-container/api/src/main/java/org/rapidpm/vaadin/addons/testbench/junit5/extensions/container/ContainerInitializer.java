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
package org.rapidpm.vaadin.addons.testbench.junit5.extensions.container;

import static org.rapidpm.vaadin.addons.junit5.extensions.ExtensionFunctions.store;
import static org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.NetworkFunctions.freePort;
import static org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.NetworkFunctions.localeIP;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.rapidpm.dependencies.core.logger.HasLogger;

public interface ContainerInitializer extends HasLogger {

  void beforeAll(Class<?> testClass , ExtensionContext context) throws Exception;

  void beforeEach(Method testMethod , ExtensionContext context) throws Exception;

  void afterEach(Method testMethod , ExtensionContext context) throws Exception;

  void afterAll(Class<?> testClass , ExtensionContext context) throws Exception;

  default String prepareIP(ExtensionContext context) {
    final String serverIP = localeIP().get();
    store().apply(context).put(NetworkFunctions.SERVER_IP, serverIP);
    logger().info(
         "IP ServletContainerExtension - will be -> " + serverIP);
    return serverIP;
  }

  default int preparePort(ExtensionContext context) {
    final int port = freePort().get().ifAbsent(() -> {
      throw new RuntimeException("no free Port available...");
    }).get();
    store().apply(context).put(NetworkFunctions.SERVER_PORT, port);
    logger().info(
        "Port ServletContainerExtension - will be -> " + port);
    return port;
  }

  default void cleanUpPort(ExtensionContext context) {
    store().apply(context).remove(NetworkFunctions.SERVER_PORT);
  }

  default void cleanUpIP(ExtensionContext context) {
    store().apply(context).remove(NetworkFunctions.SERVER_IP);
  }

}
