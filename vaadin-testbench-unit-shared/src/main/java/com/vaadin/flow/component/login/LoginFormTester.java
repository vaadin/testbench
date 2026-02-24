/**
 * Copyright (C) 2000-2026 Vaadin Ltd
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
 
  * @deprecated Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.
  */
@Tests(LoginForm.class)
@Deprecated(forRemoval = true, since = "10.1")
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
