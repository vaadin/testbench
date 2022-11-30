/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

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
            Assert.assertTrue(
                    "Expecting default route to be active, but was not",
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
            Assert.assertTrue(routes.containsAll(allViews));
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
            Assert.assertTrue(
                    "Expecting service to be "
                            + TestCustomInstantiatorFactory.class
                                    .getSimpleName()
                            + " but was " + service.getClass().getSimpleName(),
                    service instanceof TestCustomInstantiatorFactory);
        }
    }

    public static class WrongBaseClassTest extends UIUnitTest {

        @Test
        public void navigate_fails() {
            assertExecutionFails(() -> navigate(WelcomeView.class));
            assertExecutionFails(() -> navigate("welcome", WelcomeView.class));
            assertExecutionFails(() -> navigate(ParametrizedView.class, 12));
            assertExecutionFails(
                    () -> navigate(ParametrizedView.class, Map.of()));
        }

        @Test
        public void getCurrentView_fails() {
            assertExecutionFails(this::getCurrentView);
        }

        @Test
        public void fireShortcut_fails() {
            assertExecutionFails(() -> fireShortcut(Key.ENTER));
        }

        @Test
        public void wrap_fails() {
            assertExecutionFails(() -> test(new ComponentTesterTest.Span()));
            assertExecutionFails(() -> test(ComponentTester.class,
                    new ComponentTesterTest.Span()));
        }

        @Test
        public void query_fails() {
            assertExecutionFails(() -> $(Component.class));
            assertExecutionFails(
                    () -> $(Component.class, new ComponentTesterTest.Span()));
            assertExecutionFails(() -> $view(Component.class));
        }

        private void assertExecutionFails(ThrowingRunnable executable) {
            UIUnitTestSetupException exception = Assert
                    .assertThrows(UIUnitTestSetupException.class, executable);
            Assertions.assertTrue(exception.getMessage().contains("JUnit 5"));
        }
    }

}
