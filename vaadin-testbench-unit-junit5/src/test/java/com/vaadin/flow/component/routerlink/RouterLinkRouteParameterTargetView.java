/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.routerlink;

import java.util.stream.Collectors;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Tag(Tag.DIV)
@Route(value = RouterLinkRouteParameterTargetView.ROUTE
        + RouterLinkRouteParameterTargetView.ROUTE_PARAMETERS, registerAtStartup = false)
public class RouterLinkRouteParameterTargetView extends AbstractTargetView
        implements BeforeEnterObserver {

    public static final String ROUTE = "router-link-route-parameter-target";
    public static final String ROUTE_PARAMETERS = "/:segment1?/static/:segment2/:segment3*";

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var routeParameters = event.getRouteParameters();

        message.setText(
                "Route Parameter Target View: { "
                        + routeParameters.getParameterNames().stream()
                                .map(name -> name + " = "
                                        + routeParameters.get(name).orElse(""))
                                .sorted().collect(Collectors.joining("; "))
                        + " }");
    }
}
