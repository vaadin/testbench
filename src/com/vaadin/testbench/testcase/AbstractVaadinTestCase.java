package com.vaadin.testbench.testcase;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import com.thoughtworks.selenium.SeleneseTestCase;
import com.vaadin.testbench.util.BrowserDimensions;
import com.vaadin.testbench.util.BrowserUtil;
import com.vaadin.testbench.util.BrowserVersion;
import com.vaadin.testbench.util.ImageComparison;
import com.vaadin.testbench.util.ImageUtil;

/**
 * An abstract Vaadin TestCase implementation. This can be extended to create
 * Vaadin test cases. You do not have to extend this class but it provides
 * Vaadin specific methods.
 * 
 */
public abstract class AbstractVaadinTestCase extends SeleneseTestCase {

    private static final String DEFAULT_BROWSER = "winxp-firefox35";// "*chrome";

    private static int imageNumber = 0;
    private static String testCaseName = "";
    private static List<junit.framework.AssertionFailedError> softAssert = new LinkedList<junit.framework.AssertionFailedError>();
    private static BrowserDimensions dimensions = null;
    private static BrowserVersion browser = null;
    private static final int maxAmountOfTests = 2;
    private static final String DEBUG = "com.vaadin.testbench.debug";
    private static final String SOFT_FAIL = "com.vaadin.testbench.screenshot.softfail";

    protected VaadinTestBase testBase = new VaadinTestBase();
    protected ImageComparison compare = new ImageComparison();
    protected BrowserUtil browserUtils = new BrowserUtil();

    private String browserOverride = null;
    private static int screenshotPause = 50;

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
        long startScreenshot = System.currentTimeMillis();

        boolean debug = false;
        if ("true".equalsIgnoreCase(System.getProperty(DEBUG))) {
            debug = true;
        }

        boolean result = false;

        // Small pause to give components a bit of render time
        pause(screenshotPause);

        // If browser is not set get browser info.
        if (browser == null) {
            browser = browserUtils.getBrowserVersion(selenium);
        }
        String navigatorId = browser.getIdentifier();

        // setup filename
        String fileName = "";
        if (identifier == null || identifier.length() < 1) {
            fileName = fileId + "_" + browser.getPlatform() + "_" + navigatorId
                    + "_" + ++imageNumber;
        } else {
            fileName = fileId + "_" + browser.getPlatform() + "_" + navigatorId
                    + "_" + identifier;
        }

        String image = selenium.captureScreenshotToString();

        // check that we didn't get null for out image
        // and that it has length > 0
        if (image == null) {
            Assert.fail("Didn't get an image from selenium.");
        } else if (image.length() == 0) {
            Assert.fail("Got a screenshot String with length 0.");
        }

        // check that we have browserDimensions
        // if this fails do Assert.fail
        if (dimensions == null) {
            getCanvasPosition();
            if (dimensions == null) {
                Assert.fail("Couldn't get browser dimensions.");
            }
        }

        // If browser is IE we can check that no top bars have been shown.
        // If one has been we can correct the cropping dimensions.
        if (browser.isIE()) {
            int yPosition = browserUtils.canvasYPosition(selenium, browser);
            if ((yPosition + 2) != dimensions.getCanvasYPosition()) {
                // Add difference in height to canvasHeight
                dimensions.setCanvasHeight(browserUtils
                        .getCanvasHeight(selenium));
                // Set new y position
                dimensions.setCanvasYPosition(yPosition + 2);
            }
        }

