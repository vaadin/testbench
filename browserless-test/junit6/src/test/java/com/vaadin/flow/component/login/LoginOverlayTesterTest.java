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
public class LoginOverlayTesterTest extends BrowserlessTest {

    LoginOverlayView view;
    LoginOverlayTester<LoginOverlay> login_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(LoginOverlayView.class);
        view = navigate(LoginOverlayView.class);
        login_ = test(view.login);
    }

    @Test
    void overlayClosed_throwsException() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> login_.login("user", "user"));
    }

    @Test
    void login_generatesLoginEvent() {
        login_.openOverlay();

        login_.login("user", "pwd");
        Assertions.assertEquals(1, $(Span.class).from(view).all().size());
        Span message = $(Span.class).from(view).withId("m1").first();
        Assertions.assertEquals(view.generateLoginMessage("user", "pwd"),
                message.getText());
    }

    @Test
    void login_disablesLoginComponent() {
        login_.openOverlay();
        login_.login("admin", "adm");

        Assertions.assertFalse(login_.isUsable(),
                "Login should be disabled after a login event.");

        Assertions.assertThrows(IllegalStateException.class,
                () -> login_.login("us", "er"),
                "Disabled login should not accept login event");
    }

    @Test
    void forgotPassword_generatesEvent() {
        login_.openOverlay();
        login_.forgotPassword();

        Assertions.assertEquals(1, $(Span.class).from(view).all().size());
        Span message = $(Span.class).from(view).withId("m1").first();
        Assertions.assertEquals("forgot", message.getText());
    }

    @Test
    void forgotButtonHidden_usingForgotThrows() {
        login_.openOverlay();
        view.login.setForgotPasswordButtonVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> login_.forgotPassword(),
                "Hidden forgot password button should not be usable.");
    }

}
