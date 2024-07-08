/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.routerlink;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

import java.util.Map;

@Tag(Tag.DIV)
@Route(value = RouterLinkView.ROUTE, registerAtStartup = false)
public class RouterLinkView extends Component implements HasComponents {

    public static final String ROUTE = "router-link-test";

    public RouterLinkView() {
        // targetless router link
        var targetlessRouterLink = new RouterLink();
        targetlessRouterLink.setText("No Target");

        // static router link
        var staticRouterLink = new RouterLink("Static Target",
                RouterLinkStaticTargetView.class);

        // url parameter router link - empty
        var emptyUrlParameterRouterLink = new RouterLink("Empty URL Parameter Target",
                RouterLinkUrlParameterTargetView.class);

        // url parameter router link - non-empty
        var urlParameterRouterLink = new RouterLink("URL Parameter Target",
                RouterLinkUrlParameterTargetView.class,
                "parameter-value");

        // query parameter router link
        var queryParameterRouterLink = new RouterLink("Query Parameter Target",
                RouterLinkQueryParameterTargetView.class);
        queryParameterRouterLink.setQueryParameters(
                QueryParameters.empty()
                        .merging("parameter1", "parameter1-value")
                        .merging("parameter2", "parameter2-value1", "parameter2-value2"));

        // route parameter router link
        var routeParameterRouterLink = new RouterLink("Route Parameter Target",
                RouterLinkRouteParameterTargetView.class,
                new RouteParameters(Map.ofEntries(
                        Map.entry("segment2", "segment2-value"),
                        Map.entry("segment3", "segment3-value1/segment3-value2"))));

        add(targetlessRouterLink);
        add(staticRouterLink);
        add(emptyUrlParameterRouterLink);
        add(urlParameterRouterLink);
        add(queryParameterRouterLink);
        add(routeParameterRouterLink);
    }
}
