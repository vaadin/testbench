/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("template/:param")
@Tag("div")
public class TemplatedParam extends Component implements BeforeEnterObserver {
    public String parameter;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        parameter = event.getRouteParameters().get("param").get();
    }
}
