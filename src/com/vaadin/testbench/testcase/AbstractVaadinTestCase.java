package com.vaadin.testbench.testcase;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import com.thoughtworks.selenium.SeleneseTestCase;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.util.BrowserDimensions;
import com.vaadin.testbench.util.BrowserUtil;
import com.vaadin.testbench.util.BrowserVersion;
import com.vaadin.testbench.util.ImageComparison;
import com.vaadin.testbench.util.ImageData;

/**
 * An abstract Vaadin TestCase implementation. This can be extended to create
 * Vaadin test cases. You do not have to extend this class but it provides
 * Vaadin specific methods.
 * 
 */
public abstract class AbstractVaadinTestCase extends SeleneseTestCase {

    private static int imageNumber = 0;
    private static String testCaseName = "";
    private static List<junit.framework.AssertionFailedError> softAssert = new LinkedList<junit.framework.AssertionFailedError>();

    private BrowserDimensions browserDimensions = null;
    private BrowserVersion browserVersion = null;

    private static int maxAmountOfTests = 2;

    protected ImageComparison compare = new ImageComparison();

    private String browserIdentifier = null;
    private static int screenshotPause = 50;

    protected VaadinTestBase testBase = new VaadinTestBase();

    private int requestedCanvasWidth = -1;
    private int requestedCanvasHeight = -1;

    // Selenium exist in super class
    // /** Use this object to run all of your selenium tests */
    // protected Selenium selenium;

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

        // If browser is not set get browser info.
        BrowserVersion browser = getBrowserVersion();

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

