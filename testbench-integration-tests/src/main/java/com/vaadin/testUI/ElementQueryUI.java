package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Button;

public class ElementQueryUI extends AbstractTestUI {

    @WebServlet(value = "/ElementQueryUI/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ElementQueryUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        for (int i = 0; i < 10; i++) {
            addComponent(new Button("Button " + i));
        }

    }

    @Override
    protected String getTestDescription() {
        return "A generic test for ElementQuery";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
