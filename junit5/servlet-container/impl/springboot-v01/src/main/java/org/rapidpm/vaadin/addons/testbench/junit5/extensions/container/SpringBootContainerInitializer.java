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

import static java.util.Collections.addAll;
import static org.rapidpm.vaadin.addons.junit5.extensions.ExtensionFunctions.store;
import static org.rapidpm.vaadin.addons.junit5.extensions.ExtensionFunctions.storeClass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import com.google.auto.service.AutoService;


@AutoService(ContainerInitializer.class)
public class SpringBootContainerInitializer implements ContainerInitializer {
  private static final String SPRING_BOOT_APPLICATION_CONTEXT = "spring-boot-applicationContext";
  private static final String SPRING_BOOT_APP_CLASS = "spring-boot-app-class";
  private static final String SPRING_BOOT_ARGS = "spring-boot-args";

  @Override
  public void beforeAll(Class<?> testClass , ExtensionContext context) throws Exception {
    SpringBootConf springBootConf = AnnotationUtils.getAnnotation(testClass , SpringBootConf.class);
    if (springBootConf == null) {
      throw new IllegalStateException("No @SpringBootConf annotation found");
    }
    Class<?> appClass = springBootConf.source();
    if (appClass == null) {
      throw new IllegalStateException("No app class defined");
    }
    storeClass().apply(context).put(SPRING_BOOT_APP_CLASS , appClass);

    final ArrayList<String> arrayList = new ArrayList<>();
    addAll(arrayList , springBootConf.args());
    storeClass().apply(context).put(SPRING_BOOT_ARGS , arrayList
    );
  }

  private BiConsumer<ExtensionContext, ApplicationContext> storeApplicationContext() {
    return (context , springApplicationContext) -> store().apply(context)
                                                          .put(SPRING_BOOT_APPLICATION_CONTEXT , springApplicationContext);
  }

  private Function<ExtensionContext, ApplicationContext> getApplicationContext() {
    return context -> store().apply(context).get(SPRING_BOOT_APPLICATION_CONTEXT ,
                                                 ApplicationContext.class
    );
  }

  private Consumer<ExtensionContext> removeApplicationContext() {
    return context -> store().apply(context).remove(SPRING_BOOT_APPLICATION_CONTEXT);
  }

  @Override
  public void beforeEach(Method testMethod , ExtensionContext context) throws Exception {
    int port = preparePort(context);
    prepareIP(context);

    List<String> argsWithoutPort =
        ((List<String>) storeClass().apply(context).get(SPRING_BOOT_ARGS , List.class)).stream()
                                                                                       .filter(arg -> ! arg.startsWith("--server.port=")).collect(Collectors.toList());
    argsWithoutPort.add("--server.port=" + port);
    Class<?> clazz = storeClass().apply(context).get(SPRING_BOOT_APP_CLASS , Class.class);
    ApplicationContext applicationContext =
        SpringApplication.run(clazz , argsWithoutPort.toArray(new String[0]));

    storeApplicationContext().accept(context , applicationContext);
  }

  @Override
  public void afterEach(Method testMethod , ExtensionContext context) throws Exception {
    ApplicationContext applicationContext = getApplicationContext().apply(context);
    SpringApplication.exit(applicationContext);
    removeApplicationContext().accept(context);
    cleanUpPort(context);
    cleanUpIP(context);
  }

  @Override
  public void afterAll(Class<?> testClass , ExtensionContext context) throws Exception {
    storeClass().apply(context).remove(SPRING_BOOT_APP_CLASS);
    storeClass().apply(context).remove(SPRING_BOOT_ARGS);
  }
}