        return validateScreenshot(fileName, d);
    }

    public boolean validateScreenshot(String fileName, double errorTolerance) {
        maxAmountOfTests = Parameters.getMaxRetries();
        long startScreenshot = System.currentTimeMillis();

        boolean result = false;

        // Add longer pause for reference screenshot!
        if (!compare.checkIfReferenceExists(fileName)) {
            getReferenceImage(fileName);

            return true;
        }

        // Small pause to give components a bit of render time
        pause(screenshotPause);
        doCommand("waitForVaadin", new String[] { "", "" });

        // Actually capture the screen
        String image = getImage();

        // Get the dimensions of the browser window and canvas
        BrowserDimensions dimensions = getBrowserAndCanvasDimensions();

        try {
            // Compare screenshot with saved reference screen
            result = compare.compareStringImage(image, fileName,
                    errorTolerance, dimensions, false);
        } catch (junit.framework.AssertionFailedError e) {
            // If a Assert.fail("") is caught check if it's a missing reference.
            // If other throw the AssertionFailedError.
            if (e.getMessage().contains("No reference found")) {
                softAssert.add(e);
            } else if (e.getMessage().contains("differs from reference image")) {

                // Build error screenshot directory.
                String directory = Parameters.getScreenshotDirectory();

                if (!File.separator
                        .equals(directory.charAt(directory.length() - 1))) {
                    directory = directory + File.separator;
                }
                directory = directory + File.separator + "errors"
                        + File.separator;
                screenshotPause += 200;

                // If we find errors in the image take new references x times or
                // until functional image is found.
                for (int i = 0; i < maxAmountOfTests; i++) {
                    pause(screenshotPause);

                    image = selenium.captureScreenshotToString();

                    // check that we didn't get null for out image
                    // and that it has length > 0
                    if (image == null) {
                        Assert.fail("Didn't get an image from selenium on run "
                                + (i + 1));
                    } else if (image.length() == 0) {
                        Assert.fail("Got a screenshot String with length 0 on run "
                                + (i + 1));
                    }

                    try {
                        result = compare.compareStringImage(image, fileName,
                                errorTolerance, dimensions, false);
                        if (result == true) {
                            long endScreenshot = System.currentTimeMillis();
                            boolean success = (new File(directory + fileName
                                    + ".html")).delete();
                            if (success) {
                                success = (new File(directory + fileName
                                        + ".png")).delete();
                                if (Parameters.isDebug()) {
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
                if (Parameters.isScreenshotSoftFail()) {
                    softAssert.add(e);
                    result = true;
                }
                if (!result) {
                    throw e;
                }
            } else {
                if (Parameters.isScreenshotSoftFail()) {
                    softAssert.add(e);
                    result = true;
                } else {
                    throw e;
                }
            }
        }
        return result;
    }

    protected void getReferenceImage(String fileName) {
        ImageData data = new ImageData(fileName,
                getBrowserAndCanvasDimensions());

        data.generateBaseDirectory();

        pause(screenshotPause);

        data.setOriginalImage(getImage());

        data.generateComparisonImage();
        do {
            data.copyComparison();

            pause(screenshotPause);

            data.setOriginalImage(getImage());

            data.generateComparisonImage();
        } while (!compare.compareImages(data));

        // Check that the comparison folder exists and create if
        // false
        File compareFolder = new File(data.getErrorDirectory());
        if (!compareFolder.exists()) {
            compareFolder.mkdir();
        }

        try {
            ImageIO.write(data.getComparisonImage(), "png", new File(
                    compareFolder + File.separator + data.getFileName()));
            softAssert.add(new junit.framework.AssertionFailedError(
                    "No reference found for " + fileName));
        } catch (IOException ioe) {
        }
    }

    private String getImage() {
        String image = selenium.captureScreenshotToString();

        // check that we didn't get null for out image
        // and that it has length > 0
        if (image == null) {
            Assert.fail("Didn't get an image from selenium.");
        } else if (image.length() == 0) {
            Assert.fail("Got a screenshot String with length 0.");
        }

        return image;
    }

    /** Sleeps for the specified number of milliseconds */
    @Override
    public void pause(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
        }
    }

    protected boolean setRequestedCanvasSize() {
        return BrowserDimensions.setCanvasSize(selenium, getBrowserVersion(),
                requestedCanvasWidth, requestedCanvasHeight);
    }

    public void clearDimensions() {
        browserDimensions = null;
    }

    protected BrowserDimensions getBrowserAndCanvasDimensions() {
        return getBrowserDimensions(false);
    }

    public BrowserDimensions getBrowserDimensions(boolean force) {
        if (force || browserDimensions == null) {
            browserDimensions = BrowserDimensions.getBrowserDimensions(
                    getBrowserVersion(), selenium);
            if (Parameters.isDebug()) {
                System.err.println("Calculating browser dimensions..");
            }
            if (browserDimensions == null) {
                Assert.fail("Couldn't get browser dimensions.");
            }
        }
        return browserDimensions;
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

    protected void setBrowserIdentifier(String browserIdentifier) {
        this.browserIdentifier = browserIdentifier;
    }

    protected String getBrowserIdentifier() {
        return browserIdentifier;
    }

    // Init methods

    /*
     * Sets up the test case. Uses system properties to determine test hosts,
     * deployment host and browser. Must be called before each test.
     */
    @Override
    public void setUp() throws Exception {
        String remoteControlHostName = Parameters.getRemoteControlHostName();
        String deploymentUrl = Parameters.getDeploymentURL();

        if (remoteControlHostName == null
                || remoteControlHostName.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing test hosts definition. Use -D"
                            + Parameters.REMOTE_CONTROL_HOST_NAME
                            + "=<ip or name of machine running RC or HUB>");
        }

        if (deploymentUrl == null || deploymentUrl.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing deployment URL definition. Use -D"
                            + Parameters.DEPLOYMENT_URL
                            + "=http://www.deployment.com:8080/. "
                            + "DO NOT include the context path, this is stored in the test case.");
        }

        if (browserIdentifier == null) {
            throw new IllegalArgumentException(
                    "Missing browser definition. Define using setBrowserIdentifier().");
        }

        // Check if a given canvas size was requested
        String canvasSize = Parameters.getScreenshotResolution();
        if (canvasSize != null) {
            try {
                String[] parts = canvasSize.split("x", 2);
                requestedCanvasWidth = Integer.parseInt(parts[0]);
                requestedCanvasHeight = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Invalid definition of screenshot resolution. Use -D"
                                + Parameters.SCREENSHOT_RESOLUTION
                                + "=<width>x<height>");
            }
        }

        testBase.setHubAddress(remoteControlHostName);
        testBase.setUp(deploymentUrl, browserIdentifier);
        selenium = testBase.getVaadinSelenium();
    }

    @Override
    public void tearDown() throws Exception {
        if (selenium != null) {
            selenium.stop();
            selenium = null;
        }
        super.tearDown();
    }

    protected BrowserVersion getBrowserVersion() {
        if (browserVersion == null) {
            if (Parameters.isDebug()) {
                System.err.println("Fetching browser version...");
            }
            browserVersion = BrowserUtil.getBrowserVersion(selenium);
        }
        return browserVersion;
    }

    protected String getScreenshotDirectory() throws IllegalArgumentException {
        String dir = Parameters.getScreenshotDirectory();
        if (dir == null) {
            throw new IllegalArgumentException(
                    "Missing reference directory definition. Use -D"
                            + Parameters.SCREENSHOT_DIRECTORY + "=<directory> ");
        }

        return dir;
    }

    protected void setupWindow(boolean mightUseScreenshots) {
        // Focus the window to ensure it is on top of the control window
        selenium.windowFocus();

        if (getBrowserVersion().isOpera()) {
            /*
             * Opera (10.62 and older at least) does not support window.resizeTo
             * or window.moveTo so we cannot move/resize the window but have to
             * accept whatever the browser uses (overrideable in
             * profile-opera.txt).
             */
        } else if (hasRequestedCanvasSize()) {
            // The user has specified a canvas size to use so we should
            // initialize to that size
            setRequestedCanvasSize();
        } else if (mightUseScreenshots) {
            // It is possible that sometimes during the script we want to take a
            // screenshot. No canvas size is specified so we maximize the
            // browser window.
            selenium.windowMaximize();
        } else {
            // Just go with whatever the default happens to be. Should not
            // matter as we are not actually capturing the screen.
        }

        // Fetch the size of the browser. Overwrite any previous info.
        getBrowserDimensions(true);

    }

    protected boolean hasRequestedCanvasSize() {
        return requestedCanvasWidth > 0;
    }

    private String mouseClickCommand = null;

    protected void doMouseClick(String locator, String value) {
        if (value == null) {
            value = "";
        }

        if (mouseClickCommand == null) {
            if (getBrowserVersion().isOpera()
                    && getBrowserVersion().isOlderVersion(10, 50)) {
                mouseClickCommand = "mouseClickOpera";
            } else {
                mouseClickCommand = "mouseClick";
            }
        }
        doCommand(mouseClickCommand, new String[] { locator, value });
    }
}
