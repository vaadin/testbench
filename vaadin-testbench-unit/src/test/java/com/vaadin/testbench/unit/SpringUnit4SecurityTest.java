/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.security.Principal;

import com.testapp.security.LoginView;
import com.testapp.security.ProtectedView;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.flow.server.VaadinRequest;

@ContextConfiguration(classes = SecurityTestConfig.class)
@ViewPackages(packages = "com.testapp.security")
public class SpringUnit4SecurityTest extends SpringUIUnit4Test {

    @Test
    @WithMockUser(username = "john", roles = { "DEV", "PO" })
    public void withMockUser_loggedUser_authenticationInformationAvailableOnRequest() {
        VaadinRequest request = VaadinRequest.getCurrent();
        Principal principal = request.getUserPrincipal();
        Assert.assertNotNull(
                "Principal should be provided by Spring Security, but was not found",
                principal);
        Assert.assertEquals("john", principal.getName());

        Assert.assertTrue("Principal should have DEV role",
                request.isUserInRole("DEV"));
        Assert.assertTrue("Principal should have PO role",
                request.isUserInRole("PO"));
        Assert.assertFalse("Principal should not have CEO role",
                request.isUserInRole("CEO"));

        Assert.assertTrue("Principal should have DEV role",
                request.isUserInRole("ROLE_DEV"));
        Assert.assertTrue("Principal should have PO role",
                request.isUserInRole("ROLE_PO"));
        Assert.assertFalse("Principal should not have CEO role",
                request.isUserInRole("CEO"));
    }

    @Test
    public void withoutSecurity_noAuthenticationInformation() {
        Principal principal = VaadinRequest.getCurrent().getUserPrincipal();
        Assert.assertNull(
                "Principal should not be present, but got " + principal,
                principal);
    }

    @Test
    @WithAnonymousUser
    public void witAnonymousUser_noAuthenticationInformation() {
        Principal principal = VaadinRequest.getCurrent().getUserPrincipal();
        Assert.assertNull(
                "Principal should not be present, but got " + principal,
                principal);
    }

    @Test
    @WithUserDetails
    public void withUserDetails_loggedUser_authenticationInformationAvailableOnRequest() {
        VaadinRequest request = VaadinRequest.getCurrent();
        Principal principal = request.getUserPrincipal();
        Assert.assertNotNull(
                "Principal should be provided by Spring Security, but was not found",
                principal);
        Assert.assertEquals("user", principal.getName());

        Assert.assertTrue("Principal should have DEV role",
                request.isUserInRole("DEV"));
        Assert.assertTrue("Principal should have DEV role",
                request.isUserInRole("ROLE_DEV"));
        Assert.assertTrue("Principal should have SUPERUSER role",
                request.isUserInRole("SUPERUSER"));
        Assert.assertTrue("Principal should have SUPERUSER role",
                request.isUserInRole("ROLE_SUPERUSER"));
        Assert.assertFalse("Principal should not have ADMIN role",
                request.isUserInRole("ADMIN"));
        Assert.assertFalse("Principal should not have ADMIN role",
                request.isUserInRole("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(username = "john", roles = { "DEV", "PO" })
    public void withMockUser_landOnProtectedHomeView() {
        Assert.assertTrue("Logged user should land to protected home view",
                getCurrentView() instanceof ProtectedView);
    }

    @Test
    @WithAnonymousUser
    public void withAnonymousUser_redirectToLogin() {
        Assert.assertTrue("Anonymous user should be redirect to login view",
                getCurrentView() instanceof LoginView);
    }

}
