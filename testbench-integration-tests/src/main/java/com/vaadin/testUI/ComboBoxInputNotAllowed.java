package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxInputNotAllowed extends AbstractTestUI {

    public static final String SELECTED_ITEM = "item 2";
    public static final List<String> ITEMS = new ArrayList<String>();
    static {
        ITEMS.add("item 1");
        ITEMS.add(SELECTED_ITEM);
        ITEMS.add("item 3");
    }

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox comboBox = new ComboBox("", ITEMS);
        comboBox.setTextInputAllowed(false);
        addComponent(comboBox);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBoxElement.selectByText(String) selects only first item when setTextInputAllowed set to false ";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14486;
    }

}
