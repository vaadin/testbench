package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxGetSuggestions extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> cb = new ComboBox<String>();
        cb.setEmptySelectionAllowed(false);
        List<String> options = new ArrayList<String>();

        for (int i = 1; i < 100; i++) {
            options.add("item" + i);
        }
        cb.setDataSource(new ListDataSource<String>(options));

        addComponent(cb);
    }

    @Override
    protected String getTestDescription() {
        return "Test getSuggestions() method of ComboBoxElement returns correct values";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14372;
    }

}
