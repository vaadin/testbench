/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.testbench.unit.viewscan.bypackagename;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.base.child.ChildView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages(packages = "com.example.base.child")
class DiscoverRoutesInPackageByNameTest extends UIUnitTest {

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
