package com.vaadin.testUI;

import java.util.Collections;

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

    @WebServlet(value = { "/VAADIN/*",
            "/ListSelectOptionClick/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ListSelectOptionClick.class)
    public static class Servlet extends VaadinServlet {
    }

    Label counterLbl = new Label();
    Label multiCounterLbl = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        ListSelect select = new ListSelect();
        counterLbl.setValue("0");
        select.addItem("item1");
        select.addItem("item2");
        select.addItem("item3");
        select.setValue("item1");
        select.addValueChangeListener(new CounterListener(counterLbl, 0));
        select.setNullSelectionAllowed(false);

        addComponent(select);
        counterLbl.setId("counterLbl");
        addComponent(counterLbl);

        ListSelect multiSelect = new ListSelect();
        multiSelect.setMultiSelect(true);
        multiCounterLbl.setValue("0");
        multiSelect.addItem("item1");
        multiSelect.addItem("item2");
        multiSelect.addItem("item3");
        multiSelect.setValue(Collections.singletonList("item1"));
        multiSelect.addValueChangeListener(
                new CounterListener(multiCounterLbl, 0));
        multiSelect.setNullSelectionAllowed(false);

        addComponent(multiSelect);
        multiCounterLbl.setId("multiCounterLbl");
        addComponent(multiCounterLbl);

    }

    @Override
    protected String getTestDescription() {
        return "Test that user can pick option from ListSelectElement by call click() method";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    private static class CounterListener implements ValueChangeListener {
        int counter = 0;
        private Label counterLbl;

        public CounterListener(Label counterLbl, int i) {
            this.counterLbl = counterLbl;
            counter = i;
        }

        public void valueChange(ValueChangeEvent event) {
            counter++;
            counterLbl.setValue(
                    "" + counter + ": " + event.getProperty().getValue());
        }
    }
}
