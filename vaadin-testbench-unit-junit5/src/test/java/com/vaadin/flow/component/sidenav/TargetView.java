/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.sidenav;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Tag("div")
@Route(value = "sidenav-target", registerAtStartup = false)
@RouteAlias("sidenav-target/:param")
public class TargetView extends Component implements BeforeEnterObserver {

    String parameter;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        parameter = event.getRouteParameters().get("param").orElse("N/A");
    }
}
