package com.vaadin.testUI;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxInputNotAllowed extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/ComboBoxInputNotAllowed/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ComboBoxInputNotAllowed.class)
    public static class Servlet extends VaadinServlet {
    }

    public static final String SELECTED_ITEM = "item 2";
    public static final List<String> ITEMS = new ArrayList<String>();
    static {
        ITEMS.add("item 1");
        ITEMS.add(SELECTED_ITEM);
        ITEMS.add("item 3");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        ComboBox comboBox = new ComboBox("", ITEMS);
        comboBox.setTextInputAllowed(false);
        addComponent(comboBox);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "ComboBoxElement.selectByText(String) selects only first item when setTextInputAllowed set to false ";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 14486;
    }

}
