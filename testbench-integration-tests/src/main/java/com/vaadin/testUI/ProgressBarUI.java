package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.ProgressBar;

public class ProgressBarUI extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/ProgressBarUI/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ProgressBarUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void setup(VaadinRequest request) {
        ProgressBar complete = new ProgressBar();
        complete.setId("complete");
        complete.setValue(1f);

        ProgressBar halfComplete = new ProgressBar();
        halfComplete.setId("halfComplete");
        halfComplete.setValue(0.5f);

        ProgressBar notStarted = new ProgressBar();
        notStarted.setId("notStarted");
        notStarted.setValue(0f);

        addComponents(complete, halfComplete, notStarted);
    }

    @Override
    protected String getTestDescription() {
        return "Test UI for ProgressBar element API";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}
