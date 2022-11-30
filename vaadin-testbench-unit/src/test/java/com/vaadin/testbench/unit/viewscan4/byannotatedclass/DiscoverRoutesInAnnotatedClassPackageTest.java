/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.viewscan4.byannotatedclass;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.testbench.unit.UIUnit4Test;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class DiscoverRoutesInAnnotatedClassPackageTest extends UIUnit4Test {

    @Test
    public void extendingBaseClass_runTest_routesAreDiscovered() {
        Set<Class<? extends Component>> routes = VaadinService.getCurrent()
                .getRouter().getRegistry().getRegisteredRoutes().stream()
                .map(RouteBaseData::getNavigationTarget)
                .collect(Collectors.toSet());
        Assert.assertEquals(1, routes.size());
        Assert.assertTrue(routes.contains(ViewPackagesTest4View.class));
    }
}
