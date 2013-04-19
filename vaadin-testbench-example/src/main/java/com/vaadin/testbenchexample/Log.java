package com.vaadin.testbenchexample;

import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

class Log extends Table implements Handler {

    private int row = 1;

    private static final Action CLEAR = new Action("Clear");
    private static final Action ADD_COMMENT = new Action("Add Comment");
    private static final Action[] ACTIONS = { CLEAR, ADD_COMMENT };

    public Log() {
        setWidth("200px");
        setPageLength(7);
        setColumnHeaderMode(COLUMN_HEADER_MODE_HIDDEN);
        addContainerProperty("text", String.class, null);
        setColumnAlignments(new String[] { ALIGN_RIGHT });
        addActionHandler(this);
        setColumnExpandRatio("text", 1);
    }

    public void addRow(String rowStr) {
        Integer itemId = new Integer(row++);
        Item i = addItem(itemId);
        i.getItemProperty("text").setValue(rowStr);
        setCurrentPageFirstItemId(itemId);
    }

    @Override
    public Action[] getActions(Object target, Object sender) {
        return ACTIONS;
    }

    @Override
    public void handleAction(Action action, Object sender, Object target) {
        if (action == CLEAR) {
            removeAllItems();
            row = 1;
        } else if (action == ADD_COMMENT) {
            final Window window = new Window("Add comment");
            window.getContent().setSizeUndefined();
            final TextField textField = new TextField();
            window.addComponent(textField);
            textField.focus();
            Button button = new Button("Add");
            button.setDescription("Clicking this button will add a comment row to log.");
            button.addListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    Object comment = textField.getValue();
                    if (comment != null) {
                        addRow("Comment: " + comment);
                    }
                    getWindow().removeWindow(window);
                }
            });
            window.addComponent(button);
            window.setClosable(true);
            window.setModal(true);
            getWindow().addWindow(window);
        }
    }

}