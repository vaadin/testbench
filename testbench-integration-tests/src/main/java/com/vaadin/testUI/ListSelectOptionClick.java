package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;

public class ListSelectOptionClick extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/ListSelectOptionClick/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ListSelectOptionClick.class)
    public static class Servlet extends VaadinServlet {
    }

    Label counterLbl = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        ListSelect select = new ListSelect();
        counterLbl.setValue("0");
        select.addItem("item1");
        select.addItem("item2");
        select.addItem("item3");
        select.setValue("item1");
        select.addValueChangeListener(new CounterListener(0));
        select.setNullSelectionAllowed(false);

        addComponent(select);
        counterLbl.setId("counterLbl");
        addComponent(counterLbl);
    }

    @Override
    protected String getTestDescription() {
        return "Test that user can pick option from ListSelectElement by call click() method";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    private class CounterListener implements ValueChangeListener {
        int counter = 0;

        public CounterListener(int i) {
            counter = i;
        }

        public void valueChange(ValueChangeEvent event) {
            counter++;
            counterLbl.setValue("" + counter);
        }
    }
}
