package com.vaadin.testbench.testcase;

import com.thoughtworks.selenium.DefaultSelenium;

public class VaadinSeleniumImplementation extends DefaultSelenium {

    public VaadinSeleniumImplementation(String serverHost, int serverPort,
            String browserStartCommand, String browserURL) {
        super(serverHost, serverPort, browserStartCommand, browserURL);
    }

    public void waitForVaadin() {
        commandProcessor.doCommand("waitForVaadin", new String[] {});
    }

    public String doCommand(String cmd, String[] params) {
        return commandProcessor.doCommand(cmd, params);
    }

}
