/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.testbench.unit.quarkus.mocks;

import jakarta.enterprise.inject.spi.BeanManager;

import kotlin.jvm.functions.Function0;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.quarkus.QuarkusVaadinServlet;
import com.vaadin.quarkus.QuarkusVaadinServletService;
import com.vaadin.testbench.unit.mocks.MockInstantiator;
import com.vaadin.testbench.unit.mocks.MockVaadinSession;

/**
 * A mocking service that performs three very important tasks:
 * <ul>
 * <li>Overrides {@link #isAtmosphereAvailable} to tell Vaadin that we don't
 * have Atmosphere (otherwise Vaadin will crash)</li>
 * <li>Provides some dummy value as a root ID via {@link #getMainDivId}
 * (otherwise the mocked servlet env will crash).</li>
 * <li>Provides a {@link MockVaadinSession} instead of
 * {@link com.vaadin.flow.server.VaadinSession}.</li>
 * </ul>
 * The class is intentionally opened, to be extensible in user's library.
 */
public class MockQuarkusServletService extends QuarkusVaadinServletService {

    private final transient Function0<UI> uiFactory;

    /**
     * Creates a new QuarkusVaadinServletService for testing.
     *
     * @param servlet
     *            the Quarkus Vaadin servlet.
     * @param configuration
     *            the deployment configuration.
     * @param beanManager
     *            the CDI bean manager.
     * @param uiFactory
     *            the factory used to build Flow UIs.
     */
    public MockQuarkusServletService(QuarkusVaadinServlet servlet,
            DeploymentConfiguration configuration, BeanManager beanManager,
            Function0<UI> uiFactory) {
        super(servlet, configuration, beanManager);
        this.uiFactory = uiFactory;
    }

    @Override
    protected boolean isAtmosphereAvailable() {
        return false;
    }

    @Override
    public String getMainDivId(VaadinSession session, VaadinRequest request) {
        return "ROOT-1";
    }

    @Override
    protected VaadinSession createVaadinSession(VaadinRequest request) {
        return new MockVaadinSession(this, uiFactory);
    }

    @Override
    public Instantiator getInstantiator() {
        return MockInstantiator.create(super.getInstantiator());
    }

}
