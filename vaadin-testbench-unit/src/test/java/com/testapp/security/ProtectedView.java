/**
 * Copyright (C) 2000-${year} Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.testapp.security;

import javax.annotation.security.PermitAll;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Tag("div")
@Route(value = "", registerAtStartup = false)
@PermitAll
public class ProtectedView extends Component implements HasComponents {
}
