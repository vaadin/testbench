package com.vaadin.testbenchexample;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

/**
 * The Keypad object provides a calculator display and keypad, and makes up the
 * main part of the UI of our application.
 */
@SuppressWarnings("serial")
public class Keypad extends GridLayout {

    private TextField display;
    private List<CalculatorLogger> loggers;
    private Queue<String> currentCalculation = new ArrayDeque<String>();

    // By using an autoboxed Double value for the current value, we eliminate an
    // otherwise necessary state boolean, resulting in cleaner (if somewhat less
    // obvious) code. See the calculate(char) method.
    private Double currentValue;
    private double storedValue;
    private char lastOperation;

    public Keypad() {
        // Call GridLayout constructor so that we get a 4x5 slot layout.
        super(4, 5);

        setSpacing(true);

        // Create a textfield component that we'll use for our calculator
        // display
        display = new TextField();
        display.setSizeFull();
        display.setId("display");
        display.setValue("0.0");

        // Add the textfield to our keypad, define it so it takes up the top
        // four slots (i.e. the entire top row)
        addComponent(display, 0, 0, 3, 0);
        setComponentAlignment(display, Alignment.MIDDLE_RIGHT);

        // Start defining our calculator buttons.
        // This array defines the captions of the buttons we wish to add in a
        // left-to-right, top-to-bottom order
        final String[] operations = new String[] { "7", "8", "9", "/", "4",
                "5", "6", "*", "1", "2", "3", "-", "0", "=", "C", "+" };

        for (String caption : operations) {

            // Create a button and add it to the layout
            Button button = new Button(caption);
            button.setId("button_" + caption);
            button.setWidth("40px");
            addComponent(button);

            // Re-use the same click handler for all buttons (it's not dependent
            // on scope or anything)
            button.addClickListener(buttonClickHandler);
        }

        loggers = new ArrayList<CalculatorLogger>();

        // Initialize calculator values
        currentValue = null;
        storedValue = 0.0;
    }

    /**
     * Perform calculations based on button clicks
     * 
     * @param operation
     *            character taken from the pressed button
     * @return current calculation value
     */
    private double calculate(char operation) {

        // Check if user clicked a number button
        if ('0' <= operation && operation <= '9') {

            // By setting the current value to null, we state that we're
            // starting from a clean slate.
            // If this is the case here, we re-init the current value to 0.
            if (currentValue == null) {
                currentValue = 0.0;
            }

            // Multiply current value by 10 (shifting the decimal point up)
            currentValue *= 10;

            // Parse an integer value out of the 'operation' character and
            // append it to the current value
            currentValue += Integer.parseInt("" + operation);

            // We do not want to do any additional processing when we're just
            // appending values..
            return currentValue;
        }

        // If we're "starting from a clean slate" and are not inputting a new
        // number, we'll want to set the current value to the last calculated
        // value
        if (currentValue == null) {
            currentValue = storedValue;
        }

        // Since we're here, we'll need to perform the last requested operation;
        // this would be one of +, -, * or /. We do this here to have the
        // correct values written into the log
        switch (lastOperation) {
        case '+':
            storedValue += currentValue;
            break;
        case '-':
            storedValue -= currentValue;
            break;
        case '*':
            storedValue *= currentValue;
            break;
        case '/':
            storedValue /= currentValue;
            break;
        default:
            storedValue = currentValue;
            break;
        }

        // We'll want to log the operation that we wish to perform, as well as
        // handling a 'clear' request.
        switch (operation) {
        case '+':
            currentCalculation.add(currentValue + " + ");
            break;

        case '-':
            currentCalculation.add(currentValue + " - ");
            break;

        case '*':
            currentCalculation.add(currentValue + " * ");
            break;

        case '/':
            currentCalculation.add(currentValue + " / ");
            break;

        case '=':
            // Set the stored value to the current value, and log
            currentCalculation.add(currentValue + " = " + storedValue);
            logCurrentCalculationAndResult();
            break;

        case 'C':
            // The clear resets the stored value and makes this clear in the log
            clearLog();
            storedValue = 0;
            break;
        }

        // Update the last operation to the one we just logged, so that it gets
        // run on the entered value the next time this method is run
        lastOperation = operation;

        // Regardless of what we just did, we'll want to re-set the current
        // value to null, to indicate that the next time we're in this function,
        // we're starting from a clean slate, without existing input
        currentValue = null;

        // When an operation has been performed, return the stored value in
        // order to correctly update the display
        return storedValue;
    }

    /**
     * Forms a string representing the current calculation and logs it.
     */
    private void logCurrentCalculationAndResult() {
        StringBuilder logSB = new StringBuilder();
        while (!currentCalculation.isEmpty()) {
            logSB.append(currentCalculation.poll());
        }
        log(logSB.toString());
    }

    /**
     * This button click handler object does not rely on scope, and can thus be
     * re-used for all buttons on the Keypad
     */
    ClickListener buttonClickHandler = new ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {

            // Get a reference to the button that received the click
            Button button = event.getButton();

            // Get the requested operation from the button caption
            char requestedOperation = button.getCaption().charAt(0);

            // Calculate the new value
            double newValue = calculate(requestedOperation);

            // Update the result label with the new value
            display.setValue("" + newValue);
        }
    };

    /**
     * Stores a new {@link CalculatorLogger} for logging activity.
     * 
     * @param l
     *            instance of a class implementing {@link CalulatorLogger}
     */
    public void addLogger(CalculatorLogger l) {
        loggers.add(l);
    }

    /**
     * Removes a {@link CalculatorLogger}. No more logging will be done through
     * this logger.
     * 
     * @param l
     *            instance of a class implementing {@link CalulatorLogger}
     */
    public void removeLogger(CalculatorLogger l) {
        loggers.remove(l);
    }

    /**
     * Logging function - outputs operation info as it happens to any and all
     * registered loggers
     * 
     * @param str
     *            any string
     */
    private void log(String str) {
        for (CalculatorLogger l : loggers) {
            l.log(str);
        }
    }

    /**
     * Function for clearing attached loggers - how the logs are actually
     * cleared is up to the logger implementation
     */
    private void clearLog() {
        for (CalculatorLogger l : loggers) {
            l.clear();
        }
    }

}
