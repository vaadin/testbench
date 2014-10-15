package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.TreeTable;

public class TreeTableElementExpandRow extends AbstractTestUI {

    @WebServlet(value = { "/VAADIN/*", "/TreeTableElementExpandRow/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TreeTableElementExpandRow.class)
    public static class Servlet extends VaadinServlet {

    }

    TreeTable treeTable = new TreeTable();
    public static final String TEST_VALUE = "testValue";

    @Override
    protected void setup(VaadinRequest request) {
        treeTable.setWidth("200px");
        treeTable.addContainerProperty("Name", String.class, "");
        treeTable.addItem(new Object[] { "item1" }, "item1");
        treeTable.addItem(new Object[] { "item1_1" }, "item1_1");
        treeTable.addItem(new Object[] { "item1_2" }, "item1_2");
        treeTable.setParent("item1_1", "item1");
        treeTable.setParent("item1_2", "item1");
        treeTable.addItem(new Object[] { TEST_VALUE }, TEST_VALUE);
        addComponent(treeTable);

    }

    @Override
    protected String getTestDescription() {
        return "Test TreeTableRowElement toggleExpanded() method expands/collapses the row.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13773;
    }

}
