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
package com.vaadin.browserless;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.browserless.mocks.MockSpringServletService;
import com.vaadin.browserless.mocks.MockSpringVaadinSession;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

@ContextConfiguration(classes = SpringBrowserlessBaseClassTest.TestConfig.class)
class SpringBrowserlessBaseClassTest extends SpringBrowserlessTest {

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
