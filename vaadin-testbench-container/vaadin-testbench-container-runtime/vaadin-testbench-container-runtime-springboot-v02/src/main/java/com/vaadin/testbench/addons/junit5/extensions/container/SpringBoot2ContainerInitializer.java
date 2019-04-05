package com.vaadin.testbench.addons.junit5.extensions.container;

import com.google.auto.service.AutoService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

@AutoService(ContainerInitializer.class)
public class SpringBoot2ContainerInitializer extends AbstractSpringBootContainerInitializer {

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
