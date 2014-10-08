package com.vaadin.testUI;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class TableComponentsInside extends AbstractTestUI {
    @WebServlet(value = { "/VAADIN/*", "/TableComponentsInside/*" }, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = TableComponentsInside.class)
    public static class Servlet extends VaadinServlet {
    }

    private final Table table = new Table();

    @Override
    protected void setup(VaadinRequest request) {
        /*
         * Define the names and data types of columns. The "default value"
         * parameter is meaningless here.
         */
        table.addContainerProperty("Sum", Label.class, null);
        table.addContainerProperty("Is Transferred", CheckBox.class, null);
        table.addContainerProperty("Comments", TextField.class, null);
        table.addContainerProperty("Details", Button.class, null);

        /* Add a few items in the table. */
        for (int i = 0; i < 10; i++) {
            // Create the fields for the current table row
            Label sumField = new Label("100");
            CheckBox transferredField = new CheckBox("is transferred");
            if (i == 1) {
                transferredField.setValue(true);
            }
            // Multiline text field. This required modifying the
            // height of the table row.
            TextField commentsField = new TextField();
            commentsField.setValue("comment" + i);
            commentsField.setId("tf" + i);
            // The Table item identifier for the row.
            Integer itemId = new Integer(i);

            // Create a button and handle its click. A Button does not
            // know the item it is contained in, so we have to store the
            // item ID as user-defined data.
            Button detailsField = new Button("show details");
            detailsField.setId("btn" + i);
            detailsField.setData(itemId);
            detailsField.addClickListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {
                    Integer iid = (Integer) event.getButton().getData();
                    Notification.show("Link " + iid.intValue() + " clicked.",
                            Type.WARNING_MESSAGE);
                }
            });
            // Create the table row.
            table.addItem(new Object[] { sumField, transferredField,
                    commentsField, detailsField }, itemId);
        }
        addComponent(table);
    }

    @Override
    protected String getTestDescription() {
        return "TableRowElement.getElementInCell() should return the same WebElement "
                + " as searching by ElementQuery $(TestBenchElement.class).";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13770;
    }

}
