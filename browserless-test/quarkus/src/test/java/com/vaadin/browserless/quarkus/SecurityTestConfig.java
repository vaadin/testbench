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
package com.vaadin.browserless.quarkus;

import jakarta.enterprise.event.Observes;

import com.testapp.security.AnyRoleView;
import com.testapp.security.LoginView;
import com.testapp.security.ProtectedView;
import com.testapp.security.RoleRestrictedView;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.test.junit.QuarkusTestProfile;

import com.vaadin.browserless.quarkus.mocks.MockQuarkusServletService;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.auth.NavigationAccessControl;

// Since Quarkus 3.6 it should be possible to define beans in the profile
// class. For previous version the workaround is to set a custom configuration
// profile and use @IfBuildProfile annotation to isolate bean definitions
public class SecurityTestConfig {

    public static class NavigationAccessControlConfig
            implements QuarkusTestProfile {

        @Override
        public String getConfigProfile() {
            return "test-security-nac";
        }
    }

    @IfBuildProfile("test-security-nac")
    public static class NavigationAccessControlInitializer {

        public void serviceInit(@Observes ServiceInitEvent event) {
            // Currently, @QuarkusTest starts the whole application, so we check
            // the VaadinService type to register routes only for browserless
            // tests
            if (event.getSource() instanceof MockQuarkusServletService) {
                registerRoutes();
                event.getSource().addUIInitListener(uiEvent -> {
                    NavigationAccessControl accessControl = new NavigationAccessControl();
                    accessControl.setLoginView(LoginView.class);
                    uiEvent.getUI().addBeforeEnterListener(accessControl);
                });
            }
        }
    }

    private static void registerRoutes() {
        RouteConfiguration routeConfiguration = RouteConfiguration
                .forApplicationScope();
        routeConfiguration.setAnnotatedRoute(LoginView.class);
        routeConfiguration.setAnnotatedRoute(ProtectedView.class);
        routeConfiguration.setAnnotatedRoute(RoleRestrictedView.class);
        routeConfiguration.setAnnotatedRoute(AnyRoleView.class);
    }
}
