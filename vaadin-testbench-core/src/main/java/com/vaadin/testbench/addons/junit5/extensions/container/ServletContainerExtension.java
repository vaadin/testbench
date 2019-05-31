package com.vaadin.testbench.addons.junit5.extensions.container;

/*-
 * #%L
 * vaadin-testbench-core
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

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import static com.vaadin.testbench.TestBenchLogger.logger;
import static com.vaadin.testbench.addons.junit5.extensions.container.ContainerInitializer.containerInfo;

public class ServletContainerExtension implements
        BeforeAllCallback,
        BeforeEachCallback,
        AfterEachCallback,
        AfterAllCallback,
        ParameterResolver {

    private static final String CONTAINER_INITIALIZER_CLASSNAME = ContainerInitializer.class.getSimpleName();
    private final ContainerInitializer containerIntializer;

    public ServletContainerExtension() {
        ServiceLoader<ContainerInitializer> serviceLoader =
                ServiceLoader.load(ContainerInitializer.class);
        List<ContainerInitializer> initializers = new ArrayList<>();
        serviceLoader.forEach(initializers::add);

        if (initializers.isEmpty()) {
            throw new IllegalStateException("No implementation of "
                    + CONTAINER_INITIALIZER_CLASSNAME + " found");
        }

        if (initializers.size() != 1) {
            logger().warn("More than one implementation of "
                    + CONTAINER_INITIALIZER_CLASSNAME + " found!");
        }

        containerIntializer = initializers.get(0);
        logger().debug("Using " + CONTAINER_INITIALIZER_CLASSNAME
                + ": " + containerIntializer.getClass().getName());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        logger().debug("ServletContainerExtension - beforeEach");
        containerIntializer.beforeEach(context.getTestMethod().get(), context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        logger().debug("ServletContainerExtension - afterEach");
        containerIntializer.afterEach(context.getTestMethod().get(), context);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        logger().debug("ServletContainerExtension - beforeAll");
        containerIntializer.beforeAll(context.getTestClass().get(), context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        logger().debug("ServletContainerExtension - afterAll");
        containerIntializer.afterAll(context.getTestClass().get(), context);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        if (ContainerInfo.class.isAssignableFrom(parameterContext.getParameter().getType())) {
            return containerInfo();
        } else {
            throw new ParameterResolutionException("Was not able to create ContainerInfo ");
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        final Class<?> type = parameterContext.getParameter().getType();
        return ContainerInfo.class.isAssignableFrom(type);
    }
}
