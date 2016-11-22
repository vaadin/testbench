package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.ListDataProvider;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TwinColSelect;

public class TwinColSelectUI extends AbstractTestUI {

    Label multiCounterLbl = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        TwinColSelect<String> twinColSelect = new TwinColSelect<String>();
        multiCounterLbl.setValue("0");
        List<String> options = new ArrayList<String>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        twinColSelect.setDataProvider(new ListDataProvider<String>(options));
        twinColSelect.select("item1");
        twinColSelect.addSelectionListener(new CounterListener(0));

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

    private class CounterListener implements MultiSelectionListener<String> {
        int counter = 0;

        public CounterListener(int i) {
            counter = i;
        }

        @Override
        public void accept(MultiSelectionEvent<String> event) {
            counter++;
            multiCounterLbl.setValue("" + counter + ": " + event.getValue());
        }
    }
}
