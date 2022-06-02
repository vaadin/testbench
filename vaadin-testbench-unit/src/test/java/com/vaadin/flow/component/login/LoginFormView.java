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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "login-view", registerAtStartup = false)
public class LoginFormView extends Component implements HasComponents {
    LoginForm login;
    Div messages;

    public LoginFormView() {
        this.login = new LoginForm();
        messages = new Div(new Text("Messages"));
        login.addLoginListener(loginEvent -> addMessage(generateLoginMessage(
                loginEvent.getUsername(), loginEvent.getPassword())));
        login.addForgotPasswordListener(
                forgotPasswordEvent -> addMessage("forgot"));
        add(login, messages);
    }

    protected static String generateLoginMessage(String user, String password) {
        return "login: " + user + " :: " + password;
    }

    protected void addMessage(String message) {
        Span messageElement = new Span(message);
        messageElement.setId("m" + messages.getChildren().count());
        messages.add(messageElement);
    }
}
