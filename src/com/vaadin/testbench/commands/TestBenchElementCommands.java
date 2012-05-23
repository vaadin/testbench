package com.vaadin.testbench.commands;

import org.openqa.selenium.Keys;

public interface TestBenchElementCommands {
    void expectDialog(Keys... modifierKeysPressed);

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

    void scroll(int scrollTop);

    void scrollLeft(int scrollLeft);
}
