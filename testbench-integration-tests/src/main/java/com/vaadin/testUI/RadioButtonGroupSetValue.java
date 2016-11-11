package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.data.ListDataSource;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.RadioButtonGroup;

public class RadioButtonGroupSetValue extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        RadioButtonGroup<String> group = new RadioButtonGroup<String>();
        List<String> options = new ArrayList<String>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        group.setDataSource(new ListDataSource<String>(options));
        addComponent(group);
    }

    @Override
    protected String getTestDescription() {
        return "Test RadioButtonGroup element setValue() and SelectByText()";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
