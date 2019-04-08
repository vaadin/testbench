package com.vaadin.testbench.addons.junit5.extensions.container;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
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
