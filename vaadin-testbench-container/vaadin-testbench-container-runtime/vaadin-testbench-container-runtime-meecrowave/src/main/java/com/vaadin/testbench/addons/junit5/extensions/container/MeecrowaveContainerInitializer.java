package com.vaadin.testbench.addons.junit5.extensions.container;

/*-
 * #%L
 * vaadin-testbench-container-runtime-meecrowave
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
import org.apache.meecrowave.Meecrowave;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Properties;

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

    private final Properties props = properties();

    private boolean isActiveNow(String key) {
        if (props.containsKey(MEECROWAVE_RAMPUP)) {
            final String property = props.getProperty(MEECROWAVE_RAMPUP);
            return property.equals(key);
        } else {
            //logger().info("isActiveNow - property " + MEECROWAVE_RAMPUP + " not set");
            return key.equals(MEECROWAVE_RAMPUP_BEFORE_DEFAULT);
        }
    }

    @Override
    public void beforeAll(Class<?> testClass, ExtensionContext context) {
        if (isActiveNow(MEECROWAVE_RAMPUP_BEFORE_ALL))
            startAndStore(context, MEECROWAVE_RAMPUP_BEFORE_ALL);
    }

    @Override
    public void beforeEach(Method testMethod, ExtensionContext context) {
        if (isActiveNow(MEECROWAVE_RAMPUP_BEFORE_EACH))
            startAndStore(context, MEECROWAVE_RAMPUP_BEFORE_EACH);
    }

    @Override
    public void afterEach(Method testMethod, ExtensionContext context) {
        if (isActiveNow(MEECROWAVE_RAMPUP_BEFORE_EACH))
            stopAndRemove(context, MEECROWAVE_RAMPUP_BEFORE_EACH);
    }

    @Override
    public void afterAll(Class<?> testClass, ExtensionContext context) {
        if (isActiveNow(MEECROWAVE_RAMPUP_BEFORE_ALL))
            stopAndRemove(context, MEECROWAVE_RAMPUP_BEFORE_ALL);
    }

    private void startAndStore(ExtensionContext ctx, String key) {
        final Meecrowave.Builder builder = new Meecrowave.Builder();
        final String meecrowavePort = "container.meecrowave.port";
        if (props.containsKey(meecrowavePort)) {
            builder.setHttpPort(Integer.parseInt(props.getProperty(meecrowavePort)));
        } else {
            builder.randomHttpPort();
        }
        builder.setHost(NetworkFunctions.localIp());
        builder.setHttp2(true);

        final Meecrowave meecrowave = new Meecrowave(builder).bake();

        final boolean beforeEach = key.equals(MEECROWAVE_RAMPUP_BEFORE_EACH);
        final ExtensionContext.Store store = beforeEach
                ? storeMethodPlain(ctx)
                : storeClassPlain(ctx);

        store.put(SERVER_IP, meecrowave.getConfiguration().getHost());
        store.put(SERVER_PORT, meecrowave.getConfiguration().getHttpPort());
        // TODO(sven): Make it configurable.
        store.put(SERVER_WEBAPP, "/");
        store.put(MEECROWAVE_INSTANCE, meecrowave);

    }

    private void stopAndRemove(ExtensionContext ctx, String key) {
        final ExtensionContext.Store store = (key.equals(MEECROWAVE_RAMPUP_BEFORE_EACH))
                ? storeMethodPlain(ctx)
                : storeClassPlain(ctx);

        ((Meecrowave) store.get(MEECROWAVE_INSTANCE)).close();
        store.remove(MEECROWAVE_INSTANCE);
    }
}
