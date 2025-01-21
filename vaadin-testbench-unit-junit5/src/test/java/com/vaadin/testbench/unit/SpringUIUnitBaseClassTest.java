/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.testbench.unit.mocks.MockSpringServletService;
import com.vaadin.testbench.unit.mocks.MockSpringVaadinSession;

@ContextConfiguration(classes = SpringUIUnitBaseClassTest.TestConfig.class)
class SpringUIUnitBaseClassTest extends SpringUIUnitTest {

    @Test
    void extendingBaseClass_runTest_vaadinSpringMockingIsSetup() {
        Assertions.assertNotNull(VaadinService.getCurrent(),
                "Expecting VaadinService to be available up, but was not");
        Assertions.assertInstanceOf(MockSpringServletService.class,
                VaadinService.getCurrent(), "Expecting VaadinService to be "
                        + MockSpringServletService.class);

        Assertions.assertNotNull(VaadinSession.getCurrent(),
                "Expecting VaadinSession to be available up, but was not");
        Assertions.assertInstanceOf(MockSpringVaadinSession.class,
                VaadinSession.getCurrent(), "Expecting VaadinService to be "
                        + MockSpringVaadinSession.class);

        Assertions.assertNotNull(VaadinRequest.getCurrent(),
                "Expecting VaadinSession to be available up, but was not");
        Assertions.assertInstanceOf(MockSpringVaadinSession.class,
                VaadinSession.getCurrent(), "Expecting VaadinService to be "
                        + MockSpringVaadinSession.class);

    }

    // Empty configuration class used only to be able to bootstrap spring
    // ApplicationContext
    @Configuration
    static class TestConfig {

    }
}
