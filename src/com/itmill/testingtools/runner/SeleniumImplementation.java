package com.itmill.testingtools.runner;

import com.thoughtworks.selenium.DefaultSelenium;

public class SeleniumImplementation extends DefaultSelenium {

    public SeleniumImplementation(String serverHost, int serverPort,
            String browserStartCommand, String browserURL) {
        super(serverHost, serverPort, browserStartCommand, browserURL);
    }

    public void waitForITMillToolkit() {
        commandProcessor.doCommand("waitForITMillToolkit", new String[] {});
    }

    public void doCommand(String cmd, String[] params) {
        commandProcessor.doCommand(cmd, params);
    }

}
