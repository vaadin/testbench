package com.vaadin.testbench.addons.junit5.extensions.container;

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

import static com.vaadin.testbench.addons.junit5.extensions.container.ExtensionContextFunctions.containerInfo;

public class ServletContainerExtension implements
        BeforeAllCallback,
        BeforeEachCallback,
        AfterEachCallback,
        AfterAllCallback,
        ParameterResolver {

    private final ContainerInitializer containerIntializer;

    public ServletContainerExtension() {
        ServiceLoader<ContainerInitializer> serviceLoader =
                ServiceLoader.load(ContainerInitializer.class);
        List<ContainerInitializer> initializers = new ArrayList<>();
        serviceLoader.forEach(initializers::add);

        if (initializers.isEmpty()) {
            throw new IllegalStateException("No implementation of ContainerInitializer found");
        }
        if (initializers.size() != 1) {
//      logger().warning("More than one implementation of ContainerInitializer found!");
        }
        containerIntializer = initializers.get(0);
//    logger().info("Using ContainerInitializer: " + containerIntializer.getClass().getName());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
//    logger().info("ServletContainerExtension - beforeEach");
        containerIntializer.beforeEach(context.getTestMethod().get(), context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
//    logger().info("ServletContainerExtension - afterEach");
        containerIntializer.afterEach(context.getTestMethod().get(), context);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
//    logger().info("ServletContainerExtension - beforeAll");
        containerIntializer.beforeAll(context.getTestClass().get(), context);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
//    logger().info("ServletContainerExtension - afterAll");
        containerIntializer.afterAll(context.getTestClass().get(), context);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
                                   ExtensionContext extensionContext) throws ParameterResolutionException {
        if (ContainerInfo.class.isAssignableFrom(parameterContext.getParameter().getType())) {
            return containerInfo(extensionContext);
        } else {
            throw new ParameterResolutionException("was not able to create ContainerInfo instance");
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
                                     ExtensionContext extensionContext) throws ParameterResolutionException {
        final Class<?> type = parameterContext.getParameter().getType();
        return ContainerInfo.class.isAssignableFrom(type);
    }
}
