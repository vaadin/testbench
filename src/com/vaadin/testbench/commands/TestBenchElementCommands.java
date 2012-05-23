package com.vaadin.testbench.commands;

public interface TestBenchElementCommands {
    /**
     * Closes a notification
     * 
     * @return true if the notification was successfully closed.
     */
    boolean closeNotification();

    /**
     * Shows the tool tip of the specified element.
     */
    void showTooltip();

    /**
     * Scrolls the element down to the specified top value.
     * 
     * @param scrollTop
     *            the new value for scrollTop.
     */
    void scroll(int scrollTop);

    /**
     * Scrolls the element left to the specified left value.
     * 
     * @param scrollLeft
     *            the new value for scrollLeft.
     */
    void scrollLeft(int scrollLeft);
}
