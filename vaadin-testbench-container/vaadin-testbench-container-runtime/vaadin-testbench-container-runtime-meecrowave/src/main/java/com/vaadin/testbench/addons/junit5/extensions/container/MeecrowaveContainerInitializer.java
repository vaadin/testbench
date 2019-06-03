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
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo.InitializationScope;
import org.apache.meecrowave.Meecrowave;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeClassPlain;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo.InitializationScope.BEFORE_ALL;
import static com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo.InitializationScope.BEFORE_EACH;
import static com.vaadin.testbench.addons.junit5.extensions.container.ContainerInitializer.containerInfo;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_WEBAPP;

@AutoService(ContainerInitializer.class)
public class MeecrowaveContainerInitializer implements ContainerInitializer {

    private static final String MEECROWAVE_INSTANCE = "meecrowave_instance";

    @Override
    public void beforeAll(Class<?> testClass, ExtensionContext context) {
        if (containerInfo().getInitializationScope() == BEFORE_ALL)
            startAndStore(context, BEFORE_ALL);
    }

    @Override
    public void beforeEach(Method testMethod, ExtensionContext context) {
        if (containerInfo().getInitializationScope() == BEFORE_EACH)
            startAndStore(context, BEFORE_EACH);
    }

    @Override
    public void afterEach(Method testMethod, ExtensionContext context) {
        if (containerInfo().getInitializationScope() == BEFORE_EACH)
            stopAndRemove(context, BEFORE_EACH);
    }

    @Override
    public void afterAll(Class<?> testClass, ExtensionContext context) {
        if (containerInfo().getInitializationScope() == BEFORE_ALL)
            stopAndRemove(context, BEFORE_ALL);
    }

    private void startAndStore(ExtensionContext ctx, InitializationScope scope) {
        final Meecrowave.Builder builder = new Meecrowave.Builder();
        if (containerInfo().getPort() != null) {
            builder.setHttpPort(containerInfo().getPort());
        } else {
            builder.randomHttpPort();
            containerInfo().setPort(builder.getHttpPort());
        }
        builder.setHost(NetworkFunctions.localIp());
        builder.setHttp2(true);

        final Meecrowave meecrowave = new Meecrowave(builder).bake();

        final ExtensionContext.Store store = scope == BEFORE_EACH
                ? storeMethodPlain(ctx)
                : storeClassPlain(ctx);

        store.put(SERVER_IP, meecrowave.getConfiguration().getHost());
        store.put(SERVER_PORT, meecrowave.getConfiguration().getHttpPort());
        store.put(SERVER_WEBAPP, containerInfo().getWebapp());
        store.put(MEECROWAVE_INSTANCE, meecrowave);

    }

    private void stopAndRemove(ExtensionContext ctx, InitializationScope scope) {
        final ExtensionContext.Store store = scope == BEFORE_EACH
                ? storeMethodPlain(ctx)
                : storeClassPlain(ctx);

        ((Meecrowave) store.get(MEECROWAVE_INSTANCE)).close();
        store.remove(MEECROWAVE_INSTANCE);
        store.remove(SERVER_IP);
        store.remove(SERVER_PORT);
        store.remove(SERVER_WEBAPP);
    }
}
