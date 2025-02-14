/*
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.routerlink;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag(Tag.DIV)
@Route(value = RouterLinkStaticTargetView.ROUTE, registerAtStartup = false)
public class RouterLinkStaticTargetView extends AbstractTargetView
        implements HasComponents {

    public static final String ROUTE = "router-link-static-target";

    public RouterLinkStaticTargetView() {
        message.setText("Static Target View");
    }
}
