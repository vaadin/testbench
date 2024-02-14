/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.quarkus;

import com.example.base.HelloWorldView;
import com.example.base.WelcomeView;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.testbench.unit.ViewPackages;
import com.vaadin.testbench.unit.mocks.MockVaadinSession;
import com.vaadin.testbench.unit.quarkus.mocks.MockQuarkusServletService;

@QuarkusTest
@ViewPackages(packages = "com.example")
class QuarkusUIUnitBaseClassTest extends QuarkusUIUnitTest {

    @Test
    void extendingBaseClass_runTest_vaadinMockingIsSetup() {
        Assertions.assertNotNull(VaadinService.getCurrent(),
                "Expecting VaadinService to be available up, but was not");
        Assertions.assertInstanceOf(MockQuarkusServletService.class,
                VaadinService.getCurrent(), "Expecting VaadinService to be "
                        + MockQuarkusServletService.class);

        Assertions.assertNotNull(VaadinSession.getCurrent(),
                "Expecting VaadinSession to be available up, but was not");
        Assertions.assertInstanceOf(MockVaadinSession.class,
                VaadinSession.getCurrent(),
                "Expecting VaadinService to be " + MockVaadinSession.class);

        Assertions.assertNotNull(VaadinRequest.getCurrent(),
                "Expecting VaadinSession to be available up, but was not");
    }

    @Test
    void extendingBaseClass_runTest_viewDependencyInjectionWorks() {
        HelloWorldView view = navigate(HelloWorldView.class);
        Assertions.assertNotNull(view.service,
                "Expected service to be injected but field is null");

        WelcomeView view2 = navigate(WelcomeView.class);
        Assertions.assertNotNull(view2.service,
                "Expected service to be injected but field is null");
    }

}
