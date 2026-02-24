/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.browserless.mocks

import java.security.Principal

data class MockPrincipal(private val name: String, val allowedRoles: List<String> = listOf()) : Principal {
    override fun getName(): String = name

    fun isUserInRole(role: String): Boolean = allowedRoles.contains(role)
}
