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

import static com.vaadin.testbench.TestBenchLogger.logger;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.storeMethodPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_WEBAPP;

@AutoService(ContainerInitializer.class)
public class NoContainerInitializer implements ContainerInitializer {

    @Override
    public void beforeAll(Class<?> testClass, ExtensionContext context) {
        logger().debug("Running tests from " + testClass.getName()
                + " against remote deployed application");
    }

    @Override
    public void beforeEach(Method testMethod, ExtensionContext context) {
        final ExtensionContext.Store store = storeMethodPlain(context);
        final ContainerInfo containerInfo = ContainerInitializer.containerInfo();

        store.put(SERVER_IP, containerInfo.getHost());
        store.put(SERVER_PORT, containerInfo.getPort());
        store.put(SERVER_WEBAPP, containerInfo.getWebapp());
    }

    @Override
    public void afterEach(Method testMethod, ExtensionContext context) {
        final ExtensionContext.Store store = storeMethodPlain(context);
        store.remove(SERVER_IP);
        store.remove(SERVER_PORT);
        store.remove(SERVER_WEBAPP);
    }

    @Override
    public void afterAll(Class<?> testClass, ExtensionContext context) {
    }
}
