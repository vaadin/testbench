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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

public class ErrorView extends VerticalLayout
        implements HasErrorParameter<Exception> {
    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<Exception> parameter) {
        if (parameter.getException() instanceof NotFoundException) {
            throw (NotFoundException)parameter.getException();
        }
        throw new RuntimeException(parameter.getCaughtException());
    }
}
