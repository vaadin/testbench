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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.SingleParam;
import com.example.TemplatedParam;
import com.example.base.WelcomeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.InstantiatorFactory;
import com.vaadin.flow.di.Lookup;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.MenuAccessControl;

class UIUnitBaseClassTest {

    @Nested
    class TestMethodExecutionTest extends BrowserlessTest {
        @Test
        void extendingBaseClass_runTest_vaadinInstancesAvailable() {
            Assertions.assertNotNull(UI.getCurrent(),
                    "Expecting current UI to be available, but was not");
            Assertions.assertNotNull(VaadinService.getCurrent(),
                    "Expecting VaadinService to be available up, but was not");
            Assertions.assertNotNull(VaadinRequest.getCurrent(),
                    "Expecting VaadinRequest to be available up, but was not");
            Assertions.assertNotNull(VaadinResponse.getCurrent(),
                    "Expecting VaadinResponse to be available up, but was not");
            Assertions.assertNotNull(VaadinSession.getCurrent(),
                    "Expecting VaadinSession to be available up, but was not");
        }

        @Test
        void extendingBaseClass_runTest_defaultRouteActive() {
            Assertions.assertInstanceOf(WelcomeView.class, getCurrentView(),
                    "Expecting default route to be active, but was not");
        }

    }

    @Nested
    class DiscoverAllRoutesTest extends BrowserlessTest {

        @Test
        void extendingBaseClass_runTest_routesAreDiscovered() {
            Set<Class<? extends Component>> routes = VaadinService.getCurrent()
                    .getRouter().getRegistry().getRegisteredRoutes().stream()
                    .map(RouteBaseData::getNavigationTarget)
                    .collect(Collectors.toSet());
            Set<Class<? extends Component>> allViews = new HashSet<>(
                    TestRoutes.INSTANCE.getViews());
            allViews.add(SingleParam.class);
            allViews.add(TemplatedParam.class);
            Assertions.assertEquals(allViews.size(), routes.size());
            Assertions.assertTrue(routes.containsAll(allViews));
        }
    }

    @Nested
    class CustomLookupServicesTest extends BrowserlessTest {

        @Override
        protected Set<Class<?>> lookupServices() {
            return Set.of(TestCustomInstantiatorFactory.class);
        }

        @Test
        void customService_availableInLookup() {
            Lookup lookup = VaadinService.getCurrent().getContext()
                    .getAttribute(Lookup.class);
            Assertions.assertNotNull(lookup,
                    "Expecting Lookup to be initialized");

            InstantiatorFactory service = lookup
                    .lookup(InstantiatorFactory.class);
            Assertions.assertNotNull(service,
                    "Expecting service to be available through Lookup");
            Assertions.assertInstanceOf(TestCustomInstantiatorFactory.class,
                    service,
                    "Expecting service to be "
                            + TestCustomInstantiatorFactory.class
                                    .getSimpleName()
                            + " but was " + service.getClass().getSimpleName());
        }
    }

    @Nested
    class MenuAccessControlTest extends BrowserlessTest {

        @Test
        void menuAccessControl_instanceAvailable() {
            MenuAccessControl menuAccessControl = VaadinService.getCurrent()
                    .getInstantiator().getMenuAccessControl();
            Assertions.assertNotNull(menuAccessControl,
                    "Expecting MenuAccessControl to be available");

        }
    }

}
