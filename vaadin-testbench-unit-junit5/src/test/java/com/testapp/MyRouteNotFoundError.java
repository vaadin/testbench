/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.testapp;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

/**
 * Having an app-custom [NotFoundException] handler should not crash the mocking
 * process: https://github.com/mvysny/karibu-testing/issues/50
 */
public class MyRouteNotFoundError extends VerticalLayout
        implements HasErrorParameter<NotFoundException> {
    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<NotFoundException> parameter) {
        throw new RuntimeException(parameter.getException());
    }
}
