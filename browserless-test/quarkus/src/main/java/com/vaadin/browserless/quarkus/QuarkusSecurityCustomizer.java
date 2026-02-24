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

import io.quarkus.security.identity.CurrentIdentityAssociation;
import io.quarkus.security.identity.SecurityIdentity;

import com.vaadin.browserless.internal.MockRequestCustomizer;
import com.vaadin.browserless.mocks.MockRequest;

/**
 * Configures mock request with authentication details from Quarkus Security.
 *
 * For internal use only.
 */
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
