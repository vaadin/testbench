package com.vaadin.testbench.testcase;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.selenium.SeleneseTestCase;
import com.vaadin.testbench.util.BrowserDimensions;
import com.vaadin.testbench.util.BrowserUtil;
import com.vaadin.testbench.util.ImageComparison;

/**
 * An abstract Vaadin TestCase implementation. This can be extended to create
 * Vaadin test cases. You do not have to extend this class but it provides
 * Vaadin specific methods.
 * 
 */
public abstract class AbstractVaadinTestCase extends SeleneseTestCase {

    private static final String DEFAULT_BROWSER = "*chrome";

    private static int imageNumber = 0;
    private static String testCaseName = "";
    private static List<junit.framework.AssertionFailedError> softAssert = new LinkedList<junit.framework.AssertionFailedError>();

    protected VaadinTestBase testBase = new VaadinTestBase();
    protected ImageComparison compare = new ImageComparison();
    protected BrowserUtil browserUtils = new BrowserUtil();

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

    /**
     * Capture a screenshot of only the browser canvas and save to saveName.
     * Compare captured screenshot to reference screenshot.
     * 
     * @param saveName
     *            Place to save screenshot + name
     * @return true, if equals reference image
     */
    public boolean validateScreenshot(String fileId, double d, String identifier) {
        // Set testCaseName so that we can have unified numbering on
        // testCases
        // 1-> per test case instead of 1-> per browser.
        if (!testCaseName.equals(fileId)) {
            testCaseName = fileId;
            imageNumber = 0;
            softAssert = new LinkedList<junit.framework.AssertionFailedError>();
        }

        boolean result = false;

        // Pause so that we don't get the loading marker for vaadin
        // applications (and wait long enough for labels to show)
        pause(500);

        String navigator = browserUtils.browserVersion(selenium);

        // setup filename
        String fileName = "";
        if (identifier == null || identifier.length() < 1) {
            fileName = fileId + "_" + navigator + "_" + ++imageNumber;
        } else {
            fileName = fileId + "_" + navigator + "_" + identifier;
        }

        String image = selenium.captureScreenshotToString();
        if (image == null) {
            return false;
        }

        // Get sizes for canvas cropping.
        int width = Integer.parseInt(selenium.getEval("screen.availWidth;"));
        int height = Integer.parseInt(selenium.getEval("screen.availHeight;"));
        int canvasWidth = browserUtils.getCanvasWidth(selenium);
        int canvasHeight = browserUtils.getCanvasHeight(selenium);
        int canvasXPosition = browserUtils.canvasXPosition(selenium);
        int canvasYPosition = browserUtils.canvasYPosition(selenium);

        BrowserDimensions dimensions = new BrowserDimensions(width, height,
                canvasWidth, canvasHeight, canvasXPosition, canvasYPosition);

        try {
            // Compare screenshot with saved reference screen
            result = compare.compareStringImage(image, fileName, d, dimensions);
        } catch (junit.framework.AssertionFailedError e) {
            // If a Assert.fail("") is caught check if it's a missing reference.
            // If other throw the AssertionFailedError.
            if (e.getMessage().contains("No reference found")) {
                softAssert.add(e);
            } else {
                throw e;
            }
        }
        return result;
    }

    /**
     * Give collection of AssertionFailedErrors with "No reference found" in
     * message.
     * 
     * @return List of AssertionFailedErrors for missing references
     */
    public List<junit.framework.AssertionFailedError> getSoftErrors() {
        return softAssert;
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

    private static final String TEST_HOST_PROPERTY = "com.vaadin.testbench.tester.host";
    private static final String BROWSER_PROPERTY = "com.vaadin.testbench.tester.browser";
    private static final String DEPLOYMENT_URL_PROPERTY = "com.vaadin.testbench.deployment.url";

    /*
     * Sets up the test case. Uses system properties to determine test hosts,
     * deployment host and browser.
     */
    @Override
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
