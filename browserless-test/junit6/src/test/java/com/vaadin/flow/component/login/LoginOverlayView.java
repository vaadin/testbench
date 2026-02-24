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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "login-view", registerAtStartup = false)
public class LoginOverlayView extends Component implements HasComponents {
    LoginOverlay login;
    Div messages;

    public LoginOverlayView() {
        this.login = new LoginOverlay();
        messages = new Div(new Text("Messages"));
        login.addLoginListener(loginEvent -> addMessage(generateLoginMessage(
                loginEvent.getUsername(), loginEvent.getPassword())));
        login.addForgotPasswordListener(
                forgotPasswordEvent -> addMessage("forgot"));

        Button open = new Button("Login", event -> login.setOpened(true));
        add(open, login, messages);
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
