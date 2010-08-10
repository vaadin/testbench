package com.vaadin.testbench.testcase;

import com.thoughtworks.selenium.SeleneseTestBase;

public class VaadinTestBase extends SeleneseTestBase {

    private VaadinSeleniumImplementation vaadinSelenium;

    private static final int DEFAULT_SELENIUM_RC_PORT = 4444;
    private String seleniumRcHubHost;
    private int seleniumRcHubPort = DEFAULT_SELENIUM_RC_PORT;

    VaadinSeleniumImplementation getVaadinSelenium() {
        return vaadinSelenium;
    }

    @Override
    public void setUp(String url, String browserString) throws Exception {

        if (url == null) {
            throw new IllegalArgumentException("No url specified");
        }
        if (browserString == null) {
            throw new IllegalArgumentException("No browser specified");
        }
        vaadinSelenium = new VaadinSeleniumImplementation(seleniumRcHubHost,
                seleniumRcHubPort, browserString, url);

        // System.out.println("Starting test of " + url + " in " + browserString
        // + " on " + seleniumRcHost.getHost());

        vaadinSelenium.start();
    }

    /**
     * Sets the RC/HUB machine to use to the given testHost
     * 
     * @param testHost
     *            Test host as dns name "host.name" or ip address "127.0.0.1".
     *            May contain an additional ":port" part at the end.
     */
    protected void setHubAddress(String testHost) {
        String[] parts = testHost.split(":", 2);
        seleniumRcHubHost = parts[0];
        if (parts.length == 2) {
            seleniumRcHubPort = Integer.parseInt(parts[1]);
        }
    }

    /**
     * Wait for Vaadin to complete processing the current request.
     */
    public void waitForVaadin() {
        vaadinSelenium.waitForVaadin();
    }

    public void doCommand(String cmd, String[] params) {
        getVaadinSelenium().doCommand(cmd, params);
    }
}
