/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.testapp.security;

import jakarta.annotation.security.RolesAllowed;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "any-role", registerAtStartup = false)
// Quarkus interprets @PermitAll annotation differently from Flow
// In Flow @PermitAll means all authenticated uses, whereas in Quarkus it lets
// everybody in, even without authentication.
// In Quarkus, the Flow @PermitAll behaviour can be obtained with
// @RolesAllowed("**").
@RolesAllowed("**")
public class AnyRoleView extends Component implements HasComponents {
}
