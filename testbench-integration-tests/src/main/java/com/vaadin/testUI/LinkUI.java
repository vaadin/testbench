package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Link;

public class LinkUI extends AbstractTestUI {

    @WebServlet(value = { "/LinkUI/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = LinkUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        Link link = new Link("server root", new ExternalResource("/"));
        addComponent(link);
    }

    @Override
    protected String getTestDescription() {
        return "Clicking on a link should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15357;
    }
}
