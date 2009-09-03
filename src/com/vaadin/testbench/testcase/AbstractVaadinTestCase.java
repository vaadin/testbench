package com.vaadin.testbench.testcase;

import com.thoughtworks.selenium.SeleneseTestCase;
import com.vaadin.testbench.util.ImageComparison;

/**
 * An abstract Vaadin TestCase implementation. This can be extended to create
 * Vaadin test cases. You do not have to extend this class but it provides
 * Vaadin specific methods.
 * 
 */
public abstract class AbstractVaadinTestCase extends SeleneseTestCase {

    private static final String DEFAULT_BROWSER = "*opera";// iexplore";// chrome";//
    private static int imageNumber = 0;
    private static String testCaseName = "";

    protected VaadinTestBase testBase = new VaadinTestBase();
    protected ImageComparison compare = new ImageComparison();

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
    public boolean validateScreenshot(String fileId, double d) throws Exception {
        // Set testCaseName so that we can have unified numbering on testCases
        // 1-> per test case instead of 1-> per browser.
        if (!testCaseName.equals(fileId)) {
            testCaseName = fileId;
            imageNumber = 0;
        }

        boolean result = false;

        // Pause so that we don't get the loading marker for vaadin applications
        pause(500);

        String image = "";
        String navigator = selenium.getEval("navigator.appCodeName;");
        String version = selenium.getEval("navigator.appVersion;");

        navigator = (navigator.concat("_"
                + version.substring(0, version.indexOf('(')))).trim();

        // setup filename
        String fileName = fileId + "_" + navigator + "_" + ++imageNumber;

        image = selenium.captureScreenshotToString();

        // Check if the used browser is InternetExplorer that gives differently
        // built values for appCodeName and appVersion.
        if (version.contains("MSIE")) {
            navigator = version.substring(version.indexOf("MSIE"), version
                    .indexOf(';', version.indexOf("MSIE")));
            navigator = navigator.replaceAll("\\s", "_");
            fileName = fileId + "_" + navigator + "_" + imageNumber;
        }

        int height = Integer.parseInt(selenium.getEval("screen.availHeight;"));
        int width = Integer.parseInt(selenium.getEval("screen.availWidth;"));
        int canvasHeight = getCanvasHeight();
        int canvasWidth = getCanvasWidth();

        int[] dimensions = { width, height, canvasWidth, canvasHeight };

        // Compare screenshot with saved reference screen
        result = compare.compareStringImage(image, fileName, d, dimensions);

        return result;
    }

    /**
     * Get Canvas height for browser
     * 
     * @return Canvas height
     */
    public int getCanvasHeight() {
        int canvasHeight = 0;
        try {
            canvasHeight = Integer.parseInt(selenium
                    .getEval("window.innerHeight;"));
        } catch (NumberFormatException nfe) {
            try {
                canvasHeight = Integer.parseInt(selenium
                        .getEval("document.documentElement.clientHeight;"));
            } catch (NumberFormatException nfe2) {
                try {
                    canvasHeight = Integer.parseInt(selenium
                            .getEval("document.body.clientHeight;"));
                } catch (NumberFormatException nfe3) {
                }
            }
        }
        return canvasHeight;
    }

    /**
     * Get canvas width for browser
     * 
     * @return Canvas width
     */
    public int getCanvasWidth() {
        int canvasWidth = 0;
        try {
            canvasWidth = Integer.parseInt(selenium
                    .getEval("window.innerWidth;"));
        } catch (NumberFormatException nfe) {
            try {
                canvasWidth = Integer.parseInt(selenium
                        .getEval("document.documentElement.clientWidth;"));
            } catch (NumberFormatException nfe2) {
                try {
                    canvasWidth = Integer.parseInt(selenium
                            .getEval("document.body.clientWidth;"));
                } catch (NumberFormatException nfe3) {
                }
            }
        }
        return canvasWidth;
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
