package com.vaadin.testbench.util;

public class CurrentCommand {

    private int cmdNr;
    private String file;
    private String cmd;

    public CurrentCommand(String file) {
        cmdNr = 0;
        this.file = file;
        cmd = "";
    }

    public void resetCmdNr() {
        cmdNr = 0;
    }

    public void reduceCommandNumber() {
        cmdNr--;
    }

    public void setCommand(String command, String locator, String value) {
        cmdNr++;
        cmd = command + "\n Locator:" + locator + "\n Value: " + value;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getInfo() {
        return file + " failed\n Command Number: " + cmdNr + "\n Cmd: " + cmd;
    }
}
