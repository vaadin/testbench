/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
