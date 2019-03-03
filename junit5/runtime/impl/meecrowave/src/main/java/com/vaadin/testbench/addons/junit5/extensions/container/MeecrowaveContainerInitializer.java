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

import static com.vaadin.dependencies.core.logger.Logger.getLogger;
import static com.vaadin.dependencies.core.properties.PropertiesResolver.propertyReader;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.store;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_WEBAPP;

import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.meecrowave.Meecrowave;
import org.junit.jupiter.api.extension.ExtensionContext;
import com.google.auto.service.AutoService;
import com.vaadin.dependencies.core.logger.HasLogger;

@AutoService(ContainerInitializer.class)
public class MeecrowaveContainerInitializer implements ContainerInitializer, HasLogger {

  private static final String MEECROWAVE_INSTANCE = "meecrowave_instance";

  private static final String MEECROWAVE_RAMPUP = "container.meecrowave.init";
  private static final String MEECROWAVE_RAMPUP_BEFORE_EACH = "beforeEach";
  private static final String MEECROWAVE_RAMPUP_BEFORE_ALL = "beforeAll";


  private static final Properties configProperties =
      propertyReader()
          .apply(CONFIG_FOLDER + CONFIG_FILE)
          .ifFailed(failed -> getLogger(MeecrowaveContainerInitializer.class).warning(failed))
          .getOrElse(Properties::new);

  @Override
  public void beforeAll(Class<?> testClass , ExtensionContext context) throws Exception {
    if (configProperties.contains(MEECROWAVE_RAMPUP)) {
      if (configProperties
          .get(MEECROWAVE_RAMPUP)
          .equals(MEECROWAVE_RAMPUP_BEFORE_ALL)) {
        logger().info("startAndStore at beforeAll");
        startAndStore(context);
      } else {
        logger().info("nothing to do at beforeAll");
      }
    } else {
      logger().info("property " + MEECROWAVE_RAMPUP + " not set");
    }
  }

  @Override
  public void beforeEach(Method testMethod , ExtensionContext context) throws Exception {
    if (configProperties.contains(MEECROWAVE_RAMPUP)) {
      if (configProperties
          .get(MEECROWAVE_RAMPUP)
          .equals(MEECROWAVE_RAMPUP_BEFORE_EACH)) {
        logger().info("startAndStore at beforeEach");
        startAndStore(context);
      } else {
        logger().info("nothing to do at beforeEach");
      }
    }
  }


  @Override
  public void afterEach(Method testMethod , ExtensionContext context) throws Exception {
    if (configProperties.contains(MEECROWAVE_RAMPUP)) {
      if (configProperties
          .get(MEECROWAVE_RAMPUP)
          .equals(MEECROWAVE_RAMPUP_BEFORE_EACH)) {
        logger().info("stopAndRemove at afterEach");
        stopAndRemove(context);
      } else {
        logger().info("nothing to do at afterEach");
      }
    }
  }


  @Override
  public void afterAll(Class<?> testClass , ExtensionContext context) throws Exception {
    if (configProperties.contains(MEECROWAVE_RAMPUP)) {
      if (configProperties
          .get(MEECROWAVE_RAMPUP)
          .equals(MEECROWAVE_RAMPUP_BEFORE_ALL)) {
        logger().info("stopAndRemove at afterAll");
        stopAndRemove(context);
      } else {
        logger().info("nothing to do at afterAll");

      }
    }
  }

  private void startAndStore(ExtensionContext context) {
    final Meecrowave meecrowave = new Meecrowave(new Meecrowave.Builder() {
      {
        randomHttpPort();
        setHost(NetworkFunctions.localeIP().get());
        setTomcatScanning(true);
        setTomcatAutoSetup(true);
        setHttp2(true);
      }
    }).bake();
    store().apply(context).put(SERVER_IP , meecrowave.getConfiguration().getHost());
    store().apply(context).put(SERVER_PORT , meecrowave.getConfiguration().getHttpPort());
    store().apply(context).put(SERVER_WEBAPP , "/");
    store().apply(context).put(MEECROWAVE_INSTANCE , meecrowave);
  }

  private void stopAndRemove(ExtensionContext context) {
    final ExtensionContext.Store store = store().apply(context);
    ((Meecrowave) store.get(MEECROWAVE_INSTANCE)).close();
    store.remove(MEECROWAVE_INSTANCE);
  }

}
