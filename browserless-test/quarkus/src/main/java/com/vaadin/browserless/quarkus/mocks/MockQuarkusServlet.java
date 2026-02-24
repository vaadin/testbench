/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.browserless.quarkus.mocks;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.servlet.ServletException;

import java.lang.reflect.Field;

import org.jetbrains.annotations.NotNull;

import com.vaadin.browserless.internal.Routes;
import com.vaadin.browserless.internal.UIFactory;
import com.vaadin.browserless.mocks.MockVaadinHelper;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.internal.ReflectTools;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.quarkus.QuarkusVaadinServlet;

/**
 * Makes sure that the {@link #routes} are properly registered, and that
 * {@link MockQuarkusServletService} is used instead of vanilla
 * {@link com.vaadin.quarkus.QuarkusVaadinServletService}.
 */
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
