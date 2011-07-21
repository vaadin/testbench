package com.vaadin.testbench.testcase;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.thoughtworks.selenium.SeleneseTestCase;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.util.BrowserDimensions;
import com.vaadin.testbench.util.BrowserUtil;
import com.vaadin.testbench.util.BrowserVersion;
import com.vaadin.testbench.util.ImageComparison;
import com.vaadin.testbench.util.ImageFileUtil;
import com.vaadin.testbench.util.ImageUtil;

/**
 * An abstract Vaadin TestCase implementation. This can be extended to create
 * Vaadin test cases. You do not have to extend this class but it provides
 * Vaadin specific methods.
 * 
 */
public abstract class AbstractVaadinTestCase extends SeleneseTestCase {

    private static int imageNumber = 0;
    private static List<junit.framework.AssertionFailedError> softAssert = new LinkedList<junit.framework.AssertionFailedError>();

    private static final String[] error_messages = {
            "was missing reference images", "contained differences",
            "contained images with differing sizes containing differences",
            "contained images with differing sizes", "" };

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

    public String doCommand(String cmd, String[] params) {
        String result = testBase.doCommand(cmd, params);

        if (!cmd.equals("close") && !cmd.equals("expectDialog")
                && !cmd.equals("getRemoteControlName")) {
            waitForVaadin();
        }

        return result;
    }

    public boolean validateScreenshot(String testName, String imageIdentifier) {
        return validateScreenshot(testName,
                Parameters.getScreenshotComparisonTolerance(), imageIdentifier);
    }

    /**
     * Capture a screenshot of the browser canvas and compare with Compare
     * captured screenshot to reference screenshot.
     * 
     * @param fileId
     *            The test identifier. Used as part of the image filename.
     * @param errorTolerance
     *            The error to accept during comparison. Higher value means more
     *            tolerant. Between 0 and 1.
     * @param imageIdentifier
     *            The screenshot identifier. Used as part of the image filename.
     * @return true, if the captured screenshots equals the reference image
     */
    public boolean validateScreenshot(String testName, double errorTolerance,
            String imageIdentifier) {

        // If browser is not set get browser info.
        BrowserVersion browser = getBrowserVersion();

        String navigatorId = browser.getIdentifier();

        // setup filename
        String fileName = "";
        if (imageIdentifier == null || imageIdentifier.length() < 1) {
            fileName = testName + "_" + browser.getPlatform() + "_"
                    + navigatorId + "_" + ++imageNumber;
        } else {
            fileName = testName + "_" + browser.getPlatform() + "_"
                    + navigatorId + "_" + imageIdentifier;
        }

        return validateScreenshot(fileName, errorTolerance);
    }

