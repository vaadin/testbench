/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
