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
