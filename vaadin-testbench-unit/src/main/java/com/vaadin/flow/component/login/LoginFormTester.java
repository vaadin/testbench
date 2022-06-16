/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
