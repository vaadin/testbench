/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.mocks;

import com.vaadin.testbench.unit.internal.MockRequestCustomizer;

/**
 * Configures mock request with authentication details from Spring Security.
 *
 * For internal use only.
 */
public class SpringSecurityRequestCustomizer implements MockRequestCustomizer {

    @Override
    public void apply(MockRequest request) {
        MockSpringServlet.applySpringSecurityIfPresent(request);
    }
}
