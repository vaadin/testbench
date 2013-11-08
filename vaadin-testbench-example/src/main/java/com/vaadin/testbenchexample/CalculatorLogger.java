package com.vaadin.testbenchexample;

/**
 * The Logger interface will allow the Keypad to connect to arbitrary log
 * components (or none at all).
 */
public interface CalculatorLogger {

    /**
     * The log function should write a string to the log device
     * 
     * @param str
     */
    public void log(String str);

    /**
     * The clear function is a signal to the logger that the user wishes it
     * cleared - how this clearing is done is left up to the implementor
     */
    public void clear();

}
