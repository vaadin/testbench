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
import static org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.NetworkFunctions.localeIP;

import java.lang.reflect.Method;

import org.apache.meecrowave.Meecrowave;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.rapidpm.dependencies.core.logger.HasLogger;
import com.google.auto.service.AutoService;

@AutoService(ContainerInitializer.class)
public class MeecrowaveContainerInitializer implements ContainerInitializer, HasLogger {

  private static final String MEECROWAVE_INSTANCE = "meecrowave_instance";

  @Override
  public void beforeAll(Class<?> testClass , ExtensionContext context) throws Exception {
    logger().info("nothing to do at beforeAll");
  }

  @Override
  public void beforeEach(Method testMethod , ExtensionContext context) throws Exception {
    final Meecrowave meecrowave = new Meecrowave(new Meecrowave.Builder() {
      {
        randomHttpPort();
        setHost(localeIP().get());
        setTomcatScanning(true);
        setTomcatAutoSetup(true);
        setHttp2(true);
      }
    }).bake();
    store().apply(context).put(NetworkFunctions.SERVER_IP , meecrowave.getConfiguration().getHost());
    store().apply(context).put(NetworkFunctions.SERVER_PORT , meecrowave.getConfiguration().getHttpPort());
    store().apply(context).put(NetworkFunctions.SERVER_WEBAPP , "/");
    store().apply(context).put(MEECROWAVE_INSTANCE , meecrowave);


  }

  @Override
  public void afterEach(Method testMethod , ExtensionContext context) throws Exception {
    ((Meecrowave) store().apply(context).get(MEECROWAVE_INSTANCE)).close();
  }

  @Override
  public void afterAll(Class<?> testClass , ExtensionContext context) throws Exception {
    logger().info("nothing to do at afterAll");
  }
}
