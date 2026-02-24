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
package com.vaadin.flow.component.login;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.flow.component.ComponentUtil;

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
