/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

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

class UIUnitBaseClassTest {

    @Nested
    class TestMethodExecutionTest extends UIUnitTest {
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
    class DiscoverAllRoutesTest extends UIUnitTest {

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
    class CustomLookupServicesTest extends UIUnitTest {

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

}
