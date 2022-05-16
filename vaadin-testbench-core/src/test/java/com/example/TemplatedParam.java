/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
