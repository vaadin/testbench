/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.quarkus;

import java.util.Set;

import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class QuarkusSecurityCustomizerTest {

    @Test
    void hasRole_matchExactRole() {
        QuarkusSecurityIdentity securityIdentity = QuarkusSecurityIdentity
                .builder().addRoles(Set.of("USER", "MANAGER"))
                .setPrincipal(new QuarkusPrincipal("john")).setAnonymous(false)
                .build();
        Assertions.assertTrue(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, "USER"),
                "User has USER role, but check returned false");
        Assertions.assertTrue(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, "MANAGER"),
                "User has MANAGER role, but check returned false");
        Assertions.assertFalse(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, "ADMIN"),
                "User has not ADMIN role, but check returned true");

        Assertions.assertFalse(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, null),
                "User has not ADMIN role, but check returned true");
    }

    /*
     * Tests @RoleAllowed("**")
     */
    @Test
    void hasRole_allRoles_authenticatedUser_getsTrue() {
        QuarkusSecurityIdentity securityIdentity = QuarkusSecurityIdentity
                .builder().addRoles(Set.of("USER", "MANAGER"))
                .setPrincipal(new QuarkusPrincipal("john")).setAnonymous(false)
                .build();
        Assertions.assertTrue(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, "**"),
                "User has roles, but check returned false");
    }

    @Test
    void hasRole_anonymousUser_getsFalse() {
        QuarkusSecurityIdentity securityIdentity = QuarkusSecurityIdentity
                .builder().addRoles(Set.of("ANONYMOUS")).setAnonymous(true)
                .build();
        Assertions.assertFalse(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, "**"));
        Assertions.assertFalse(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, "ADMIN"));
        Assertions.assertFalse(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, "USER"));
        Assertions.assertFalse(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, ""));
        Assertions.assertFalse(
                QuarkusSecurityCustomizer.hasRole(securityIdentity, null));
    }

}
