/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.SingleParam;
import com.example.TemplatedParam;
import com.example.base.ParametrizedView;
import com.example.base.WelcomeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
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

    @Nested
    class WrongBaseClassTest extends UIUnit4Test {

        @Test
        void navigate_fails() {
            assertExecutionFails(() -> navigate(WelcomeView.class));
            assertExecutionFails(() -> navigate("welcome", WelcomeView.class));
            assertExecutionFails(() -> navigate(ParametrizedView.class, 12));
            assertExecutionFails(
                    () -> navigate(ParametrizedView.class, Map.of()));
        }

        @Test
        void getCurrentView_fails() {
            assertExecutionFails(this::getCurrentView);
        }

        @Test
        void fireShortcut_fails() {
            assertExecutionFails(() -> fireShortcut(Key.ENTER));
        }

        @Test
        void wrap_fails() {
            assertExecutionFails(() -> test(new ComponentTesterTest.Span()));
            assertExecutionFails(() -> test(ComponentTester.class,
                    new ComponentTesterTest.Span()));
        }

        @Test
        void query_fails() {
            assertExecutionFails(() -> $(Component.class));
            assertExecutionFails(
                    () -> $(Component.class, new ComponentTesterTest.Span()));
            assertExecutionFails(() -> $view(Component.class));
        }

        private void assertExecutionFails(Executable executable) {
            UIUnitTestSetupException exception = Assertions
                    .assertThrows(UIUnitTestSetupException.class, executable);
            Assertions.assertTrue(exception.getMessage().contains("JUnit 4"));
        }
    }

}
