package com.vaadin.testbench.commands;

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
}
