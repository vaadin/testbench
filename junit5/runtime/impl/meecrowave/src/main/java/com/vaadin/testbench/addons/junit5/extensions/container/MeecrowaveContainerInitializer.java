package com.vaadin.testbench.addons.junit5.extensions.container;

import com.google.auto.service.AutoService;
import org.apache.meecrowave.Meecrowave;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.function.Function;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeClassPlain;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_WEBAPP;

@AutoService(ContainerInitializer.class)
public class MeecrowaveContainerInitializer implements ContainerInitializer {

    private static final String MEECROWAVE_INSTANCE = "meecrowave_instance";

    private static final String MEECROWAVE_RAMPUP = "container.meecrowave.init";
    private static final String MEECROWAVE_RAMPUP_BEFORE_EACH = "beforeEach";
    private static final String MEECROWAVE_RAMPUP_BEFORE_ALL = "beforeAll";

    private static final String MEECROWAVE_RAMPUP_BEFORE_DEFAULT = MEECROWAVE_RAMPUP_BEFORE_EACH;

    private final Properties props = properties().get();

    private Function<String, Boolean> isActiveNow() {
        return (key) -> {
            if (props.containsKey(MEECROWAVE_RAMPUP)) {
                final String property = props.getProperty(MEECROWAVE_RAMPUP);
                return property.equals(key);
            } else {
                //logger().info("isActiveNow - property " + MEECROWAVE_RAMPUP + " not set");
                return key.equals(MEECROWAVE_RAMPUP_BEFORE_DEFAULT);
            }
        };
    }

    @Override
    public void beforeAll(Class<?> testClass, ExtensionContext context) {
        if (isActiveNow().apply(MEECROWAVE_RAMPUP_BEFORE_ALL))
            startAndStore(context, MEECROWAVE_RAMPUP_BEFORE_ALL);
    }

    @Override
    public void beforeEach(Method testMethod, ExtensionContext context) {
        if (isActiveNow().apply(MEECROWAVE_RAMPUP_BEFORE_EACH))
            startAndStore(context, MEECROWAVE_RAMPUP_BEFORE_EACH);
    }

    @Override
    public void afterEach(Method testMethod, ExtensionContext context) {
        if (isActiveNow().apply(MEECROWAVE_RAMPUP_BEFORE_EACH))
            stopAndRemove(context, MEECROWAVE_RAMPUP_BEFORE_EACH);
    }

    @Override
    public void afterAll(Class<?> testClass, ExtensionContext context) {
        if (isActiveNow().apply(MEECROWAVE_RAMPUP_BEFORE_ALL))
            stopAndRemove(context, MEECROWAVE_RAMPUP_BEFORE_ALL);
    }

    private void startAndStore(ExtensionContext ctx, String key) {
        final Meecrowave meecrowave = new Meecrowave(new Meecrowave.Builder() {
            {
                randomHttpPort();
                setHost(NetworkFunctions.localeIP().get());
                setTomcatScanning(true);
                setTomcatAutoSetup(true);
                setHttp2(true);
            }
        }).bake();

        final boolean beforeEach = key.equals(MEECROWAVE_RAMPUP_BEFORE_EACH);
        final ExtensionContext.Store store = beforeEach
                ? storeMethodPlain().apply(ctx)
                : storeClassPlain().apply(ctx);

        store.put(SERVER_IP, meecrowave.getConfiguration().getHost());
        store.put(SERVER_PORT, meecrowave.getConfiguration().getHttpPort());
        store.put(SERVER_WEBAPP, "/"); //TODO make it configurable
        store.put(MEECROWAVE_INSTANCE, meecrowave);

    }

    private void stopAndRemove(ExtensionContext ctx, String key) {
        final ExtensionContext.Store store = (key.equals(MEECROWAVE_RAMPUP_BEFORE_EACH))
                ? storeMethodPlain().apply(ctx)
                : storeClassPlain().apply(ctx);

        ((Meecrowave) store.get(MEECROWAVE_INSTANCE)).close();
        store.remove(MEECROWAVE_INSTANCE);
    }
}
