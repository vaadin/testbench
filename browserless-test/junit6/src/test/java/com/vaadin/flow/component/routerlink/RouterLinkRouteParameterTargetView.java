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
package com.vaadin.flow.component.routerlink;

import java.util.stream.Collectors;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Tag(Tag.DIV)
@Route(value = RouterLinkRouteParameterTargetView.ROUTE
        + RouterLinkRouteParameterTargetView.ROUTE_PARAMETERS, registerAtStartup = false)
public class RouterLinkRouteParameterTargetView extends AbstractTargetView
        implements BeforeEnterObserver {

    public static final String ROUTE = "router-link-route-parameter-target";
    public static final String ROUTE_PARAMETERS = "/:segment1?/static/:segment2/:segment3*";

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var routeParameters = event.getRouteParameters();

        message.setText(
                "Route Parameter Target View: { "
                        + routeParameters.getParameterNames().stream()
                                .map(name -> name + " = "
                                        + routeParameters.get(name).orElse(""))
                                .sorted().collect(Collectors.joining("; "))
                        + " }");
    }
}
