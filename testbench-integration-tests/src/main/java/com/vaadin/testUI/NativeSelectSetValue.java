package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

public class NativeSelectSetValue extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/NativeSelectSetValue/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = NativeSelectSetValue.class)
    public static class Servlet extends VaadinServlet {
    }

    private int counter = 0;
    Label lblCounter = new Label("0");

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect select = new NativeSelect();
        select.addItem("item 1");
        select.addItem("item 2");
        select.addItem("item 3");
        select.setValue("item 1");
        lblCounter.setId("counter");

        select.addValueChangeListener(new ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                counter++;
                lblCounter.setValue("" + counter);

            }
        });
        addComponent(select);
        addComponent(lblCounter);
    }

    @Override
    protected String getTestDescription() {
        return "Native select element setValue method should change value and triggers change event";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13365;
    }

}
