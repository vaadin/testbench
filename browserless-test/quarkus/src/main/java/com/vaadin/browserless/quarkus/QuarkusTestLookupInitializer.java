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
package com.vaadin.browserless.quarkus;

import jakarta.servlet.ServletException;

import java.util.Collection;
import java.util.Map;

import com.vaadin.browserless.internal.MockRequestCustomizer;
import com.vaadin.flow.di.LookupInitializer;
import com.vaadin.flow.function.VaadinApplicationInitializationBootstrap;
import com.vaadin.flow.server.VaadinContext;

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
