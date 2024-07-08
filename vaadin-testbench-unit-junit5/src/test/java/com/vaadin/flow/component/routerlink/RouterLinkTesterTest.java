/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.routerlink;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.testbench.unit.ComponentTester;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@ViewPackages
class RouterLinkTesterTest extends UIUnitTest {

    private ComponentTester<RouterLinkView> $routerLinkView;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RouterLinkView.class);
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RouterLinkStaticTargetView.class);
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RouterLinkUrlParameterTargetView.class);
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RouterLinkQueryParameterTargetView.class);
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RouterLinkRouteParameterTargetView.class);

        var routerLinkView = navigate(RouterLinkView.class);
        $routerLinkView = test(routerLinkView);
    }

    @Test
    void routerLink_targetless() {
        // get router link
        var targetlessRouterLink = $routerLinkView.find(RouterLink.class)
                .withText("No Target")
                .single();
        var $targetlessRouterLink = test(targetlessRouterLink);
        Assertions.assertNotNull($targetlessRouterLink,
                "Tester for targetless RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals("",
                $targetlessRouterLink.getHref());

        // verify its click action fails due to no navigation target
        Assertions.assertThrows(IllegalStateException.class, $targetlessRouterLink::click);
    }

    @Test
    void routerLink_static() {
        // get router link
        var staticRouterLink = $routerLinkView.find(RouterLink.class)
                .withText("Static Target")
                .single();
        var $staticRouterLink = test(staticRouterLink);
        Assertions.assertNotNull($staticRouterLink,
                "Tester for static RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkStaticTargetView.ROUTE,
                $staticRouterLink.getHref());

        // verify its click action returns correct target
        var targetView = $staticRouterLink.click();
        Assertions.assertInstanceOf(RouterLinkStaticTargetView.class, targetView);

        // verify navigation target is correct
        var $targetView = test(targetView);
        Assertions.assertDoesNotThrow(() -> $targetView.find(Span.class)
                .withText("Static Target View")
                .single());
    }

    @Test
    void routerLink_emptyUrlParameter() {
        var emptyUrlParameterRouterLink = $routerLinkView.find(RouterLink.class)
                .withText("Empty URL Parameter Target")
                .single();
        var $emptyUrlParameterRouterLink = test(emptyUrlParameterRouterLink);
        Assertions.assertNotNull($emptyUrlParameterRouterLink,
                "Tester for empty URL parameter RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkUrlParameterTargetView.ROUTE,
                $emptyUrlParameterRouterLink.getHref());

        // verify its click action returns correct target
        var targetView = $emptyUrlParameterRouterLink.click();
        Assertions.assertInstanceOf(RouterLinkUrlParameterTargetView.class, targetView);

        // verify navigation target is correct
        var $targetView = test(targetView);
        Assertions.assertDoesNotThrow(() -> $targetView.find(Span.class)
                .withText("URL Parameter Target View: {  }")
                .single());
    }

    @Test
    void routerLink_urlParameter() {
        var urlParameterRouterLink = $routerLinkView.find(RouterLink.class)
                .withText("URL Parameter Target")
                .single();
        var $urlParameterRouterLink = test(urlParameterRouterLink);
        Assertions.assertNotNull($urlParameterRouterLink,
                "Tester for URL parameter RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkUrlParameterTargetView.ROUTE + "/parameter-value",
                $urlParameterRouterLink.getHref());

        // verify its click action returns correct target
        var targetView = $urlParameterRouterLink.click();
        Assertions.assertInstanceOf(RouterLinkUrlParameterTargetView.class, targetView);

        // verify navigation target is correct
        var $targetView = test(targetView);
        Assertions.assertDoesNotThrow(() -> $targetView.find(Span.class)
                .withText("URL Parameter Target View: { parameter-value }")
                .single());
    }

    @Test
    void routerLink_queryParameter() {
        var queryParameterRouterLink = $routerLinkView.find(RouterLink.class)
                .withText("Query Parameter Target")
                .single();
        var $queryParameterRouterLink = test(queryParameterRouterLink);
        Assertions.assertNotNull($queryParameterRouterLink,
                "Tester for QueryParameter RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkQueryParameterTargetView.ROUTE +
                        "?parameter2=parameter2-value1&parameter2=parameter2-value2&parameter1=parameter1-value",
                $queryParameterRouterLink.getHref());

        // verify its click action returns correct target
        var targetView = $queryParameterRouterLink.click();
        Assertions.assertInstanceOf(RouterLinkQueryParameterTargetView.class, targetView);

        // verify navigation target is correct
        var $targetView = test(targetView);
        Assertions.assertDoesNotThrow(() -> $targetView.find(Span.class)
                .withText("Query Parameter Target View: { parameter1 = [parameter1-value]; parameter2 = [parameter2-value1, parameter2-value2] }")
                .single());
    }

    @Test
    void routerLink_routeParameter() {
        var routeParameterRouterLink = $routerLinkView.find(RouterLink.class)
                .withText("Route Parameter Target")
                .single();
        var $routeParameterRouterLink = test(routeParameterRouterLink);
        Assertions.assertNotNull($routeParameterRouterLink,
                "Tester for RouteParameter RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkRouteParameterTargetView.ROUTE +
                        "/static/segment2-value/segment3-value1/segment3-value2",
                $routeParameterRouterLink.getHref());

        // verify its click action returns correct target
        var targetView = $routeParameterRouterLink.click();
        Assertions.assertInstanceOf(RouterLinkRouteParameterTargetView.class, targetView);

        // verify navigation target is correct
        var $targetView = test(targetView);
        Assertions.assertDoesNotThrow(() -> $targetView.find(Span.class)
                .withText("Route Parameter Target View: { segment2 = segment2-value; segment3 = segment3-value1/segment3-value2 }")
                .single());
    }
}
