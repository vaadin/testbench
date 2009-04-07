package com.itmill.testingtools.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.thoughtworks.selenium.SeleneseTestBase;

public class TestBase extends SeleneseTestBase {

    private SeleniumImplementation selenium;

    private static final int DEFAULT_SELENIUM_RC_PORT = 4444;
    private List<TestHost> testHosts = new ArrayList<TestHost>();

    public SeleniumImplementation getSelenium() {
        return selenium;
    }

    @Override
    public void setUp(String url, String browserString) throws Exception {
        TestHost seleniumRcHost = getRandomTestHost();

        if (url == null) {
            throw new IllegalArgumentException("No url specified");
        }

        selenium = new SeleniumImplementation(seleniumRcHost.getHost(),
                seleniumRcHost.getPort(), browserString, url);
        System.out.println("Starting test of " + url + " in " + browserString
                + " on " + seleniumRcHost.getHost());
        selenium.start();
    }

    private TestHost getRandomTestHost() {
        if (testHosts.isEmpty()) {
            return new TestHost("localhost", DEFAULT_SELENIUM_RC_PORT);
        }

        Random r = new Random();
        int id = r.nextInt(testHosts.size());
        return testHosts.get(id);
    }

    protected void setTestHosts(String[] testHosts) {
        if (testHosts == null) {
            this.testHosts.clear();
            return;
        }

        for (String testHost : testHosts) {
            String[] parts = testHost.split(":", 2);
            int port = DEFAULT_SELENIUM_RC_PORT;
            if (parts.length == 2) {
                port = Integer.parseInt(parts[1]);
            }

            this.testHosts.add(new TestHost(parts[0], port));
        }
    }

    public void waitForITMillToolkit() {
        selenium.waitForITMillToolkit();
    }

    private class TestHost {
        private String host;
        private int port;

        public TestHost(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

    }

}
