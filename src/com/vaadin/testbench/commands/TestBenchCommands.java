package com.vaadin.testbench.commands;

import org.openqa.selenium.WebElement;

/**
 */
public interface TestBenchCommands {
    public static String SET_TEST_NAME = "setTestName";

    public void setTestName(String testName);

    @Deprecated
    public void setCanvasSize(int w, int h);

    @Deprecated
    public String getCanvasSize();

    public String getRemoteControlName();

    @Deprecated
    public String captureScreenshotToString();

    public void expectDialog(WebElement element, String value);

    public boolean closeNotification(WebElement element);
}
