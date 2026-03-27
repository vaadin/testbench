/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.quarkus.mocks;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.servlet.ServletException;

import java.lang.reflect.Field;

import org.jetbrains.annotations.NotNull;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.internal.ReflectTools;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.quarkus.QuarkusVaadinServlet;
import com.vaadin.testbench.unit.internal.Routes;
import com.vaadin.testbench.unit.internal.UIFactory;
import com.vaadin.testbench.unit.mocks.MockVaadinHelper;

/**
 * Makes sure that the {@link #routes} are properly registered, and that
 * {@link MockQuarkusServletService} is used instead of vanilla
 * {@link com.vaadin.quarkus.QuarkusVaadinServletService}.
 * 
 * @deprecated Replace the vaadin-testbench-unit dependency with
 *             browserless-test-junit6 and use the corresponding class from the
 *             com.vaadin.browserless package instead. This class will be
 *             removed in a future version.
 */
@Deprecated(forRemoval = true, since = "10.1")
public class MockQuarkusServlet extends QuarkusVaadinServlet {

    /**
     * Configuration object of all routes and error routes for test.
     */
    protected final Routes routes;
    /**
     * Factory used to build Flow UIs.
     */
    protected final transient UIFactory uiFactory;
    /**
     * The CDI bean manager.
     */
    protected final transient BeanManager beanManager;

    /**
     * Creates a {@link QuarkusVaadinServlet} for testing environment.
     *
     * @param routes
     *            routes available for testing.
     * @param beanManager
     *            the CDI bean manager
     * @param uiFactory
     *            the factory used to build Flow UIs.
     */
    public MockQuarkusServlet(Routes routes, BeanManager beanManager,
            @NotNull UIFactory uiFactory) {
        this.routes = routes;
        this.uiFactory = uiFactory;
        this.beanManager = beanManager;
        injectBeanManager();
    }

    private void injectBeanManager() {
        Field beanManagerField;
        try {
            beanManagerField = QuarkusVaadinServlet.class
                    .getDeclaredField("beanManager");
            ReflectTools.setJavaFieldValue(this, beanManagerField,
                    this.beanManager);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Cannot inject BeanManager field", e);
        }
    }

    @Override
    protected DeploymentConfiguration createDeploymentConfiguration()
            throws ServletException {
        MockVaadinHelper.mockFlowBuildInfo(this);
        return super.createDeploymentConfiguration();
    }

    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        final VaadinServletService service = new MockQuarkusServletService(this,
                deploymentConfiguration, beanManager, uiFactory);
        service.init();
        routes.register(service.getContext());
        return service;
    }

}
