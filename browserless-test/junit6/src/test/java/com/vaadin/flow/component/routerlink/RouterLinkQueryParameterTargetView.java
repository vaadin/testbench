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
@Route(value = RouterLinkQueryParameterTargetView.ROUTE, registerAtStartup = false)
public class RouterLinkQueryParameterTargetView extends AbstractTargetView
        implements BeforeEnterObserver {

    public static final String ROUTE = "router-link-query-parameter-target";

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var queryParameters = event.getLocation().getQueryParameters();
        message.setText(
                "Query Parameter Target View: { "
                        + queryParameters.getParameters().entrySet().stream()
                                .map(entry -> entry.getKey() + " = ["
                                        + entry.getValue().stream().sorted()
                                                .collect(Collectors
                                                        .joining(", "))
                                        + "]")
                                .sorted().collect(Collectors.joining("; "))
                        + " }");
    }
}
