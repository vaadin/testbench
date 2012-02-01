package com.vaadin.testbench.commands;

import org.openqa.selenium.WebElement;

/**
 */
public interface TestBenchCommands {
    static String SET_TEST_NAME = "setTestName";

    void setTestName(String testName);

    String getRemoteControlName();

    void expectDialog(WebElement element, String value);

    boolean closeNotification(WebElement element);

    void showTooltip(WebElement element);

    void scroll(WebElement element, int scrollTop);

    void scrollLeft(WebElement element, int scrollLeft);

    void waitForVaadin();
}
