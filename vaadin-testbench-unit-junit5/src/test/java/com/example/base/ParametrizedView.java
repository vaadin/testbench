/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.example.base;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

@Route("params")
public class ParametrizedView extends VerticalLayout
        implements HasUrlParameter<Integer> {

    Integer parameter;
    QueryParameters qp;

    @Override
    public void setParameter(BeforeEvent event, Integer parameter) {
        this.parameter = parameter;
        qp = event.getLocation().getQueryParameters();
    }
}
