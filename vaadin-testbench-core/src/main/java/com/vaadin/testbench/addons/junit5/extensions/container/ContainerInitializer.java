package com.vaadin.testbench.addons.junit5.extensions.container;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.function.Supplier;

import static com.vaadin.testbench.PropertiesResolver.propertyReader;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.freePort;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.localeIP;

public interface ContainerInitializer {

    String CONFIG_FOLDER = ".testbenchextensions/";
    String CONFIG_FILE = "container-config";

    void beforeAll(Class<?> testClass, ExtensionContext context) throws Exception;

    void beforeEach(Method testMethod, ExtensionContext context) throws Exception;

    void afterEach(Method testMethod, ExtensionContext context) throws Exception;

    void afterAll(Class<?> testClass, ExtensionContext context) throws Exception;

    default Supplier<Properties> properties() {
        return () -> propertyReader()
                .apply(CONFIG_FOLDER + CONFIG_FILE)
//        .ifFailed(failed -> logger().warning(failed))
//        .ifAbsent(() -> logger().warning("no properties file was loaded.."))
                .getOrElse(Properties::new);
    }

    @Deprecated
    default String prepareIP(ExtensionContext context) {
        final String serverIP = localeIP().get();
        storeMethodPlain().apply(context).put(SERVER_IP, serverIP);
//    logger().info(
//        "IP ServletContainerExtension - will be -> " + serverIP);
        return serverIP;
    }

    @Deprecated
    default int preparePort(ExtensionContext context) {
        final int port = freePort().get()
                .ifAbsent(() -> {
                    throw new RuntimeException("no free Port available...");
                })
                .get();
        storeMethodPlain().apply(context).put(SERVER_PORT, port);
//    logger().info(
//        "Port ServletContainerExtension - will be -> " + port);
        return port;
    }

    @Deprecated
    default void cleanUpPort(ExtensionContext context) {
        storeMethodPlain().apply(context).remove(SERVER_PORT);
    }

    @Deprecated
    default void cleanUpIP(ExtensionContext context) {
        storeMethodPlain().apply(context).remove(SERVER_IP);
    }
}
