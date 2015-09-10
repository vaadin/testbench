package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.LayoutEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowUI extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/WindowUI/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = WindowUI.class)
    public static class Servlet extends VaadinServlet {
    }

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