        try {
            // Compare screenshot with saved reference screen
            result = compare.compareStringImage(image, fileName, d, dimensions,
                    false);
        } catch (junit.framework.AssertionFailedError e) {
            // If a Assert.fail("") is caught check if it's a missing reference.
            // If other throw the AssertionFailedError.
            if (e.getMessage().contains("No reference found")) {
                softAssert.add(e);
            } else if (e.getMessage().contains("differs from reference image")) {

                // Build error screenshot directory.
                String directory = System
                        .getProperty("com.vaadin.testbench.screenshot.directory");

                if (!File.separator.equals(directory
                        .charAt(directory.length() - 1))) {
                    directory = directory + File.separator;
                }
                directory = directory + File.separator + "errors"
                        + File.separator;

                // If we find errors in the image take new references x times or
                // until functional image is found.
                for (int i = 0; i < maxAmountOfTests; i++) {
                    pause(200);

                    image = selenium.captureScreenshotToString();

                    // check that we didn't get null for out image
                    // and that it has length > 0
                    if (image == null) {
                        Assert.fail("Didn't get an image from selenium on run "
                                + (i + 1));
                    } else if (image.length() == 0) {
                        Assert
                                .fail("Got a screenshot String with length 0 on run "
                                        + (i + 1));
                    }

                    try {
                        result = compare.compareStringImage(image, fileName, d,
                                dimensions, false);
                        if (result == true) {
                            long endScreenshot = System.currentTimeMillis();
                            boolean success = (new File(directory + fileName
                                    + ".html")).delete();
                            if (success) {
                                success = (new File(directory + fileName
                                        + ".png")).delete();
                                if (debug) {
                                    if (success) {
                                        System.err
                                                .println("Removed created clean image and difference html.\n"
                                                        + "Comparison successful");
                                    } else {
                                        System.err
                                                .println("Removed created difference html.\n"
                                                        + "Comparison successful");
                                    }
                                }
                            } else {
                                System.err
                                        .println("Failed to remove created error files.\n"
                                                + "Comparison successful.");
                            }
                            screenshotPause = (int) (endScreenshot - startScreenshot);
                            if (screenshotPause > 700) {
                                screenshotPause = 50;
                            }
                            return result;
                        }
                    } catch (junit.framework.AssertionFailedError afe) {
                        result = false;
                    }
                }

                // Do a Roberts Cross edge detection on images and compare for
                // diff. Should remove some small faults.
                // result = compare.compareStringImage(image, fileName, d,
                // dimensions, true);
                if ("true".equals(System.getProperty(SOFT_FAIL))) {
                    softAssert.add(e);
                    result = true;
                }
                if (!result) {
                    throw e;
                }
            } else {
                if ("true".equals(System.getProperty(SOFT_FAIL))) {
                    softAssert.add(e);
                    result = true;
                } else {
                    throw e;
                }
            }
        }
        return result;
    }

    public void getCanvasPosition() {
        // clear for new test.
        imageNumber = 0;
        softAssert.clear();

        browser = browserUtils.getBrowserVersion(selenium);

        // Get sizes for canvas cropping.
        int width = Integer.parseInt(selenium.getEval("screen.availWidth;"));
        int height = Integer.parseInt(selenium.getEval("screen.availHeight;"));
        int canvasWidth = browserUtils.getCanvasWidth(selenium);
        int canvasHeight = browserUtils.getCanvasHeight(selenium);
        int canvasXPosition = browserUtils.canvasXPosition(selenium, browser);
        int canvasYPosition = browserUtils.canvasYPosition(selenium, browser);

        dimensions = new BrowserDimensions(width, height, canvasWidth,
                canvasHeight, canvasXPosition, canvasYPosition);

        if (browser.isIE()) {
            dimensions.setCanvasXPosition(canvasXPosition + 2);
            dimensions.setCanvasYPosition(canvasYPosition + 2);
            // get canvas width so that it includes mainview scrollbar.
            dimensions.setCanvasWidth(width
                    - (dimensions.getCanvasXPosition() * 2));

            // Print dimensions if debug
            if ("true".equalsIgnoreCase(System.getProperty(DEBUG))) {
                System.out.println("availWidth: " + dimensions.getWidth()
                        + "\navailHeight: " + dimensions.getHeight()
                        + "\ncanvasWidth: " + dimensions.getCanvasWidth()
                        + "\ncanvasHeight: " + dimensions.getCanvasHeight()
                        + "\ncanvasX: " + dimensions.getCanvasXPosition()
                        + "\ncanvasY: " + dimensions.getCanvasYPosition());
            }

            return;
        }

        pause(200);
        String image = selenium.captureScreenshotToString();

        BufferedImage screenshot = ImageUtil.stringToImage(image);

        int[] startBlock = new int[10];
        startBlock = screenshot.getRGB(dimensions.getCanvasXPosition(),
                dimensions.getCanvasYPosition(), 1, 10, startBlock, 0, 1);

        for (int y = dimensions.getCanvasYPosition(); y > 0; y--) {
            int[] testBlock = new int[10];
            testBlock = screenshot.getRGB(dimensions.getCanvasXPosition(), y,
                    1, 10, testBlock, 0, 1);
            if (!Arrays.equals(startBlock, testBlock)) {
                dimensions.setCanvasYPosition(y + 1);
                break;
            }
        }

        // Print dimensions if debug
        if ("true".equalsIgnoreCase(System.getProperty(DEBUG))) {
            System.out.println("availWidth: " + dimensions.getWidth()
                    + "\navailHeight: " + dimensions.getHeight()
                    + "\ncanvasWidth: " + dimensions.getCanvasWidth()
                    + "\ncanvasHeight: " + dimensions.getCanvasHeight()
                    + "\ncanvasX: " + dimensions.getCanvasXPosition()
                    + "\ncanvasY: " + dimensions.getCanvasYPosition());
        }
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
