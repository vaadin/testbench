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
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;

@Tag(Tag.DIV)
@Route(value = RouterLinkUrlParameterTargetView.ROUTE, registerAtStartup = false)
public class RouterLinkUrlParameterTargetView extends AbstractTargetView
        implements HasComponents, HasUrlParameter<String> {

    public static final String ROUTE = "router-link-url-parameter-target";

    @Override
    public void setParameter(BeforeEvent event,
            @OptionalParameter String parameter) {
        message.setText("URL Parameter Target View: { "
                + (parameter != null ? parameter : "") + " }");
    }
}
