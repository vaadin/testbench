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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.ExtensionContext;
import com.vaadin.dependencies.core.logger.HasLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions;
import com.google.auto.service.AutoService;

@AutoService(ContainerInitializer.class)
public class SpringBoot2ContainerInitializer implements ContainerInitializer, HasLogger {

  private static final String SPRING_BOOT2_APPLICATION_CONTEXT = "spring-boot2-applicationContext";
  private static final String SPRING_BOOT2_APP_CLASS           = "spring-boot2-app-class";
  private static final String SPRING_BOOT2_ARGS                = "spring-boot2-args";

  @Override
  public void beforeAll(Class<?> testClass, ExtensionContext context) throws Exception {
    SpringBoot2Conf springBootConf =
        AnnotationUtils.getAnnotation(testClass, SpringBoot2Conf.class);
    if (springBootConf == null) {
      throw new IllegalStateException("No @SpringBoot2Conf annotations found");
    }
    Class<?> appClass = springBootConf.source();
    if (appClass == null) {
      throw new IllegalStateException("No app class defined");
    }
    ExtensionFunctions.storeClass().apply(context).put(SPRING_BOOT2_APP_CLASS, appClass);

    final List arrayList = new ArrayList();
    Collections.addAll(arrayList, springBootConf.args());
    ExtensionFunctions.storeClass().apply(context).put(SPRING_BOOT2_ARGS, arrayList);

  }

  private BiConsumer<ExtensionContext, ApplicationContext> storeApplicationContext() {
    return (context, springApplicationContext) -> ExtensionFunctions.store().apply(context)
                                                                    .put(SPRING_BOOT2_APPLICATION_CONTEXT, springApplicationContext);
  }

  private Function<ExtensionContext, ApplicationContext> getApplicationContext() {
    return context -> ExtensionFunctions.store().apply(context).get(SPRING_BOOT2_APPLICATION_CONTEXT,
                                                                    ApplicationContext.class
    );
  }

  private Consumer<ExtensionContext> removeApplicationContext() {
    return context -> ExtensionFunctions.store().apply(context).remove(SPRING_BOOT2_APPLICATION_CONTEXT);
  }

  @Override
  public void beforeEach(Method testMethod, ExtensionContext context) throws Exception {
    int port = preparePort(context);
    prepareIP(context);

    List<String> argsWithoutPort =
        ((List<String>) ExtensionFunctions.storeClass().apply(context).get(SPRING_BOOT2_ARGS, List.class)).stream()
                                                                                                          .filter(arg -> !arg.startsWith("--server.port=")).collect(Collectors.toList());
    argsWithoutPort.add("--server.port=" + port);
    Class<?> clazz = ExtensionFunctions.storeClass().apply(context).get(SPRING_BOOT2_APP_CLASS, Class.class);
    ApplicationContext applicationContext =
        SpringApplication.run(clazz, argsWithoutPort.toArray(new String[argsWithoutPort.size()]));

    storeApplicationContext().accept(context, applicationContext);
  }


  @Override
  public void afterEach(Method testMethod, ExtensionContext context) throws Exception {
    ApplicationContext applicationContext = getApplicationContext().apply(context);
    SpringApplication.exit(applicationContext);
    removeApplicationContext().accept(context);
    cleanUpPort(context);
    cleanUpIP(context);
  }


  @Override
  public void afterAll(Class<?> testClass, ExtensionContext context) throws Exception {
    ExtensionFunctions.storeClass().apply(context).remove(SPRING_BOOT2_APP_CLASS);
    ExtensionFunctions.storeClass().apply(context).remove(SPRING_BOOT2_ARGS);
  }
}
