package com.vaadin.testUI;

import static java.util.Calendar.DAY_OF_MONTH;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Calendar;

public class CalendarUI extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/CalendarUI/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = CalendarUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        final Calendar calendar = new Calendar();
        calendar.setWidth("100%");

        Button monthView = new Button("Month view");
        monthView.setId("month-view");
        monthView.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                java.util.Calendar javaCalendar = java.util.Calendar
                        .getInstance();
                javaCalendar.set(DAY_OF_MONTH, 1);
                calendar.setStartDate(javaCalendar.getTime());
                javaCalendar.set(DAY_OF_MONTH,
                        javaCalendar.getActualMaximum(DAY_OF_MONTH));
                calendar.setEndDate(javaCalendar.getTime());
            }
        });

        addComponents(monthView, calendar);
    }

    @Override
    protected String getTestDescription() {
        return "UI used to validate Calendar element API";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}
