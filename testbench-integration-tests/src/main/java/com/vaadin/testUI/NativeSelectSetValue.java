package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.ListDataProvider;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

public class NativeSelectSetValue extends AbstractTestUI {


    private int counter = 0;
    Label lblCounter = new Label("0");

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect select = new NativeSelect();
        List<String> options = new ArrayList<String>();
        options.add("item 1");
        options.add("item 2");
        options.add("item 3");
        select.setDataProvider(new ListDataProvider<String>(options));
        select.setValue("item 1");
        lblCounter.setId("counter");

        select.addSelectionListener(new EventCounter());
        addComponent(select);
        addComponent(lblCounter);
    }

    private class EventCounter implements SingleSelectionListener<String> {
        private int counter = 0;

        @Override
        public void accept(SingleSelectionEvent<String> event) {
            counter++;
            lblCounter.setValue("" + counter);
        }

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
