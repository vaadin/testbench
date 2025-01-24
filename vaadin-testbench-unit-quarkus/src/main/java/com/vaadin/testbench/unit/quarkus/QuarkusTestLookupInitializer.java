/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.quarkus;

import jakarta.servlet.ServletException;

import java.util.Collection;
import java.util.Map;

import com.vaadin.flow.di.LookupInitializer;
import com.vaadin.flow.function.VaadinApplicationInitializationBootstrap;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.testbench.unit.internal.MockRequestCustomizer;

/**
 * An extension of {@link LookupInitializer} that provides services for Quarkus
 * testing integration.
 *
 * Currently, provides integration with Quarkus security.
 *
 * For internal use only.
 */
public class QuarkusTestLookupInitializer extends LookupInitializer {

    @Override
    public void initialize(VaadinContext context,
            Map<Class<?>, Collection<Class<?>>> services,
            VaadinApplicationInitializationBootstrap bootstrap)
            throws ServletException {
        if (securityPresent()) {
            ensureService(services, MockRequestCustomizer.class,
                    QuarkusSecurityCustomizer.class);
        }
        super.initialize(context, services, bootstrap);
    }

    private boolean securityPresent() {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(
                    "io.quarkus.security.identity.SecurityIdentity") != null;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

}
