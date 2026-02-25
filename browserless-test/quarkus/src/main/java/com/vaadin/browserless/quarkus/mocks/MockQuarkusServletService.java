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

import com.vaadin.browserless.internal.UIFactory;
import com.vaadin.browserless.mocks.MockInstantiator;
import com.vaadin.browserless.mocks.MockVaadinSession;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.quarkus.QuarkusVaadinServlet;
import com.vaadin.quarkus.QuarkusVaadinServletService;

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

    private final transient UIFactory uiFactory;

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
            UIFactory uiFactory) {
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
