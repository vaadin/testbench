/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.viewscan.byclasses;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.base.child.ChildView;
import com.example.base.navigation.NavigationPostponeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages(classes = { ChildView.class, NavigationPostponeView.class })
class DiscoverRoutesInPackageByClassTest extends UIUnitTest {

    @Test
    void extendingBaseClass_runTest_routesAreDiscovered() {
        Set<Class<? extends Component>> routes = VaadinService.getCurrent()
                .getRouter().getRegistry().getRegisteredRoutes().stream()
                .map(RouteBaseData::getNavigationTarget)
                .collect(Collectors.toSet());
        Assertions.assertEquals(2, routes.size());
        Assertions.assertTrue(routes.contains(ChildView.class));
        Assertions.assertTrue(routes.contains(NavigationPostponeView.class));
    }
}
