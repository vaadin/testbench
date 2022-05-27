/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.testbench.unit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.testbench.unit.mocks.MockSpringServlet;

@Configuration
class SpringSupport {

    @Bean
    VaadinServiceInitListener setupRequestForAuthentication() {
        return event -> event.getSource()
                .addSessionInitListener(sessionEvent -> MockSpringServlet
                        .applySpringSecurityIfPresent());
    }

}
