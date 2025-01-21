/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.quarkus;

import jakarta.enterprise.event.Observes;

import com.testapp.security.AnyRoleView;
import com.testapp.security.LoginView;
import com.testapp.security.ProtectedView;
import com.testapp.security.RoleRestrictedView;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.test.junit.QuarkusTestProfile;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.auth.NavigationAccessControl;
import com.vaadin.flow.server.auth.ViewAccessChecker;
import com.vaadin.testbench.unit.quarkus.mocks.MockQuarkusServletService;

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

    public static class ViewAccessCheckerConfig implements QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "test-security-vac";
        }

    }

    @IfBuildProfile("test-security-nac")
    public static class NavigationAccessControlInitializer {

        public void serviceInit(@Observes ServiceInitEvent event) {
            // Currently, @QuarkusTest starts the whole application, so we check
            // the VaadinService type to register routes only for UI Unit tests
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

    @IfBuildProfile("test-security-vac")
    public static class ViewAccessCheckerInitializer {

        public void serviceInit(@Observes ServiceInitEvent event) {
            // Currently, @QuarkusTest starts the whole application, so we check
            // the VaadinService type to register routes only for UI Unit tests
            if (event.getSource() instanceof MockQuarkusServletService) {
                registerRoutes();
                event.getSource().addUIInitListener(uiEvent -> {
                    ViewAccessChecker viewAccessChecker = new ViewAccessChecker();
                    viewAccessChecker.setLoginView(LoginView.class);
                    uiEvent.getUI().addBeforeEnterListener(viewAccessChecker);
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
