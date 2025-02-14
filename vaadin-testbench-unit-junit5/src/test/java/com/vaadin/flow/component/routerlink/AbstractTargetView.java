/*
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.flow.component.routerlink;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Span;

abstract class AbstractTargetView extends Component implements HasComponents {

    final Span message;

    public AbstractTargetView() {
        message = new Span();
        add(message);
    }
}
