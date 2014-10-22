package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.OptionGroup;

public class OptionGroupSetValue extends AbstractTestUI {
    @WebServlet(value = { "/VAADIN/*", "/OptionGroupSetValue/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = OptionGroupSetValue.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        OptionGroup group = new OptionGroup();
        group.addItem("item1");
        group.addItem("item2");
        group.addItem("item3");
        addComponent(group);
    }

    @Override
    protected String getTestDescription() {
        return "Test option group element setValue() and SelectByText()";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14918;
    }

}
