/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.testbench.unit.mocks.MockSpringServletService;
import com.vaadin.testbench.unit.mocks.MockSpringVaadinSession;

@ContextConfiguration(classes = SpringUIUnit4BaseClassTest.TestConfig.class)
public class SpringUIUnit4BaseClassTest extends SpringUIUnit4Test {

    @Test
    public void extendingBaseClass_runTest_vaadinSpringMockingIsSetup() {
        Assert.assertNotNull(
                "Expecting VaadinService to be available up, but was not",
                VaadinService.getCurrent());
        Assert.assertTrue(
                "Expecting VaadinService to be "
                        + MockSpringServletService.class,
                VaadinService.getCurrent() instanceof MockSpringServletService);

        Assert.assertNotNull(
                "Expecting VaadinSession to be available up, but was not",
                VaadinSession.getCurrent());
        Assert.assertTrue(
                "Expecting VaadinSession to be "
                        + MockSpringVaadinSession.class,
                VaadinSession.getCurrent() instanceof MockSpringVaadinSession);
    }

    // Empty configuration class used only to be able to bootstrap spring
    // ApplicationContext
    @Configuration
    static class TestConfig {

    }
}
