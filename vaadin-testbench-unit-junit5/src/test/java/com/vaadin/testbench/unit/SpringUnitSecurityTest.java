/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.security.Principal;

import com.testapp.security.LoginView;
import com.testapp.security.ProtectedView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.MenuAccessControl;

@ContextConfiguration(classes = SecurityTestConfig.NavigationAccessControlConfig.class)
@ViewPackages(packages = "com.testapp.security")
class SpringUnitSecurityTest extends SpringUIUnitTest {

    @Test
    @WithMockUser(username = "john", roles = { "DEV", "PO" })
    void withMockUser_loggedUser_authenticationInformationAvailableOnRequest() {
        VaadinRequest request = VaadinRequest.getCurrent();
        Principal principal = request.getUserPrincipal();
        Assertions.assertNotNull(principal,
                "Principal should be provided by Spring Security, but was not found");
        Assertions.assertEquals("john", principal.getName());

        Assertions.assertTrue(request.isUserInRole("DEV"),
                "Principal should have DEV role");
        Assertions.assertTrue(request.isUserInRole("PO"),
                "Principal should have PO role");
        Assertions.assertFalse(request.isUserInRole("CEO"),
                "Principal should not have CEO role");

        Assertions.assertTrue(request.isUserInRole("ROLE_DEV"),
                "Principal should have DEV role");
        Assertions.assertTrue(request.isUserInRole("ROLE_PO"),
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
    @WithAnonymousUser
    void witAnonymousUser_noAuthenticationInformation() {
        Principal principal = VaadinRequest.getCurrent().getUserPrincipal();
        Assertions.assertNull(principal,
                "Principal should not be present, but got " + principal);
    }

    @Test
    @WithUserDetails
    void withUserDetails_loggedUser_authenticationInformationAvailableOnRequest() {
        VaadinRequest request = VaadinRequest.getCurrent();
        Principal principal = request.getUserPrincipal();
        Assertions.assertNotNull(principal,
                "Principal should be provided by Spring Security, but was not found");
        Assertions.assertEquals("user", principal.getName());

        Assertions.assertTrue(request.isUserInRole("DEV"),
                "Principal should have DEV role");
        Assertions.assertTrue(request.isUserInRole("ROLE_DEV"),
                "Principal should have DEV role");
        Assertions.assertTrue(request.isUserInRole("SUPERUSER"),
                "Principal should have SUPERUSER role");
        Assertions.assertTrue(request.isUserInRole("ROLE_SUPERUSER"),
                "Principal should have SUPERUSER role");
        Assertions.assertFalse(request.isUserInRole("ADMIN"),
                "Principal should not have ADMIN role");
        Assertions.assertFalse(request.isUserInRole("ROLE_ADMIN"),
                "Principal should not have ADMIN role");
    }

    @Test
    @WithMockUser(username = "john", roles = { "DEV", "PO" })
    void withMockUser_landOnProtectedHomeView() {
        Assertions.assertInstanceOf(ProtectedView.class, getCurrentView(),
                "Logged user should land to protected home view");
    }

    @Test
    @WithAnonymousUser
    void withAnonymousUser_redirectToLogin() {
        Assertions.assertInstanceOf(LoginView.class, getCurrentView(),
                "Anonymous user should be redirect to login view");
    }

    @Test
    void extendingBaseClass_runTest_menuAccessControlAvailable() {
        Class<? extends MenuAccessControl> menuAccessControlClass = SecurityTestConfig
                .springMenuAccessControlClass();
        Assumptions.assumeTrue(menuAccessControlClass != null,
                "SpringMenuAccessControl class not available");
        MenuAccessControl menuAccessControl = VaadinService.getCurrent()
                .getInstantiator().getMenuAccessControl();
        Assertions.assertNotNull(menuAccessControl,
                "Expecting MenuAccessControl to be available");
        Assertions.assertInstanceOf(menuAccessControlClass, menuAccessControl,
                "Expecting menu access control to be "
                        + menuAccessControlClass.getName() + " but was "
                        + menuAccessControl.getClass().getName());

    }

}
