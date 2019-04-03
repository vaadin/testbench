package com.vaadin.testbench.addons.junit5.extensions.container;

import com.google.auto.service.AutoService;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.function.Function;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_WEBAPP;

@AutoService(ContainerInitializer.class)
public class NoContainerInitializer implements ContainerInitializer {

    public static final String CONTAINER_NONE_HOST = "container.none.host";
    public static final String CONTAINER_NONE_PORT = "container.none.port";
    public static final String CONTAINER_NONE_WEBAPP = "container.none.webapp";

    private final Properties props = properties().get();
    private final Boolean isHostDefined = isKeyDefined().apply(CONTAINER_NONE_HOST);
    private final Boolean isPortDefined = isKeyDefined().apply(CONTAINER_NONE_PORT);
    private final Boolean isWebAppDefined = isKeyDefined().apply(CONTAINER_NONE_WEBAPP);
    private final String host = props.getProperty(CONTAINER_NONE_HOST);
    private final Integer port = Integer.parseInt(props.getProperty(CONTAINER_NONE_PORT));
    private final String webapp = props.getProperty(CONTAINER_NONE_WEBAPP);

    private Function<String, Boolean> isKeyDefined() {
        return (key) -> {
            if (props.containsKey(key)) {
                final String property = props.getProperty(key);
                return !property.isEmpty();
            } else {
                return false;
            }
        };
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

        final ExtensionContext.Store store = storeMethodPlain().apply(context);

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
