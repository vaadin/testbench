package com.vaadin.testbench.addons.junit5.extensions.container;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Properties;

import static com.vaadin.testbench.PropertiesResolver.readProperties;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.freePort;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.localIp;

public interface ContainerInitializer {

    String CONFIG_FOLDER = ".testbenchextensions/";
    String CONFIG_FILE = "container-config";

    void beforeAll(Class<?> testClass, ExtensionContext context) throws Exception;

    void beforeEach(Method testMethod, ExtensionContext context) throws Exception;

    void afterEach(Method testMethod, ExtensionContext context) throws Exception;

    void afterAll(Class<?> testClass, ExtensionContext context) throws Exception;

    default Properties properties() {
        return readProperties(CONFIG_FOLDER + CONFIG_FILE);
    }

    @Deprecated
    default String prepareIp(ExtensionContext context) {
        final String serverIp = localIp();
        storeMethodPlain(context).put(SERVER_IP, serverIp);
//    logger().info(
//        "IP ServletContainerExtension - will be -> " + serverIP);
        return serverIp;
    }

    @Deprecated
    default int preparePort(ExtensionContext context) {
        final int port = freePort().orElseThrow(() -> new RuntimeException("No free Port available..."));
        storeMethodPlain(context).put(SERVER_PORT, port);
//    logger().info(
//        "Port ServletContainerExtension - will be -> " + port);
        return port;
    }

    @Deprecated
    default void cleanupPort(ExtensionContext context) {
        storeMethodPlain(context).remove(SERVER_PORT);
    }

    @Deprecated
    default void cleanupIp(ExtensionContext context) {
        storeMethodPlain(context).remove(SERVER_IP);
    }
}
