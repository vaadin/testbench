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
import java.util.Set;
import java.util.stream.Collectors;

import com.example.SingleParam;
import com.example.TemplatedParam;
import com.example.base.WelcomeView;
import com.example.base.child.ChildView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.BasicGridView;
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
    class DiscoverRoutesInPackageTest extends UIUnitTest {

        @Override
        protected String scanPackage() {
            return ChildView.class.getPackageName();
        }

        @Test
        void extendingBaseClass_runTest_routesAreDiscovered() {
            Set<Class<? extends Component>> routes = VaadinService.getCurrent()
                    .getRouter().getRegistry().getRegisteredRoutes().stream()
                    .map(RouteBaseData::getNavigationTarget)
                    .collect(Collectors.toSet());
            Assertions.assertEquals(1, routes.size());
            Assertions.assertTrue(routes.contains(ChildView.class));
        }
    }

}
