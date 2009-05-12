package com.vaadin.testingtools.testcase;

import com.thoughtworks.selenium.SeleneseTestCase;

/**
 * An abstract Vaadin TestCase implementation. This can be extended to create
 * Vaadin test cases. You do not have to extend this class but it provides
 * Vaadin specific methods.
 * 
 */
public abstract class AbstractVaadinTestCase extends SeleneseTestCase {

    private static final String DEFAULT_BROWSER = "*chrome";

    protected VaadinTestBase testBase = new VaadinTestBase();

    private String browserOverride = null;

    /**
     * Wait for Vaadin to complete processing the current request.
     */
    public void waitForVaadin() {
        testBase.waitForVaadin();
    }

    public void doCommand(String cmd, String[] params) {
        testBase.doCommand(cmd, params);
    }

    /*
     * Sets up the test case using the supplied deploymentUrl and browser. Call
     * setTestHosts before calling this to select the testing host.
     */
    @Override
    public void setUp(String deploymentUrl, String browserString)
            throws Exception {
        testBase.setUp(deploymentUrl, browserString);
        selenium = testBase.getVaadinSelenium();
    }

    @Override
    public void setUp(String url) throws Exception {
        if (browserOverride != null) {
            testBase.setUp(url, browserOverride);
        } else {
            testBase.setUp(url);
        }

        selenium = testBase.getVaadinSelenium();
    }

    protected void setTestHosts(String... testHosts) {
        testBase.setTestHosts(testHosts);
    }

    protected void setBrowser(String browser) {
        browserOverride = browser;
    }

    private static final String TEST_HOST_PROPERTY = "com.vaadin.testingtools.tester.host";
    private static final String BROWSER_PROPERTY = "com.vaadin.testingtools.tester.browser";
    private static final String DEPLOYMENT_URL_PROPERTY = "com.vaadin.testingtools.deployment.url";

    /*
     * Sets up the test case. Uses system properties to determine test hosts,
     * deployment host and browser.
     */
    public void setUp() throws Exception {
        String testHosts = System.getProperty(TEST_HOST_PROPERTY);
        String deploymentUrl = System.getProperty(DEPLOYMENT_URL_PROPERTY);
        String browser = System.getProperty(BROWSER_PROPERTY);

        if (testHosts == null || testHosts.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing test hosts definition. Use -D"
                            + TEST_HOST_PROPERTY + "=testhost1,testhost2");
        }

        if (deploymentUrl == null || deploymentUrl.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing deployment URL definition. Use -D"
                            + DEPLOYMENT_URL_PROPERTY
                            + "=http://www.deployment.com:8080/. "
                            + "DO NOT include the context path, this is stored in the test case.");
        }

        if (browserOverride != null) {
            browser = browserOverride;
        } else if (browser == null || browser.length() == 0) {
            browser = DEFAULT_BROWSER;
        }

        setTestHosts(testHosts.split(","));
        setUp(deploymentUrl, browser);
    }

    @Override
    public void tearDown() throws Exception {
        selenium.stop();
        super.tearDown();
    }
}
