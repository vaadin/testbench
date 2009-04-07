package com.itmill.testingtools.runner;

import com.thoughtworks.selenium.SeleneseTestCase;

public abstract class AbstractTestCase extends SeleneseTestCase {

    public enum Browser {
        CHROME("*chrome");

        private String browserId;

        public String getBrowserId() {
            return browserId;
        }

        private Browser(String browserId) {
            this.browserId = browserId;
        }
    }

    protected TestBase testBase = new TestBase();

    public void waitForITMillToolkit() {
        testBase.waitForITMillToolkit();
    }

    @Override
    public void setUp(String url, String browserString) throws Exception {
        testBase.setUp(url, browserString);
        selenium = testBase.getSelenium();
    }

    @Override
    public void setUp(String url) throws Exception {
        testBase.setUp(url);
        selenium = testBase.getSelenium();
    }

    protected void setUp(String url, Browser browser) throws Exception {
        testBase.setUp(url, browser.getBrowserId());
        selenium = testBase.getSelenium();
    }

    protected void setTestHosts(String... testHosts) {
        testBase.setTestHosts(testHosts);
    }

    private static final String TEST_HOST_PROPERTY = "testingtools.tester.host";
    private static final String BROWSER_PROPERTY = "testingtools.tester.browser";
    private static final String DEPLOYMENT_HOST_PROPERTY = "testingtools.deployment.url";

    public void setUp() throws Exception {
        String testHosts = System.getProperty(TEST_HOST_PROPERTY);
        String deploymentHost = System.getProperty(DEPLOYMENT_HOST_PROPERTY);
        String browser = System.getProperty(BROWSER_PROPERTY);

        if (testHosts == null || testHosts.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing test hosts definition. Use -D"
                            + TEST_HOST_PROPERTY + "=testhost1,testhost2");
        }

        if (deploymentHost == null || deploymentHost.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing deployment host definition. Use -D"
                            + DEPLOYMENT_HOST_PROPERTY
                            + "=http://www.deployment.com:8080/. "
                            + "DO NOT include the context path, this is stored in the test case.");
        }
        if (browser == null || browser.length() == 0) {
            browser = Browser.CHROME.getBrowserId();
        }

        setTestHosts(testHosts.split(","));

        setUp(deploymentHost, browser);

        // RemoteControlConfiguration conf = new RemoteControlConfiguration();
        // conf.set
        // HTMLLauncher launcher = new HTMLLauncher(new SeleniumServer(conf));
    }

    @Override
    public void tearDown() throws Exception {
        selenium.stop();
        super.tearDown();
    }
}
