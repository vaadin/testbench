package com.vaadin.testUI;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Button;

public class ElementQueryUI extends AbstractTestUI {


    @Override
    protected void setup(VaadinRequest request) {
        for (int i = 0; i < 10; i++) {
            addComponent(new Button("Button " + i));
        }

        getLayout().setSizeUndefined();
    }

    @Override
    protected String getTestDescription() {
        return "A generic test for ElementQuery";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
