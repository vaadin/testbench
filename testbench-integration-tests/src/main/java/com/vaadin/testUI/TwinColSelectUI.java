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
import com.vaadin.ui.TwinColSelect;

public class TwinColSelectUI extends AbstractTestUI {

    @WebServlet(value = { "/TwinColSelectUI/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TwinColSelectUI.class)
    public static class Servlet extends VaadinServlet {
    }

    Label multiCounterLbl = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        TwinColSelect twinColSelect = new TwinColSelect();
        multiCounterLbl.setValue("0");
        twinColSelect.addItem("item1");
        twinColSelect.addItem("item2");
        twinColSelect.addItem("item3");
        twinColSelect.setValue(Collections.singletonList("item1"));
        twinColSelect.addValueChangeListener(
                new CounterListener(multiCounterLbl, 0));

        addComponent(twinColSelect);
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
