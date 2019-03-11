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

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_WEBAPP;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import com.google.auto.service.AutoService;
import com.vaadin.dependencies.core.logger.HasLogger;

@AutoService(ContainerInitializer.class)
public class NoContainerInitializer implements ContainerInitializer, HasLogger {

  public static final String CONTAINER_NONE_HOST = "container.none.host";
  public static final String CONTAINER_NONE_PORT = "container.none.port";
  public static final String CONTAINER_NONE_WEBAPP = "container.none.webapp";

  private final Properties props = properties().get();

  private Function<String, Boolean> isKeyDefined() {
    return (key) -> {
      if (props.containsKey(key)) {
        final String property = props.getProperty(key);
        return ! property.isEmpty();
      } else {
        return false;
      }
    };
  }

  private final Boolean isHostDefined = isKeyDefined().apply(CONTAINER_NONE_HOST);
  private final Boolean isPortDefined = isKeyDefined().apply(CONTAINER_NONE_PORT);
  private final Boolean isWebAppDefined = isKeyDefined().apply(CONTAINER_NONE_WEBAPP);

  private final String host = props.getProperty(CONTAINER_NONE_HOST);
  private final Integer port = Integer.parseInt(props.getProperty(CONTAINER_NONE_PORT));
  private final String webapp = props.getProperty(CONTAINER_NONE_WEBAPP);

  @Override
  public void beforeAll(Class<?> testClass , ExtensionContext context) throws Exception {
    logger()
        .info("Running tests from " + testClass.getName() + " against remote deployed application");
  }

  @Override
  public void beforeEach(Method testMethod , ExtensionContext context) throws Exception {
    if (! isHostDefined) logger().warning("Property " + CONTAINER_NONE_HOST + " is not defined");
    if (! isPortDefined) logger().warning("Property " + CONTAINER_NONE_PORT + " is not defined");
    if (! isWebAppDefined) logger().warning("Property " + CONTAINER_NONE_WEBAPP + " is not defined");

    final ExtensionContext.Store store = storeMethodPlain().apply(context);

    store.put(SERVER_IP , host);
    store.put(SERVER_PORT , port);
    store.put(SERVER_WEBAPP , webapp);
  }

  @Override
  public void afterEach(Method testMethod , ExtensionContext context) throws Exception {

  }

  @Override
  public void afterAll(Class<?> testClass , ExtensionContext context) throws Exception {

  }

}
