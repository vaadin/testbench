/*
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
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

import com.vaadin.flow.server.VaadinRequest;

@ContextConfiguration(classes = SpringUnitSecurityTest.TestConfig.class)
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

    // Empty configuration class used only to be able to bootstrap spring
    // ApplicationContext
    @Configuration
    static class TestConfig {

        @Bean
        UserDetailsService mockUserDetailsService() {

            return new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username)
                        throws UsernameNotFoundException {
                    if ("user".equals(username)) {
                        return new User(username, UUID.randomUUID().toString(),
                                List.of(new SimpleGrantedAuthority(
                                        "ROLE_SUPERUSER"),
                                        new SimpleGrantedAuthority(
                                                "ROLE_DEV")));
                    }
                    throw new UsernameNotFoundException(
                            "User " + username + " not exists");
                }
            };
        }
    }

}
