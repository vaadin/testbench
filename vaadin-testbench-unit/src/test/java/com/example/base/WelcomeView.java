/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.example.base;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.PWA;

@Route("welcome")
@RouteAlias("")
@PWA(name = "My Foo PWA", shortName = "Foo PWA")
public class WelcomeView extends VerticalLayout {
    public WelcomeView() {
        setWidth(null);
        add(new Text("Welcome!"));
    }
}
