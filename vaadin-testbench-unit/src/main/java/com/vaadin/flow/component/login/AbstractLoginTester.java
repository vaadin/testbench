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

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.testbench.unit.ComponentTester;

/**
 * Class with common functions for Login components LoginForm and LoginOverlay.
 *
 * @param <T>
 *            component type
 */
public class AbstractLoginTester<T extends AbstractLogin>
        extends ComponentTester<T> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public AbstractLoginTester(T component) {
        super(component);
    }

    /**
     * Send login credentials through the login.
     *
     * @param userName
     *            user to login
     * @param password
     *            password for user
     */
    public void login(String userName, String password) {
        ensureComponentIsUsable();
        ComponentUtil.fireEvent(getComponent(), new AbstractLogin.LoginEvent(
                getComponent(), true, userName, password));
    }

    /**
     * Simulate click on forgot password button.
     *
     * @throws IllegalStateException
     *             when forgot password button is hidden
     */
    public void forgotPassword() {
        ensureComponentIsUsable();
        if (!getComponent().isForgotPasswordButtonVisible()) {
            throw new IllegalStateException(
                    "Forgot password button is not visible");
        }
        ComponentUtil.fireEvent(getComponent(),
                new AbstractLogin.ForgotPasswordEvent(getComponent(), true));
    }
}