    /**
     * Capture a screenshot of the canvas and compare it to a reference image
     * 
     * @param referenceFileId
     *            The filename for the screenshot, without any extension
     * @param errorTolerance
     *            The tolerance to use in the comparison
     * @return
     */
    public boolean validateScreenshot(String referenceFileId,
            double errorTolerance) {
        maxAmountOfTests = Parameters.getMaxRetries();

        boolean result = false;

        // Add longer pause for reference screenshot!
        if (!ImageFileUtil.getReferenceScreenshotFile(referenceFileId + ".png")
                .exists()) {
            captureReferenceImage(referenceFileId);
            return true;
        }

        // Small pause to give components a bit of render time
        // pause(screenshotPause);
        // doCommand("waitForVaadin", new String[] { "", "" });

        // Get the dimensions of the browser window and canvas
        BrowserDimensions dimensions = getBrowserAndCanvasDimensions();

        String compareScreenResult = doCommand(
                "compareScreen",
                new String[] {
                        compare.generateBlocksFromReferenceFile(referenceFileId),
                        String.valueOf(errorTolerance * 768),
                        String.valueOf(maxAmountOfTests),
                        String.valueOf(dimensions.getCanvasXPosition()),
                        String.valueOf(dimensions.getCanvasYPosition()),
                        String.valueOf(dimensions.getCanvasWidth()),
                        String.valueOf(dimensions.getCanvasHeight()),
                        String.valueOf(Parameters.getRetryDelay()) });

        String[] compareScreenResults = compareScreenResult.split(",");
        if (compareScreenResults.length == 2) {
            if (Parameters.isDebug()) {
                System.err.println("RC retried screen shot "
                        + compareScreenResults[1] + " time(s)");
            }
            result = true;
        } else {
            if (Parameters.isDebug()) {
                System.err.println("RC retried screen shot "
                        + compareScreenResults[1] + " time(s)");
            }
            // Get the screen shot, which has been passed in the result
            String screenshotAsBase64String = compareScreenResults[2];

            try {
                // Compare screenshot with saved reference screen
                result = compare.compareStringImage(screenshotAsBase64String,
                        referenceFileId, errorTolerance, dimensions);
            } catch (junit.framework.AssertionFailedError e) {
                // If a Assert.fail("") is caught check if it's a missing
                // reference.
                // If other throw the AssertionFailedError.
                if (e.getMessage().contains("No reference found")) {
                    softAssert.add(e);
                    result = true;
                } else if (e.getMessage().contains(
                        "differs from reference image")) {

                    // Build error screenshot directory.
                    String directory = Parameters.getScreenshotDirectory();

                    if (!File.separator.equals(directory.charAt(directory
                            .length() - 1))) {
                        directory = directory + File.separator;
                    }
                    directory = directory + File.separator + "errors"
                            + File.separator;

                    // Do a Roberts Cross edge detection on images and compare
                    // for diff. Should remove some small faults.
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
        }
        return result;
    }

    protected void captureReferenceImage(String referenceFileId) {
        pause(screenshotPause);
        BufferedImage capturedImage = captureCanvas();

        BufferedImage imageCopy;

        // Loop until two consequtive screenshots are an exact match
        do {
            imageCopy = ImageUtil.duplicateImage(capturedImage);
            pause(screenshotPause);
            capturedImage = captureCanvas();
        } while (!compare.compareImages(capturedImage, imageCopy, 0.0));

        // Check that the comparison folder exists and create if
        // needed
        ImageFileUtil.createScreenshotDirectoriesIfNeeded();

        try {
            ImageIO.write(
                    capturedImage,
                    "png",
                    ImageFileUtil.getErrorScreenshotFile(referenceFileId
                            + ".png"));
            softAssert.add(new junit.framework.AssertionFailedError(
                    "No reference found for " + referenceFileId));
        } catch (IOException ioe) {
            // FIXME: Report error
        }
    }

    /**
     * Capture a screenshot of the canvas
     * 
     * @return BufferedImage with an screenshot of the browser canvas
     */
    private BufferedImage captureCanvas() {
        BufferedImage image = ImageUtil.stringToImage(captureScreenshot());
        ImageUtil.cropImage(image, getBrowserAndCanvasDimensions());

        return image;
    }

    private String captureScreenshot() {
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

    protected BrowserDimensions setRequestedCanvasSize() {
        String dimensions = doCommand(
                "setCanvasSize",
                new String[] { String.valueOf(requestedCanvasWidth),
                        String.valueOf(requestedCanvasHeight) });
        if (dimensions.startsWith("OK")) {
            return new BrowserDimensions(dimensions.substring(3));
        }
        return null;
    }

    public void clearDimensions() {
        browserDimensions = null;
    }

    protected BrowserDimensions getBrowserAndCanvasDimensions() {
        return getBrowserDimensions(false);
    }

    public BrowserDimensions getBrowserDimensions(boolean force) {
        if (force || browserDimensions == null) {
            if (Parameters.isDebug()) {
                System.err.println("Calculating browser dimensions..");
            }
            String dimensions = doCommand("getCanvasSize", new String[] {});
            if (dimensions.startsWith("OK")) {
                browserDimensions = new BrowserDimensions(
                        dimensions.substring(3));
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
    public void startBrowser(String browserIdentifier) throws Exception {
        this.browserIdentifier = browserIdentifier;

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

    /**
     * Sets up the test case. Uses system properties to determine test hosts,
     * deployment host and browser. Must be called before each test.
     * 
     * @deprecated Use startBrowser(String) instead
     */
    @Override
    @Deprecated
    public void setUp() throws Exception {

        if (browserIdentifier == null) {
            throw new IllegalArgumentException(
                    "Missing browser definition. Define using setBrowserIdentifier().");
        }

        startBrowser(browserIdentifier);
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

        // if (getBrowserVersion().isOpera()) {
        /*
         * Opera (10.62 and older at least) does not support window.resizeTo or
         * window.moveTo so we cannot move/resize the window but have to accept
         * whatever the browser uses (overrideable in profile-opera.txt).
         */
        // } else
        if (hasRequestedCanvasSize()) {
            // The user has specified a canvas size to use so we should
            // initialize to that size
            BrowserDimensions dim = setRequestedCanvasSize();
            if (dim != null) {
                browserDimensions = dim;
            }
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
        if (!hasRequestedCanvasSize()) {
            // A canvas size request automatically updates the browser
            // dimensions
            getBrowserDimensions(true);
        }

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

    public void open(String url) {
        try {
            doCommand("open", new String[] { url });
        } catch (Exception e) {
            System.out.println("Open failed, retrying");
            selenium.stop();
            selenium.start();
            clearDimensions();
            setupWindow(true);
            doCommand("open", new String[] { url });
        }
        waitForVaadin();
    }

    public void createFailureScreenshot(String testName) {
        String statusScreen = selenium.captureScreenshotToString();
        String directory = getScreenshotDirectory();
        if (!File.separator.equals(directory.charAt(directory.length() - 1))) {
            directory = directory + File.separator;
        }
        File target = new File(directory + "errors");
        if (!target.exists()) {
            target.mkdir();
        }
        try {
            ImageIO.write(
                    ImageUtil.stringToImage(statusScreen),
                    "png",
                    new File(directory
                            + "errors/"
                            + testName
                            + "_failure_"
                            + getBrowserIdentifier().replaceAll("[^a-zA-Z0-9]",
                                    "_") + ".png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void handleSoftErrors() {
        if (getSoftErrors().isEmpty()) {
            return;
        }

        StringBuilder message = new StringBuilder();
        byte[] errors = new byte[5];

        for (junit.framework.AssertionFailedError afe : getSoftErrors()) {
            if (afe.getMessage().contains("No reference found")) {
                errors[0] = 1;
            } else if (afe.getMessage()
                    .contains("differs from reference image")) {
                errors[1] = 1;
            } else if (afe.getMessage().contains("Images differ and")) {
                errors[2] = 1;
            } else if (afe.getMessage()
                    .contains("Images are of different size")) {
                errors[3] = 1;
            } else {
                errors[4] = 1;
                error_messages[4] = afe.getMessage();
            }
        }

        boolean add_and = false;
        message.append("Test ");

        for (int i = 0; i < 5; i++) {
            if (errors[i] == 1) {
                if (add_and) {
                    message.append(" and ");
                }
                message.append(error_messages[i]);
                add_and = true;
            }
        }
        junit.framework.Assert.fail(message.toString());
    }

    protected void setTestName(String testName) {
        testBase.doCommand("setTestName", new String[] { testName });
    }

    public void doUploadFile(String locator, String filename) {
        try {
            File file = new File(filename);
            FileInputStream fin = new FileInputStream(file);
            String fileData = new String(Base64.encodeBase64(IOUtils
                    .toByteArray(fin)));

            testBase.doCommand("uploadFile", new String[] { locator, filename,
                    fileData });
        } catch (FileNotFoundException e) {
            Assert.fail("File " + filename + " does not exist!");
            e.printStackTrace();
        } catch (IOException e) {
            Assert.fail("Error while reading file " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Returns the name (host name or IP address) of the remote control where
     * the test is being run.
     * 
     * @return Host name or IP of the remote control host
     */
    protected String getRemoteControlName() {
        String retVal = doCommand("getRemoteControlName", new String[] {});
        if (retVal.startsWith("OK")) {
            // "OK,hostname"
            return retVal.substring(3);
        }
        return "?";
    }

}
