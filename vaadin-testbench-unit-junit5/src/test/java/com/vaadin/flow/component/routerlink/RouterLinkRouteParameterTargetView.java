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
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.stream.Collectors;

@Tag(Tag.DIV)
@Route(value = RouterLinkRouteParameterTargetView.ROUTE + RouterLinkRouteParameterTargetView.ROUTE_PARAMETERS,
        registerAtStartup = false)
public class RouterLinkRouteParameterTargetView extends Component
        implements HasComponents, BeforeEnterObserver {

    public static final String ROUTE = "router-link-route-parameter-target";
    public static final String ROUTE_PARAMETERS = "/:segment1?/static/:segment2/:segment3*";

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var routeParameters = event.getRouteParameters();

        add(new Span("Route Parameter Target View: { " +
                routeParameters.getParameterNames().stream()
                        .map(name -> name + " = " + routeParameters.get(name).orElse(""))
                        .sorted()
                        .collect(Collectors.joining("; ")) +
                " }"));
    }
}
