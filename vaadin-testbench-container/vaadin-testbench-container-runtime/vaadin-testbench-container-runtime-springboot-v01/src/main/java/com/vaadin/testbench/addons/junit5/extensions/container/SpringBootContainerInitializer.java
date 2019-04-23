package com.vaadin.testbench.addons.junit5.extensions.container;

/*-
 * #%L
 * vaadin-testbench-container-runtime-springboot-v01
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import com.google.auto.service.AutoService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

@AutoService(ContainerInitializer.class)
public class SpringBootContainerInitializer extends AbstractSpringBootContainerInitializer {

    private void storeApplicationContext(ExtensionContext context, ApplicationContext springApplicationContext) {
        storeMethodPlain(context)
                .put(SPRING_BOOT_APPLICATION_CONTEXT,
                        springApplicationContext);
    }

    private ApplicationContext getApplicationContext(ExtensionContext context) {
        return storeMethodPlain(context)
                .get(SPRING_BOOT_APPLICATION_CONTEXT,
                        ApplicationContext.class);
    }

    @Override
    public void startAndStoreApplicationContext(ExtensionContext context,
                                                Class<?> springBootMainClass,
                                                List<String> argsWithoutPort) {
        ApplicationContext applicationContext =
                SpringApplication.run(springBootMainClass, argsWithoutPort.toArray(new String[0]));

        storeApplicationContext(context, applicationContext);
    }

    @Override
    public void stopSpringApplication(ExtensionContext context) {
        ApplicationContext applicationContext = getApplicationContext(context);
        SpringApplication.exit(applicationContext);
    }
}
