package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.selection.MultiSelectionEvent;
import com.vaadin.event.selection.MultiSelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;

public class ListSelectOptionClick extends AbstractTestUI {

    Label counterLbl = new Label();
    Label multiCounterLbl = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        ListSelect select = new ListSelect();
        counterLbl.setValue("0");
        List<String> options = new ArrayList<String>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        select.setDataSource(new ListDataSource<String>(options));
        select.select("item1");
        select.addSelectionListener(new CounterListener(counterLbl, 0));

        addComponent(select);
        counterLbl.setId("counterLbl");
        addComponent(counterLbl);

        ListSelect multiSelect = new ListSelect();
        multiCounterLbl.setValue("0");
        options = new ArrayList<String>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        multiSelect.setDataSource(new ListDataSource<String>(options));
        multiSelect.select("item1");
        multiSelect
                .addSelectionListener(new CounterListener(multiCounterLbl, 0));

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

    private class CounterListener implements MultiSelectionListener<String> {
        int counter = 0;
        private Label counterLbl;

        public CounterListener(Label counterLbl, int i) {
            this.counterLbl = counterLbl;
            counter = i;
        }

        @Override
        public void accept(MultiSelectionEvent<String> event) {
            counter++;
            counterLbl.setValue("" + counter + ": " + event.getValue());
        }
    }
}
