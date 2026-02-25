/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
