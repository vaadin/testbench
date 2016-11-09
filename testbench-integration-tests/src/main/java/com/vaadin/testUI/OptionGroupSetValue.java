package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.RadioButtonGroup;

public class OptionGroupSetValue extends AbstractTestUI {
    @WebServlet(value = { "/VAADIN/*", "/OptionGroupSetValue/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = OptionGroupSetValue.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<String> group = new RadioButtonGroup<String>();
        List<String> options = new ArrayList<String>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        group.setDataSource(new ListDataSource<String>(options));
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
