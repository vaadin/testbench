/*
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.routerlink;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class RouterLinkTesterTest extends UIUnitTest {

    private RouterLinkView routerLinkView;

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

        routerLinkView = navigate(RouterLinkView.class);
    }

    @Test
    void routerLink_targetless() {
        // get router link
        var $targetlessRouterLink = test(routerLinkView.targetlessRouterLink);
        Assertions.assertNotNull($targetlessRouterLink,
                "Tester for targetless RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals("", $targetlessRouterLink.getHref());

        // verify its click action fails due to no navigation target
        Assertions.assertThrows(IllegalStateException.class,
                $targetlessRouterLink::click);
    }

    @Test
    void routerLink_static() {
        // get router link
        var $staticRouterLink = test(routerLinkView.staticRouterLink);
        Assertions.assertNotNull($staticRouterLink,
                "Tester for static RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkStaticTargetView.ROUTE,
                $staticRouterLink.getHref());

        assertNavigationSucceeded($staticRouterLink,
                RouterLinkStaticTargetView.class, "Static Target View");
    }

    @Test
    void routerLink_emptyUrlParameter() {
        var $emptyUrlParameterRouterLink = test(
                routerLinkView.emptyUrlParameterRouterLink);
        Assertions.assertNotNull($emptyUrlParameterRouterLink,
                "Tester for empty URL parameter RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkUrlParameterTargetView.ROUTE,
                $emptyUrlParameterRouterLink.getHref());

        assertNavigationSucceeded($emptyUrlParameterRouterLink,
                RouterLinkUrlParameterTargetView.class,
                "URL Parameter Target View: {  }");
    }

    @Test
    void routerLink_urlParameter() {
        var $urlParameterRouterLink = test(
                routerLinkView.urlParameterRouterLink);
        Assertions.assertNotNull($urlParameterRouterLink,
                "Tester for URL parameter RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(
                RouterLinkUrlParameterTargetView.ROUTE + "/parameter-value",
                $urlParameterRouterLink.getHref());

        assertNavigationSucceeded($urlParameterRouterLink,
                RouterLinkUrlParameterTargetView.class,
                "URL Parameter Target View: { parameter-value }");
    }

    @Test
    void routerLink_queryParameter() {
        var $queryParameterRouterLink = test(
                routerLinkView.queryParameterRouterLink);
        Assertions.assertNotNull($queryParameterRouterLink,
                "Tester for QueryParameter RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkQueryParameterTargetView.ROUTE
                + "?parameter2=parameter2-value1&parameter2=parameter2-value2&parameter1=parameter1-value",
                $queryParameterRouterLink.getHref());

        assertNavigationSucceeded($queryParameterRouterLink,
                RouterLinkQueryParameterTargetView.class,
                "Query Parameter Target View: { parameter1 = [parameter1-value]; parameter2 = [parameter2-value1, parameter2-value2] }");
    }

    @Test
    void routerLink_routeParameter() {
        var $routeParameterRouterLink = test(
                routerLinkView.routeParameterRouterLink);
        Assertions.assertNotNull($routeParameterRouterLink,
                "Tester for RouteParameter RouterLink not initialized.");

        // verify its href
        Assertions.assertEquals(RouterLinkRouteParameterTargetView.ROUTE
                + "/static/segment2-value/segment3-value1/segment3-value2",
                $routeParameterRouterLink.getHref());

        assertNavigationSucceeded($routeParameterRouterLink,
                RouterLinkRouteParameterTargetView.class,
                "Route Parameter Target View: { segment2 = segment2-value; segment3 = segment3-value1/segment3-value2 }");
    }

    private void assertNavigationSucceeded(RouterLinkTester<RouterLink> tester,
            Class<? extends AbstractTargetView> expectedTarget,
            String expectedMessage) {
        // verify its click action returns correct target
        var targetView = tester.click();
        Assertions.assertInstanceOf(expectedTarget, targetView);
        Assertions.assertSame(targetView, getCurrentView());

        // verify navigation target is correct
        Assertions.assertEquals(expectedMessage,
                expectedTarget.cast(targetView).message.getText());
    }
}
