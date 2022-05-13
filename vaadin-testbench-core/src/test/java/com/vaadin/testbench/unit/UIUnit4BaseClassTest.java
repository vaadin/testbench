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

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.example.base.About;
import com.example.base.Home;
import com.example.base.sub.SubView;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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
                    getCurrentView() instanceof Home);
        }

    }

    public static class DiscoverAllRoutesTest extends UIUnit4Test {
        @Override
        protected String scanPackage() {
            return "com.example.base";
        }

        @Test
        public void extendingBaseClass_runTest_routesAreDiscovered() {
            Set<Class<? extends Component>> routes = VaadinService.getCurrent()
                    .getRouter().getRegistry().getRegisteredRoutes().stream()
                    .map(RouteBaseData::getNavigationTarget)
                    .collect(Collectors.toSet());
            Assert.assertEquals(3, routes.size());
            Assert.assertTrue(routes.containsAll(
                    Set.of(Home.class, About.class, SubView.class)));
        }
    }

    public static class DiscoverRoutesInPackageTest extends UIUnit4Test {

        @Override
        protected String scanPackage() {
            return SubView.class.getPackageName();
        }

        @Test
        public void extendingBaseClass_runTest_routesAreDiscovered() {
            Set<Class<? extends Component>> routes = VaadinService.getCurrent()
                    .getRouter().getRegistry().getRegisteredRoutes().stream()
                    .map(RouteBaseData::getNavigationTarget)
                    .collect(Collectors.toSet());
            Assert.assertEquals(1, routes.size());
            Assert.assertTrue(routes.contains(SubView.class));
        }
    }

}
