package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxGetSuggestions extends AbstractTestUI {
    @WebServlet(value = { "/VAADIN/*", "/ComboBoxGetSuggestions/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ComboBoxGetSuggestions.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox cb = new ComboBox();
        cb.setNullSelectionItemId("item1");
        cb.setInvalidAllowed(false);
        cb.setNewItemsAllowed(false);
        for (int i = 1; i < 100; i++) {
            cb.addItem("item" + i);
        }
        addComponent(cb);
    }

    @Override
    protected String getTestDescription() {
        return "Test getSuggestions() method of ComboBoxElement returns correct values";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14372;
    }

}
