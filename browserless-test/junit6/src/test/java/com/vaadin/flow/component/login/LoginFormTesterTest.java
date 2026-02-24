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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
public class LoginFormTesterTest extends BrowserlessTest {

    LoginFormView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(LoginFormView.class);
        view = navigate(LoginFormView.class);
    }

    @Test
    void login_generatesLoginEvent() {
        test(view.login).login("user", "pwd");
        Assertions.assertEquals(1, $(Span.class).from(view).all().size());
        Span message = $(Span.class).from(view).withId("m1").first();
        Assertions.assertEquals(view.generateLoginMessage("user", "pwd"),
                message.getText());
    }

    @Test
    void login_disablesLoginComponent() {
        test(view.login).login("admin", "adm");

        Assertions.assertFalse(test(view.login).isUsable(),
                "Login should be disabled after a login event.");

        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.login).login("us", "er"),
                "Disabled login should not accept login event");
    }

    @Test
    void forgotPassword_generatesEvent() {
        test(view.login).forgotPassword();

        Assertions.assertEquals(1, $(Span.class).from(view).all().size());
        Span message = $(Span.class).from(view).withId("m1").first();
        Assertions.assertEquals("forgot", message.getText());
    }

    @Test
    void forgotButtonHidden_usingForgotThrows() {
        view.login.setForgotPasswordButtonVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> test(view.login).forgotPassword(),
                "Hidden forgot password button should not be usable.");
    }

}
