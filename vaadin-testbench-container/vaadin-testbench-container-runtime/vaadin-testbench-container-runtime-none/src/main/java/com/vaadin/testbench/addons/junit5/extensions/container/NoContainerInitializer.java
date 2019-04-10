package com.vaadin.testbench.addons.junit5.extensions.container;

/*-
 * #%L
 * vaadin-testbench-container-runtime-none
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

import java.lang.reflect.Method;
import java.util.Properties;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_WEBAPP;

@AutoService(ContainerInitializer.class)
public class NoContainerInitializer implements ContainerInitializer {

    public static final String CONTAINER_NONE_HOST = "container.none.host";
    public static final String CONTAINER_NONE_PORT = "container.none.port";
    public static final String CONTAINER_NONE_WEBAPP = "container.none.webapp";

    private final Properties props = properties();
    private final boolean isHostDefined = isKeyDefined(CONTAINER_NONE_HOST);
    private final boolean isPortDefined = isKeyDefined(CONTAINER_NONE_PORT);
    private final boolean isWebAppDefined = isKeyDefined(CONTAINER_NONE_WEBAPP);
    private final String host = props.getProperty(CONTAINER_NONE_HOST);
    private final Integer port = Integer.parseInt(props.getProperty(CONTAINER_NONE_PORT));
    private final String webapp = props.getProperty(CONTAINER_NONE_WEBAPP);

    private boolean isKeyDefined(String key) {
        return !props.getProperty(key, "").isEmpty();
    }

    @Override
    public void beforeAll(Class<?> testClass, ExtensionContext context) {
//    logger()
//        .info("Running tests from " + testClass.getName() + " against remote deployed application");
    }

    @Override
    public void beforeEach(Method testMethod, ExtensionContext context) {
//    if (! isHostDefined) logger().warning("Property " + CONTAINER_NONE_HOST + " is not defined");
//    if (! isPortDefined) logger().warning("Property " + CONTAINER_NONE_PORT + " is not defined");
//    if (! isWebAppDefined) logger().warning("Property " + CONTAINER_NONE_WEBAPP + " is not defined");

        final ExtensionContext.Store store = storeMethodPlain(context);

        store.put(SERVER_IP, host);
        store.put(SERVER_PORT, port);
        store.put(SERVER_WEBAPP, webapp);
    }

    @Override
    public void afterEach(Method testMethod, ExtensionContext context) {

    }

    @Override
    public void afterAll(Class<?> testClass, ExtensionContext context) {

    }
}
