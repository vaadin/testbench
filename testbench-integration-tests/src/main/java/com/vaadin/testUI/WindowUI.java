package com.vaadin.testUI;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Window window = new Window();

        window.setCaption("Some caption");
        window.center();

        window.setWidth("200px");
        window.setHeight("200px");

        window.setContent(new Label("Hello world"));
        addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Test UI for Window element API";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}
