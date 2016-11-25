package com.vaadin.testUI;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class ParameterizedUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setContent(new Label("Hello " + request.getParameter("name")));
    }

}
