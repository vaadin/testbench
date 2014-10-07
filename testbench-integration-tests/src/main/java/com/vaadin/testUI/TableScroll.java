package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableScroll extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/TableScroll/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TableScroll.class)
    public static class Servlet extends VaadinServlet {
    }

    Table table;
    public static final int COLUMNS = 5;
    public static final int ROWS = 100;

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("200px");
        layout.setHeight("200px");

        Table table = new Table();
        table.setWidth("200px");
        table.setHeight("200px");
        initProperties(table);
        fillTable(table);
        layout.addComponent(table);
        addComponent(layout);
    }

    @Override
    protected String getTestDescription() {
        return "Test table element scroll and scrollLeft";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13826;
    }

    // set up the properties (columns)
    private void initProperties(Table table) {
        for (int i = 0; i < COLUMNS; i++) {
            table.addContainerProperty("property" + i, String.class,
                    "some value");
        }
    }

    // fill the table with some random data
    private void fillTable(Table table) {
        initProperties(table);
        for (int i = 0; i < ROWS; i++) {
            String[] line = new String[COLUMNS];
            for (int j = 0; j < COLUMNS; j++) {
                line[j] = "col=" + j + " row=" + i;
            }
            table.addItem(line, null);
        }
    }
}
