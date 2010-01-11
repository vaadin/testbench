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

    public void setCommand(String command, String value) {
        cmdNr++;
        if (value.length() > 0) {
            cmd = command + " and value = " + value;
        } else {
            cmd = command;
        }
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getInfo() {
        return new String("In Test = " + file + " at CommandNr = " + cmdNr
                + " with Cmd = " + cmd);
    }
}
