/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.viewscan4.bypackagenameandclass;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.base.child.ChildView;
import com.example.base.navigation.NavigationPostponeView;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.testbench.unit.UIUnit4Test;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages(classes = NavigationPostponeView.class, packages = "com.example.base.child")
public class DiscoverRoutesInPackageByClassAndNameTest extends UIUnit4Test {

    @Test
    public void extendingBaseClass_runTest_routesAreDiscovered() {
        Set<Class<? extends Component>> routes = VaadinService.getCurrent()
                .getRouter().getRegistry().getRegisteredRoutes().stream()
                .map(RouteBaseData::getNavigationTarget)
                .collect(Collectors.toSet());
        Assert.assertEquals(2, routes.size());
        Assert.assertTrue(routes.contains(ChildView.class));
        Assert.assertTrue(routes.contains(NavigationPostponeView.class));
    }
}
