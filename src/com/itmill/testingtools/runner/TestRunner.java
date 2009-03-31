package com.itmill.testingtools.runner;

public class TestRunner extends ITMillToolkitTestCase {

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
    }

    // public void testNew() throws Exception {
    // selenium.open("/TT/sampler?restartApplication");
    // waitForITMillToolkit();
    // selenium
    // .click("itmilltoolkit=TTsampler::/IVerticalLayout[0]/ChildComponentContainer[1]/ISplitPanelHorizontal[0]/IPanel[0]/IGridLayout[0]/AbsolutePanel[0]/ChildComponentContainer[1]/IButton[0]");
    // waitForITMillToolkit();
    // selenium
    // .click("itmilltoolkit=TTsampler::/IVerticalLayout[0]/ChildComponentContainer[0]/IHorizontalLayout[0]/ChildComponentContainer[6]/IHorizontalLayout[0]/ChildComponentContainer[1]/IButton[0]");
    // // waitForITMillToolkit();
    // }

    @Override
    public void tearDown() throws Exception {
        selenium.stop();
        super.tearDown();
    }
}
