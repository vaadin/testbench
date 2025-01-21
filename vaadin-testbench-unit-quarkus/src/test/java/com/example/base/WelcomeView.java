/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.example.base;

import jakarta.inject.Inject;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route("welcome")
@RouteAlias("")
public class WelcomeView extends VerticalLayout {

    @Inject
    public DummyService service;

    public WelcomeView() {
        setWidth(null);
        add(new Text("Welcome!"));
    }

}
