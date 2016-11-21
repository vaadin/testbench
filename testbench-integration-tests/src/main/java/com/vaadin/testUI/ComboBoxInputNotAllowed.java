package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxInputNotAllowed extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*",
            "/ComboBoxInputNotAllowed/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ComboBoxInputNotAllowed.class)
    public static class Servlet extends VaadinServlet {
    }

    public static final String ITEM_ON_FIRST_PAGE = "item 2";
    public static final String ITEM_ON_SECOND_PAGE = "item 19";
    public static final String ITEM_ON_LAST_PAGE = "item 30";
    public static final String ITEM_LAST_WITH_PARENTHESIS = "item (last)";

    public static final List<String> ITEMS = new ArrayList<String>();
    static {
        for (int i = 1; i <= 30; i++) {
            ITEMS.add("item " + i);
        }
        ITEMS.add(ITEM_LAST_WITH_PARENTHESIS);
    }

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox comboBox = new ComboBox("", ITEMS);
        comboBox.setTextInputAllowed(false);
        comboBox.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                addComponent(new Label(
                        "Value is now: " + event.getProperty().getValue()));
            }
        });
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
