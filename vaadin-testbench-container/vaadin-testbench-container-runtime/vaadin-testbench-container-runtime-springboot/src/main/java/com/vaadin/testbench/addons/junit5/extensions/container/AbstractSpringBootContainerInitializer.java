package com.vaadin.testbench.addons.junit5.extensions.container;

/*-
 * #%L
 * vaadin-testbench-container-runtime-springboot
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

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.List;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeClassPlain;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;

public abstract class AbstractSpringBootContainerInitializer
        implements ContainerInitializer {

    public static final String SPRING_BOOT_APPLICATION_CONTEXT = "spring-boot-applicationContext";
    public static final String SPRING_BOOT_APP_CLASS = "spring-boot-app-class";
    public static final String SPRING_BOOT_ARGS = "spring-boot-args";
    public static final String SERVER_PORT = "--server.port=";
    public static final String NO_APP_CLASS_DEFINED = "No app class defined to define the SpringBoot Application Class";
    public static final String NOT_ANNOTATED = "No @SpringBootConf annotations at the testclass found";

    @Override
    public void beforeAll(Class<?> testClass, ExtensionContext context) {
        if (!isAnnotated(testClass, SpringBootConf.class)) {
            throw new IllegalStateException(NOT_ANNOTATED);
        } else {
            final SpringBootConf conf = testClass.getAnnotation(SpringBootConf.class);
            Class<?> appClass = conf.source();
            storeClassPlain(context)
                    .put(SPRING_BOOT_APP_CLASS, appClass);

            storeClassPlain(context)
                    .put(SPRING_BOOT_ARGS, asList(conf.args()));
        }
    }

    private void removeApplicationContext(ExtensionContext context) {
        storeMethodPlain(context)
                .remove(SPRING_BOOT_APPLICATION_CONTEXT);
    }

    @Override
    public void beforeEach(Method testMethod, ExtensionContext context) {
        int port = preparePort(context);
        prepareIp(context);

        final ExtensionContext.Store store = storeClassPlain(context);

        List<String> argsWithoutPort =
                ((List<String>) store.get(SPRING_BOOT_ARGS, List.class)).stream()
                        .filter(arg -> !arg.startsWith(SERVER_PORT))
                        .collect(toList());
        argsWithoutPort.add(SERVER_PORT + port);

        Class<?> clazz = store.get(SPRING_BOOT_APP_CLASS, Class.class);
        startAndStoreApplicationContext(context, clazz, argsWithoutPort);
    }

    public abstract void startAndStoreApplicationContext(ExtensionContext context,
                                                         Class<?> springBootMainClass,
                                                         List<String> argsWithoutPort);

    @Override
    public void afterEach(Method testMethod, ExtensionContext context) {
        stopSpringApplication(context);
        removeApplicationContext(context);
        cleanupPort(context);
        cleanupIp(context);
    }

    public abstract void stopSpringApplication(ExtensionContext context);

    @Override
    public void afterAll(Class<?> testClass, ExtensionContext context) {
        storeClassPlain(context).remove(SPRING_BOOT_APP_CLASS);
        storeClassPlain(context).remove(SPRING_BOOT_ARGS);
    }
}
