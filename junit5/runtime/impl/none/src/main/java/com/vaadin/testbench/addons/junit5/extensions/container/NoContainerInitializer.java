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
package com.vaadin.testbench.addons.junit5.extensions.container;

import java.lang.reflect.Method;

import org.junit.jupiter.api.extension.ExtensionContext;
import com.vaadin.dependencies.core.logger.HasLogger;
import com.google.auto.service.AutoService;

@AutoService(ContainerInitializer.class)
public class NoContainerInitializer implements ContainerInitializer, HasLogger {

  @Override
  public void beforeAll(Class<?> testClass, ExtensionContext context) throws Exception {
    logger()
        .info("Running tests from " + testClass.getName() + " against remote deployed application");
  }

  @Override
  public void beforeEach(Method testMethod, ExtensionContext context) throws Exception {

  }

  @Override
  public void afterEach(Method testMethod, ExtensionContext context) throws Exception {

  }

  @Override
  public void afterAll(Class<?> testClass, ExtensionContext context) throws Exception {

  }

}
