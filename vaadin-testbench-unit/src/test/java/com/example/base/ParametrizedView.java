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
