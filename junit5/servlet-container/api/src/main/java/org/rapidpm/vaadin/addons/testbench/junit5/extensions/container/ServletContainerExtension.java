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

import static org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ExtensionContextFunctions.containerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.rapidpm.dependencies.core.logger.HasLogger;

/**
 *
 */
public class ServletContainerExtension implements BeforeAllCallback, BeforeEachCallback,
    AfterEachCallback, AfterAllCallback, ParameterResolver, HasLogger {

  private final ContainerInitializer containerIntializer;

  public ServletContainerExtension() {
    ServiceLoader<ContainerInitializer> serviceLoader =
        ServiceLoader.load(ContainerInitializer.class);
    List<ContainerInitializer> initializers = new ArrayList<>();
    serviceLoader.forEach(initializers::add);

    if (initializers.isEmpty()) {
      throw new IllegalStateException("No implementation of ContainerInitializer found");
    }
    if (initializers.size() != 1) {
      logger().warning("More than one implementation of ContainerInitializer found!");
    }
    containerIntializer = initializers.get(0);
    logger().info("Using ContainerInitializer: " + containerIntializer.getClass().getName());
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    logger().info("ServletContainerExtension - beforeEach");
    containerIntializer.beforeEach(context.getTestMethod().get(), context);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    logger().info("ServletContainerExtension - afterEach");
      containerIntializer.afterEach(context.getTestMethod().get(), context);
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    containerIntializer.beforeAll(context.getTestClass().get(), context);
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    containerIntializer.afterAll(context.getTestClass().get(), context);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    if (ContainerInfo.class.isAssignableFrom(parameterContext.getParameter().getType())) {
      return containerInfo().apply(extensionContext);
    } else {
      throw new ParameterResolutionException("was not able to create ContainerInfo instance");
    }
  }



  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    final Class<?> type = parameterContext.getParameter().getType();
    return ContainerInfo.class.isAssignableFrom(type);

  }
}
