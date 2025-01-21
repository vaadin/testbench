/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

@Route("ParameterizedView")
public class ParameterizedView extends Div implements HasUrlParameter<String> {

    private final Span parameterHolder;

    public ParameterizedView() {
        parameterHolder = new Span("EMPTY");
        parameterHolder.setId("parameter-holder");
        add(parameterHolder);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        parameterHolder.setText("PARAMETER: " + parameter);
    }
}
