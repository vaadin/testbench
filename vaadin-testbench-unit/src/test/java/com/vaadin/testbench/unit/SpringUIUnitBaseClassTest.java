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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

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
    }

    // Empty configuration class used only to be able to bootstrap spring
    // ApplicationContext
    @Configuration
    static class TestConfig {

    }
}
