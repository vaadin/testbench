package com.vaadin.testbench.addons.junit5.extensions.container;

import com.google.auto.service.AutoService;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;

@AutoService(ContainerInitializer.class)
public class SpringBootContainerInitializer extends AbstractSpringBootContainerInitializer {

    private BiConsumer<ExtensionContext, ApplicationContext> storeApplicationContext() {
        return (context, springApplicationContext) -> storeMethodPlain().apply(context)
                .put(SPRING_BOOT_APPLICATION_CONTEXT,
                        springApplicationContext);
    }

    private Function<ExtensionContext, ApplicationContext> getApplicationContext() {
        return context -> storeMethodPlain().apply(context)
                .get(SPRING_BOOT_APPLICATION_CONTEXT,
                        ApplicationContext.class);
    }

    @Override
    public void startAndStoreApplicationContext(ExtensionContext context,
                                                Class<?> springBootMainClass,
                                                List<String> argsWithoutPort) {
        ApplicationContext applicationContext =
                SpringApplication.run(springBootMainClass, argsWithoutPort.toArray(new String[0]));

        storeApplicationContext().accept(context, applicationContext);
    }

    @Override
    public void stopSpringApplication(ExtensionContext context) {
        ApplicationContext applicationContext = getApplicationContext().apply(context);
        SpringApplication.exit(applicationContext);
    }
}
