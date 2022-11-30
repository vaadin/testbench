/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.internal

import kotlin.test.expect
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.router.HasErrorParameter
import com.vaadin.flow.router.NotFoundException
import com.vaadin.flow.router.RouteNotFoundError
import com.vaadin.flow.server.VaadinContext
import com.vaadin.flow.server.startup.ApplicationRouteRegistry
import com.vaadin.testbench.unit.expectList
import com.vaadin.testbench.unit.mocks.MockVaadinHelper
import com.vaadin.testbench.unit.viewscan.byannotatedclass.ViewPackagesTestView
import com.vaadin.testbench.unit.viewscan4.byannotatedclass.ViewPackagesTest4View
import com.example.base.ErrorView
import com.example.base.HelloWorldView
import com.example.base.ParametrizedView
import com.example.base.WelcomeView
import com.example.base.child.ChildView
import com.example.base.navigation.NavigationPostponeView
import com.github.mvysny.dynatest.DynaNodeGroup
import com.github.mvysny.dynatest.DynaTestDsl
import com.github.mvysny.dynatest.expectThrows
import com.testapp.MyRouteNotFoundError


val allViews: Set<Class<out Component>> = setOf<Class<out Component>>(
        HelloWorldView::class.java, WelcomeView::class.java,
        ParametrizedView::class.java, ChildView::class.java, NavigationPostponeView::class.java,
        ViewPackagesTest4View::class.java, ViewPackagesTestView::class.java)
val allErrorRoutes: Set<Class<out HasErrorParameter<*>>> = setOf(ErrorView::class.java, MockRouteNotFoundError::class.java, MockInternalSeverError::class.java)

@DynaTestDsl
fun DynaNodeGroup.routesTestBatch() {
    afterEach { MockVaadin.tearDown() }

    // TODO: restrict scan until we have component wrapper test views on this codebase
    val packagesToScan = arrayOf("com.example.base", "com.vaadin.testbench.unit.viewscan",
            "com.vaadin.testbench.unit.viewscan4")

    test("All views discovered") {
        val routes: Routes = Routes().autoDiscoverViews(*packagesToScan)
        expect(allViews) { routes.routes.toSet() }
        expect(allErrorRoutes) { routes.errorRoutes.toSet() }
    }

    test("calling autoDiscoverViews() multiple times won't fail") {
        expect(allViews) { Routes().autoDiscoverViews(*packagesToScan).routes }
        expect(allViews) { Routes().autoDiscoverViews(*packagesToScan).routes }
    }

    // https://github.com/mvysny/karibu-testing/issues/50
    test("app-specific NotFoundException handler removes MockRouteNotFoundError") {
        val routes: Routes = Routes().autoDiscoverViews("com.example.base", "com.testapp", "com.vaadin.flow.router")
        expect(setOf(ErrorView::class.java, MockInternalSeverError::class.java, MyRouteNotFoundError::class.java, RouteNotFoundError::class.java)) { routes.errorRoutes.toSet() }
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

    test("MockRouteNotFoundError is called when the route doesn't exist and it contains informative error message") {
        val routes: Routes = Routes().autoDiscoverViews("com.example.base")
        MockVaadin.setup(routes)
        UI.getCurrent().navigate("A_VIEW_THAT_DOESNT_EXIST")
        val view = UI.getCurrent().internals.activeRouterTargetsChain.first();
        expect(MockRouteNotFoundError::class.java) {
            view::class.java
        }
        expectThrows(NotFoundException::class, "No route found for 'A_VIEW_THAT_DOESNT_EXIST': Couldn't find route for 'A_VIEW_THAT_DOESNT_EXIST'\nAvailable routes:") {
            throw (view as MockRouteNotFoundError).cause!!
        }
        expect(true) {
            val errorMessage = view.element.textRecursively2
            errorMessage.contains("Could not navigate to 'A_VIEW_THAT_DOESNT_EXIST'")
            errorMessage.contains("Reason: Couldn't find route for 'A_VIEW_THAT_DOESNT_EXIST'")
        }
    }

    test("merge routes") {
        val routes1 = Routes(mutableSetOf(HelloWorldView::class.java, WelcomeView::class.java, ViewPackagesTest4View::class.java,
                ViewPackagesTestView::class.java),
                mutableSetOf(ErrorView::class.java))
        val routes2 = Routes(mutableSetOf(ParametrizedView::class.java, ChildView::class.java, NavigationPostponeView::class.java),
                mutableSetOf(MockRouteNotFoundError::class.java, MockInternalSeverError::class.java))
        val merged = routes1.merge(routes2);
        expect(allViews) { merged.routes.toSet() }
        expect(allErrorRoutes) { merged.errorRoutes.toSet() }
    }
}
