package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.CheckBoxGroup;

public class CheckBoxGroupSetValue extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CheckBoxGroup<String> group = new CheckBoxGroup<>();
        List<String> options = new ArrayList<>();
        options.add("item1");
        options.add("item2");
        options.add("item3");
        group.setDataProvider(new ListDataProvider<>(options));
        addComponent(group);
    }

    @Override
    protected String getTestDescription() {
        return "Test CheckBoxGroup element setValue() and SelectByText()";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
