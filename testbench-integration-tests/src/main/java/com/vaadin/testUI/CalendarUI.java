package com.vaadin.testUI;

import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.HorizontalLayout;

public class CalendarUI extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/CalendarUI/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = CalendarUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(new Locale("en", "US"));
        final Calendar calendar = new Calendar();
        calendar.setWidth("100%");

        Button monthView = new Button("Month view");
        monthView.setId("month-view");
        monthView.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                java.util.Calendar juCalendar = java.util.Calendar
                        .getInstance();
                juCalendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
                calendar.setStartDate(juCalendar.getTime());
                juCalendar.set(java.util.Calendar.DAY_OF_MONTH, juCalendar
                        .getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
                calendar.setEndDate(juCalendar.getTime());
            }
        });

        Button weekView = new Button("Week view");
        weekView.setId("week-view");
        weekView.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                java.util.Calendar juCalendar = java.util.Calendar
                        .getInstance();
                juCalendar.set(java.util.Calendar.DAY_OF_WEEK,
                        java.util.Calendar.MONDAY);
                calendar.setStartDate(juCalendar.getTime());
                juCalendar.set(java.util.Calendar.DAY_OF_WEEK,
                        java.util.Calendar.SUNDAY);
                System.out.println(juCalendar.getTime());
                calendar.setEndDate(juCalendar.getTime());
            }
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(monthView,
                weekView);

        addComponents(buttonLayout, calendar);
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
