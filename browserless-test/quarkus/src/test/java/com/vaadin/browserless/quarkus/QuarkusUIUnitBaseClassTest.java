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
