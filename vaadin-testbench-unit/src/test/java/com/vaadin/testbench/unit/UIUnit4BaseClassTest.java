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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.di.InstantiatorFactory;
import com.vaadin.flow.di.Lookup;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import static org.junit.Assert.assertTrue;

@RunWith(Enclosed.class)
public class UIUnit4BaseClassTest {

    public static class TestMethodExecutionTest extends UIUnit4Test {

        @Test
        public void extendingBaseClass_runTest_vaadinInstancesAvailable() {
            Assert.assertNotNull(
                    "Expecting current UI to be available, but was not",
                    UI.getCurrent());
            Assert.assertNotNull(
                    "Expecting VaadinService to be available up, but was not",
                    VaadinService.getCurrent());
            Assert.assertNotNull(
                    "Expecting VaadinRequest to be available up, but was not",
                    VaadinRequest.getCurrent());
            Assert.assertNotNull(
                    "Expecting VaadinResponse to be available up, but was not",
                    VaadinResponse.getCurrent());
            Assert.assertNotNull(
                    "Expecting VaadinSession to be available up, but was not",
                    VaadinSession.getCurrent());
        }

        @Test
        public void extendingBaseClass_runTest_defaultRouteActive() {
            assertTrue("Expecting default route to be active, but was not",
                    getCurrentView() instanceof WelcomeView);
        }

    }

    public static class DiscoverAllRoutesTest extends UIUnit4Test {

        @Test
        public void extendingBaseClass_runTest_routesAreDiscovered() {
            Set<Class<? extends Component>> routes = VaadinService.getCurrent()
                    .getRouter().getRegistry().getRegisteredRoutes().stream()
                    .map(RouteBaseData::getNavigationTarget)
                    .collect(Collectors.toSet());
            Set<Class<? extends Component>> allViews = new HashSet<>(
                    TestRoutes.INSTANCE.getViews());
            allViews.add(SingleParam.class);
            allViews.add(TemplatedParam.class);
            Assert.assertEquals(allViews.size(), routes.size());
            assertTrue(routes.containsAll(allViews));
        }
    }

    public static class CustomLookupServicesTest extends UIUnit4Test {

        @Override
        protected Set<Class<?>> lookupServices() {
            return Set.of(TestCustomInstantiatorFactory.class);
        }

        @Test
        public void customService_availableInLookup() {
            Lookup lookup = VaadinService.getCurrent().getContext()
                    .getAttribute(Lookup.class);
            Assert.assertNotNull("Expecting Lookup to be initialized", lookup);

            InstantiatorFactory service = lookup
                    .lookup(InstantiatorFactory.class);
            Assert.assertNotNull(
                    "Expecting service to be available through Lookup",
                    service);
            assertTrue(
                    "Expecting service to be "
                            + TestCustomInstantiatorFactory.class
                                    .getSimpleName()
                            + " but was " + service.getClass().getSimpleName(),
                    service instanceof TestCustomInstantiatorFactory);
        }
    }

}
