package com.vaadin.testUI;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;

public class DateFieldPopupSetValue extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/DateFieldPopupSetValue/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DateFieldPopupSetValue.class)
    public static class Servlet extends VaadinServlet {
    }

    public Calendar calendar = Calendar.getInstance();

    public static Date initialDate = new Date(2015, 3, 12);
    public static Date changedDate = new Date(2015, 5, 11);
    Label counterLbl = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        counterLbl.setId("counter");
        DateField df = new DateField();
        df.setDateFormat("MM/dd/yy");
        df.setValue(initialDate);
        df.addValueChangeListener(new EventCounter());
        addComponent(df);
        addComponent(counterLbl);
    }

    @Override
    protected String getTestDescription() {
        return "Test popupDateFieldElement getValue/setValue";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15092;
    }

    private class EventCounter implements ValueChangeListener {
        private int counter = 0;

        public void valueChange(ValueChangeEvent event) {
            counter++;
            counterLbl.setValue("" + counter);
        }

    }
}
