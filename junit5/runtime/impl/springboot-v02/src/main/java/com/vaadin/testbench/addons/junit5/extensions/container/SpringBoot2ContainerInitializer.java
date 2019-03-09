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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import com.google.auto.service.AutoService;

@AutoService(ContainerInitializer.class)
public class SpringBoot2ContainerInitializer extends AbstractSpringBootContainerInitializer {

  private BiConsumer<ExtensionContext, ApplicationContext> storeApplicationContext() {
    return (context , springApplicationContext) -> storeMethodPlain().apply(context)
                                                                     .put(SPRING_BOOT_APPLICATION_CONTEXT ,
                                                                          springApplicationContext);
  }

  private Function<ExtensionContext, ApplicationContext> getApplicationContext() {
    return context -> storeMethodPlain().apply(context)
                                        .get(SPRING_BOOT_APPLICATION_CONTEXT ,
                                             ApplicationContext.class);
  }

  @Override
  public void startAndStoreApplicationContext(ExtensionContext context ,
                                              Class<?> springBootMainClass ,
                                              List<String> argsWithoutPort) {
    ApplicationContext applicationContext =
        SpringApplication.run(springBootMainClass , argsWithoutPort.toArray(new String[0]));

    storeApplicationContext().accept(context , applicationContext);
  }

  @Override
  public void stopSpringApplication(ExtensionContext context) {
    ApplicationContext applicationContext = getApplicationContext().apply(context);
    SpringApplication.exit(applicationContext);
  }
}
