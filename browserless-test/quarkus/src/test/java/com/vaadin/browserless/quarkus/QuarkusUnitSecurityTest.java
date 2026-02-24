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
package com.vaadin.testbench.unit.quarkus;

import java.security.Principal;

import com.testapp.security.AnyRoleView;
import com.testapp.security.LoginView;
import com.testapp.security.ProtectedView;
import com.testapp.security.RoleRestrictedView;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.testbench.unit.ViewPackages;
import com.vaadin.testbench.unit.internal.MockRouteNotFoundError;

@ViewPackages(packages = "com.testapp.security")
@QuarkusTest
@TestProfile(SecurityTestConfig.NavigationAccessControlConfig.class)
class QuarkusUnitSecurityTest extends QuarkusUIUnitTest {

    @Test
    @TestSecurity(user = "john", roles = { "DEV", "PO" })
    void withMockUser_loggedUser_authenticationInformationAvailableOnRequest() {
        VaadinRequest request = VaadinRequest.getCurrent();
        Principal principal = request.getUserPrincipal();
        Assertions.assertNotNull(principal,
                "Principal should be provided by Quarkus Security, but was not found");
        Assertions.assertEquals("john", principal.getName());

        Assertions.assertTrue(request.isUserInRole("DEV"),
                "Principal should have DEV role");
        Assertions.assertTrue(request.isUserInRole("PO"),
                "Principal should have PO role");
        Assertions.assertFalse(request.isUserInRole("CEO"),
                "Principal should not have CEO role");

    }

    @Test
    void withoutSecurity_noAuthenticationInformation() {
        Principal principal = VaadinRequest.getCurrent().getUserPrincipal();
        Assertions.assertNull(principal,
                "Principal should not be present, but got " + principal);
    }

    @Test
    @TestSecurity(authorizationEnabled = false)
    void witAnonymousUser_noAuthenticationInformation() {
        Principal principal = VaadinRequest.getCurrent().getUserPrincipal();
        Assertions.assertNull(principal,
                "Principal should not be present, but got " + principal);
    }

    @Test
    @TestSecurity(user = "john", roles = { "DEV", "PO" })
    void withMockUser_landOnProtectedHomeView() {
        Assertions.assertInstanceOf(ProtectedView.class, getCurrentView(),
                "Logged user should land to protected home view");
    }

    @Test
    @TestSecurity(user = "john", roles = { "DEV" })
    void withMockUser_roleProtectedView_roleNotAssigned_viewNotFound() {
        navigate("role-restricted", MockRouteNotFoundError.class);
    }

    @Test
    @TestSecurity(user = "john", roles = { "PO" })
    void withMockUser_roleProtectedView_roleAssigned_navigateToView() {
        navigate("role-restricted", RoleRestrictedView.class);
        Assertions.assertInstanceOf(RoleRestrictedView.class, getCurrentView(),
                "Logged user should navigate to protected view");
    }

    /**
     * Tests @RoleAllowed("**"), that in Quarkus is expected to behave
     * like @PermitAll in Flow
     */
    @Test
    @TestSecurity(user = "john", roles = { "DEV" })
    void withMockUser_anyRoleProtectedView_roleNotAssigned_viewNotFound() {
        navigate(AnyRoleView.class);
        Assertions.assertInstanceOf(AnyRoleView.class, getCurrentView(),
                "Logged user should navigate to protected view");
    }

    @Test
    @TestSecurity(authorizationEnabled = false)
    void withAnonymousUser_redirectToLogin() {
        Assertions.assertInstanceOf(LoginView.class, getCurrentView(),
                "Anonymous user should be redirect to login view");
    }

    @Test
    @TestSecurity(authorizationEnabled = false)
    void withAnonymousUser_navigateToProtectedView_redirectToLogin() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> navigate(ProtectedView.class));
        Assertions.assertInstanceOf(LoginView.class, getCurrentView(),
                "Anonymous user should be redirect to login view");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> navigate(RoleRestrictedView.class));
        Assertions.assertInstanceOf(LoginView.class, getCurrentView(),
                "Anonymous user should be redirect to login view");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> navigate(AnyRoleView.class));
        Assertions.assertInstanceOf(LoginView.class, getCurrentView(),
                "Anonymous user should be redirect to login view");
    }

}
