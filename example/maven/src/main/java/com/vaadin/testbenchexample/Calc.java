package com.vaadin.testbenchexample;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * A simple calculator app using Vaadin. This app is just a demo app that can be
 * tested with TestBench. It is closely related to the official demo app, but
 * has some tuning to help testing like usage of setDebugId(String) and some
 * additional features to emphasize advanced features in TestBench.
 */
public class Calc extends Application {

    @Override
    public void init() {
        setMainWindow(new CalcWindow());
    }

    @Override
    public Window getWindow(String name) {
        // Multitab support, return new windows for each tab
        Window window = super.getWindow(name);
        if (window == null && name != null) {
            window = new CalcWindow();
            window.setName(name);
            addWindow(window);
        }
        return window;
    }

    class CalcWindow extends Window implements ClickListener {
        // All variables are automatically stored in the session.
        private Double current = 0.0;
        private double stored = 0.0;
        private char lastOperationRequested = 'C';
        private VerticalLayout topLayout = new VerticalLayout();

        // User interface components
        private final Label display = new Label("0.0");

        private final Log log = new Log();

        public CalcWindow() {
            super("Calc window");
            setContent(topLayout);
            display.setDebugId("display");

            // Create the main layout for our application (4 columns, 5 rows)
            final GridLayout layout = new GridLayout(4, 5);

            topLayout.setMargin(true);
            topLayout.setSpacing(true);
            Label title = new Label("<h1>Calculator</h1>", Label.CONTENT_XHTML);
            topLayout.addComponent(title);
            topLayout.addComponent(log);

            HorizontalLayout horizontalLayout = new HorizontalLayout();
            horizontalLayout.setSpacing(true);
            horizontalLayout.addComponent(layout);
            horizontalLayout.addComponent(log);
            topLayout.addComponent(horizontalLayout);

            // Create a result label that over all 4 columns in the first row
            layout.addComponent(display, 0, 0, 3, 0);
            layout.setComponentAlignment(display, Alignment.MIDDLE_RIGHT);
            display.setSizeUndefined();

            // The operations for the calculator in the order they appear on the
            // screen (left to right, top to bottom)
            String[] operations = new String[] { "7", "8", "9", "/", "4", "5",
                    "6", "*", "1", "2", "3", "-", "0", "=", "C", "+" };

            for (String caption : operations) {

                // Create a button and use this application for event handling
                NativeButton button = new NativeButton(caption);
                button.setHeight("30px");
                button.setWidth("40px");
                button.addListener(this);
                button.setDebugId("button_" + caption);

                // Add the button to our main layout
                layout.addComponent(button);
            }
        }

        // Event handler for button clicks. Called for all the buttons in the
        // application.
        public void buttonClick(ClickEvent event) {

            // Get the button that was clicked
            Button button = event.getButton();

            // Get the requested operation from the button caption
            char requestedOperation = button.getCaption().charAt(0);

            // Calculate the new value
            double newValue = calculate(requestedOperation);

            // Update the result label with the new value
            display.setValue(newValue);

        }

        // Calculator "business logic" implemented here to keep the example
        // minimal
        private double calculate(char requestedOperation) {
            if ('0' <= requestedOperation && requestedOperation <= '9') {
                if (current == null) {
                    current = 0.0;
                }
                current = current * 10
                        + Double.parseDouble("" + requestedOperation);
                return current;
            }

            if (current == null) {
                current = stored;
            }
            switch (lastOperationRequested) {
            case '+':
                stored += current;
                break;
            case '-':
                stored -= current;
                break;
            case '/':
                stored /= current;
                break;
            case '*':
                stored *= current;
                break;
            case 'C':
                stored = current;
                break;
            }

            switch (requestedOperation) {
            case '+':
                log.addRow(current + " +");
                break;
            case '-':
                log.addRow(current + " -");
                break;
            case '/':
                log.addRow(current + " /");
                break;
            case '*':
                log.addRow(current + " x");
                break;
            case '=':
                log.addRow(current + " =");
                log.addRow("=======");
                log.addRow("" + stored);
                break;
            }

            lastOperationRequested = requestedOperation;
            current = null;
            if (requestedOperation == 'C') {
                stored = 0.0;
            }
            return stored;
        }

    }

    static class Log extends Table implements Handler {

        private int row = 1;

        private static final Action CLEAR = new Action("Clear");
        private static final Action ADD_COMMENT = new Action("Add Comment");
        private static final Action[] ACTIONS = { CLEAR, ADD_COMMENT };

        public Log() {
            setWidth("200px");
            // setHeight("100%");
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

}
