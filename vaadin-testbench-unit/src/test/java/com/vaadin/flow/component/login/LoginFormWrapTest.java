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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.TestBenchUnit;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class LoginFormWrapTest extends TestBenchUnit {

    LoginFormView view;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(LoginFormView.class);
        view = navigate(LoginFormView.class);
    }

    @Test
    void login_generatesLoginEvent() {
        wrap(view.login).login("user", "pwd");
        Assertions.assertEquals(1, $(Span.class).from(view).all().size());
        Span message = $(Span.class).from(view).withId("m1").first()
                .getComponent();
        Assertions.assertEquals(view.generateLoginMessage("user", "pwd"),
                message.getText());
    }

    @Test
    void login_disablesLoginComponent() {
        wrap(view.login).login("admin", "adm");

        Assertions.assertFalse(wrap(view.login).isUsable(),
                "Login should be disabled after a login event.");

        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.login).login("us", "er"),
                "Disabled login should not accept login event");
    }

    @Test
    void forgotPassword_generatesEvent() {
        wrap(view.login).forgotPassword();

        Assertions.assertEquals(1, $(Span.class).from(view).all().size());
        Span message = $(Span.class).from(view).withId("m1").first()
                .getComponent();
        Assertions.assertEquals("forgot", message.getText());
    }

    @Test
    void forgotButtonHidden_usingForgotThrows() {
        view.login.setForgotPasswordButtonVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> wrap(view.login).forgotPassword(),
                "Hidden forgot password button should not be usable.");
    }

}
