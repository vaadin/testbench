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
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
public class LoginOverlayTesterTest extends UIUnitTest {

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
        Span message = $(Span.class).from(view).withId("m1").first()
                .getComponent();
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
        Span message = $(Span.class).from(view).withId("m1").first()
                .getComponent();
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
