/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit.internal

import kotlin.test.expect
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.router.HasErrorParameter
import com.vaadin.flow.router.InternalServerError
import com.vaadin.flow.router.NotFoundException
import com.vaadin.flow.router.RouteNotFoundError
import com.vaadin.flow.server.VaadinContext
import com.vaadin.flow.server.startup.ApplicationRouteRegistry
import com.vaadin.testbench.unit.mocks.MockVaadinHelper
import com.example.base.ErrorView
import com.example.base.HelloWorldView
import com.example.base.ParametrizedView
import com.example.base.WelcomeView
import com.example.base.child.ChildView
import com.example.base.navigation.NavigationPostponeView
import com.testapp.MyRouteNotFoundError
import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.DynaTestDsl
import com.github.mvysny.dynatest.expectThrows


val allViews: Set<Class<out Component>> = setOf<Class<out Component>>(
        HelloWorldView::class.java, WelcomeView::class.java,
        ParametrizedView::class.java, ChildView::class.java, NavigationPostponeView::class.java)
val allErrorRoutes: Set<Class<out HasErrorParameter<*>>> = setOf(ErrorView::class.java, MockRouteNotFoundError::class.java)

@DynaTestDsl
fun DynaNodeGroup.routesTestBatch() {
    afterEach { MockVaadin.tearDown() }

    test("All views discovered") {
        val routes: Routes = Routes().autoDiscoverViews("com.example.base")
        expect(allViews) { routes.routes.toSet() }
        expect(allErrorRoutes) { routes.errorRoutes.toSet() }
    }

    test("calling autoDiscoverViews() multiple times won't fail") {
        expect(allViews) { Routes().autoDiscoverViews("com.example.base").routes }
        expect(allViews) { Routes().autoDiscoverViews("com.example.base").routes }
    }

    // https://github.com/mvysny/karibu-testing/issues/50
    test("app-specific NotFoundException handler removes MockRouteNotFoundError") {
        val routes: Routes = Routes().autoDiscoverViews("com.example.base", "com.testapp", "com.vaadin.flow.router")
        expect(setOf(ErrorView::class.java, InternalServerError::class.java, MyRouteNotFoundError::class.java, RouteNotFoundError::class.java)) { routes.errorRoutes.toSet() }
        // make sure that Vaadin initializes properly with this set of views
        MockVaadin.setup(routes)
    }

    test("PWA is ignored by default") {
        val routes: Routes = Routes().autoDiscoverViews("com.example.base")
        val ctx: VaadinContext = MockVaadinHelper.createMockVaadinContext()
        routes.register(ctx)
        expect(null) {
            @Suppress("DEPRECATION")
            ApplicationRouteRegistry.getInstance(ctx).pwaConfigurationClass
        }
    }

    test("PWA is discovered properly if need be") {
        val routes: Routes = Routes().autoDiscoverViews("com.example.base")
        routes.skipPwaInit = false
        val ctx: VaadinContext = MockVaadinHelper.createMockVaadinContext()
        routes.register(ctx)
        expect(WelcomeView::class.java) {
            @Suppress("DEPRECATION")
            ApplicationRouteRegistry.getInstance(ctx).pwaConfigurationClass
        }
    }

    test("MockRouteNotFoundError is called when the route doesn't exist, and it fails immediately with an informative error message") {
        val routes: Routes = Routes().autoDiscoverViews("com.example.base")
        MockVaadin.setup(routes)
        expectThrows(NotFoundException::class, "No route found for 'A_VIEW_THAT_DOESNT_EXIST': Couldn't find route for 'A_VIEW_THAT_DOESNT_EXIST'\nAvailable routes:") {
            UI.getCurrent().navigate("A_VIEW_THAT_DOESNT_EXIST")
        }
    }
}
