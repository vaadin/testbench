/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit.quarkus;

import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;

import com.vaadin.testbench.unit.internal.MockRequestCustomizer;
import com.vaadin.testbench.unit.mocks.MockRequest;

/**
 * Configures mock request with authentication details from Quarkus Security.
 *
 * For internal use only.
 
  * @deprecated Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.
  */
@Deprecated(forRemoval = true, since = "10.1")
public class QuarkusSecurityCustomizer implements MockRequestCustomizer {

    @Override
    public void apply(MockRequest request) {
        SecurityIdentity current = CurrentIdentityAssociation.current();
        if (current.isAnonymous()) {
            request.setUserPrincipalInt(null);
            request.setUserInRole((principal, role) -> false);
        } else {
            request.setUserPrincipalInt(current.getPrincipal());
            request.setUserInRole((principal,
                    role) -> current.getPrincipal().equals(principal)
                            && hasRole(current, role));
        }
    }

    // This method should be removed after implementing a proper solution
    // e.g. an override of NavigationAccessControl.getRolesChecker or a custom
    // AccessAnnotationChecker, to evaluate the "**" role
    // To be done in https://github.com/vaadin/quarkus/issues/142
    static boolean hasRole(SecurityIdentity identity, String role) {
        // Same check as in Quarkus RolesAllowedCheck class
        return identity.hasRole(role)
                || ("**".equals(role) && !identity.isAnonymous());
    }
}
