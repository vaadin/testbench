/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.browserless.viewscan.byclasses;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.base.child.ChildView;
import com.example.base.navigation.NavigationPostponeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.server.VaadinService;

@ViewPackages(classes = { ChildView.class, NavigationPostponeView.class })
class DiscoverRoutesInPackageByClassTest extends BrowserlessTest {

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
