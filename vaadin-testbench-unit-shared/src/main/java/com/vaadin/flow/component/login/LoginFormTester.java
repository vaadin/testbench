/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.login;

import com.vaadin.testbench.unit.Tests;

/**
 * Tester for LoginForm components.
 *
 * @param <T>
 *            component type
 */
@Tests(LoginForm.class)
public class LoginFormTester<T extends LoginForm>
        extends AbstractLoginTester<T> {

    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public LoginFormTester(T component) {
        super(component);
    }
}
