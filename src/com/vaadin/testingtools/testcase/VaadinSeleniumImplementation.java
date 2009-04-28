package com.vaadin.testingtools.testcase;

import com.thoughtworks.selenium.DefaultSelenium;

public class VaadinSeleniumImplementation extends DefaultSelenium {

    public VaadinSeleniumImplementation(String serverHost, int serverPort,
            String browserStartCommand, String browserURL) {
        super(serverHost, serverPort, browserStartCommand, browserURL);
    }

    public void waitForVaadin() {
        commandProcessor.doCommand("waitForVaadin", new String[] {});
    }

    public void doCommand(String cmd, String[] params) {
        commandProcessor.doCommand(cmd, params);
    }

}
