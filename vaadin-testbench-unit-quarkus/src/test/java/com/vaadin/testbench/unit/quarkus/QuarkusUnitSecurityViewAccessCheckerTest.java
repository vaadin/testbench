/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.quarkus;

import java.security.Principal;

import com.testapp.security.LoginView;
import com.testapp.security.ProtectedView;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.testbench.unit.ViewPackages;

@QuarkusTest
@ViewPackages(packages = "com.testapp.security")
@TestProfile(SecurityTestConfig.ViewAccessCheckerConfig.class)
class QuarkusUnitSecurityViewAccessCheckerTest extends QuarkusUIUnitTest {

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
    @TestSecurity(authorizationEnabled = false)
    void withAnonymousUser_redirectToLogin() {
        Assertions.assertInstanceOf(LoginView.class, getCurrentView(),
                "Anonymous user should be redirect to login view");
    }

}
