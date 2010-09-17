package com.vaadin.testbench.util;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import com.thoughtworks.selenium.Selenium;
import com.vaadin.testbench.Parameters;

/**
 * Class with browser dimensions
 * 
 */
public class BrowserDimensions {

    private static final String USER_WINDOW_JS = "this.browserbot.getUserWindow()";
    private static final String DOCUMENT_BODY_JS = USER_WINDOW_JS
            + ".document.body";

    private int screenWidth;
    private int screenHeight;
    private int canvasWidth;
    private int canvasHeight;
    private int canvasXPosition;
    private int canvasYPosition;

    public BrowserDimensions(int screenWidth, int screenHeight,
            int canvasWidth, int canvasHeight, int canvasXPosition,
            int canvasYPosition) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.canvasXPosition = canvasXPosition;
        this.canvasYPosition = canvasYPosition;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int width) {
        screenWidth = width;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int height) {
        screenHeight = height;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public int getCanvasXPosition() {
        return canvasXPosition;
    }

    public void setCanvasXPosition(int canvasXPosition) {
        this.canvasXPosition = canvasXPosition;
    }

    public int getCanvasYPosition() {
        return canvasYPosition;
    }

    public void setCanvasYPosition(int canvasYPosition) {
        this.canvasYPosition = canvasYPosition;
    }

    /**
     * Checks using javascript the size of the screen+canvas and the position of
     * the canvas on the screen.
     * 
     * @param browser
     * 
     * @param selenium
     * 
     * @return
     */
    public static BrowserDimensions getBrowserDimensions(
            BrowserVersion browser, Selenium selenium) {
        // Firefox on OSX has a problem with moveTo(0,0)
        if (browser.isMac() && browser.isFirefox()) {
            selenium.getEval("window.moveTo(0,1);");
        }
        // Get sizes for canvas cropping.

        // Hide scrollbar to get correct measurements in IE
        if (browser.isIE()) {
            selenium.getEval(hideScrollbarJS());
        }
        // int browserWindowWidth = Integer.parseInt(selenium
        // .getEval(getOuterWidthHeightJS() + "return outerWidth;"));
        int canvasWidth = BrowserUtil.getCanvasWidth(selenium);
        int canvasHeight = BrowserUtil.getCanvasHeight(selenium);
        int canvasXPosition = BrowserUtil.canvasXPosition(selenium, browser);
        int canvasYPosition = BrowserUtil.canvasYPosition(selenium, browser,
                canvasHeight);

        if (browser.isIE()) {
            // FIXME: Canvas position given by IE is 2px off
            canvasXPosition += 2;
            canvasYPosition += 2;
        }

        int screenWidth = Integer.parseInt(selenium
                .getEval("screen.availWidth;"));
        int screenHeight = Integer.parseInt(selenium
                .getEval("screen.availHeight;"));

        BrowserDimensions dimensions = new BrowserDimensions(screenWidth,
                screenHeight, canvasWidth, canvasHeight, canvasXPosition,
                canvasYPosition);

        if (!browser.isIE()) {
            // Only IE provides canvas position. For the other browsers we
            // locate it based on a screenshot.
            pause(200);
            String screenShot = selenium.captureScreenshotToString();
            findCanvasPosition(screenShot, dimensions);
        }

        return dimensions;
    }

    private static void findCanvasPosition(String image,
            BrowserDimensions dimensions) {

        /* Find out canvas y position */
        BufferedImage screenshot = ImageUtil.stringToImage(image);

        int[] startBlock = new int[10];
        int xPosition = dimensions.getCanvasXPosition()
                + dimensions.getCanvasWidth() / 2;
        startBlock = screenshot.getRGB(xPosition,
                dimensions.getCanvasYPosition() + 10, 1, 10, startBlock, 0, 1);

        for (int y = dimensions.getCanvasYPosition() + 10; y > 0; y--) {
            int[] testBlock = new int[10];
            testBlock = screenshot.getRGB(xPosition, y, 1, 10, testBlock, 0, 1);
            if (!Arrays.equals(startBlock, testBlock)) {
                dimensions.setCanvasYPosition(y + 1);
                break;
            }
        }

        int yPosition = dimensions.getCanvasYPosition() + 10;
        startBlock = screenshot.getRGB(xPosition, yPosition, 10, 1, startBlock,
                0, 1);

        for (int x = xPosition; x > 0; x--) {
            int[] testBlock = new int[10];
            testBlock = screenshot.getRGB(x, yPosition, 10, 1, testBlock, 0, 1);
            if (!Arrays.equals(startBlock, testBlock)) {
                dimensions.setCanvasXPosition(x + 1);
                break;
            }
        }

        // Print dimensions if debug
        if (Parameters.isDebug()) {
            System.out.println("availWidth: " + dimensions.getScreenWidth()
                    + "\navailHeight: " + dimensions.getScreenHeight()
                    + "\ncanvasWidth: " + dimensions.getCanvasWidth()
                    + "\ncanvasHeight: " + dimensions.getCanvasHeight()
                    + "\ncanvasX: " + dimensions.getCanvasXPosition()
                    + "\ncanvasY: " + dimensions.getCanvasYPosition());
        }
    }

    private static void pause(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
        }

    }

    private static String hideScrollbarJS() {
        return DOCUMENT_BODY_JS + ".style.overflow='hidden';";
    }

    /**
     * Resizes the browser window size so the canvas has the given width and
     * height.
     * <p>
     * Note: Does not work in Opera as Opera does not allow window.resize(w,h).
     * Opera is resized during startup using custom profiles.
     * </p>
     */
    public static boolean setCanvasSize(Selenium selenium,
            BrowserVersion browserVersion, int requestedCanvasWidth,
            int requestedCanvasHeight) {
        if (requestedCanvasHeight == -1 || requestedCanvasWidth == -1) {
            // No canvas size specified. Nothing to do.
            return false;
        }

        // Get the canvas size into variables innerWidth/innerHeight
        String getInnerWidthHeight = getInnerWidthHeightJS();

        // Update the window size so that the canvas size matches the requested
        String widthChange = requestedCanvasWidth + "-innerWidth";
        String heightChange = requestedCanvasHeight + "-innerHeight";
        String resizeBy = USER_WINDOW_JS + ".resizeBy(" + widthChange + ","
                + heightChange + ");";

        // Need to move browser to top left before resize to avoid the
        // possibility that it goes below or to the right of the screen.
        String moveTo = USER_WINDOW_JS + ".moveTo(1,1);";
        selenium.getEval(getInnerWidthHeight + moveTo + resizeBy);

        if (browserVersion.isLinux() && browserVersion.isChrome()) {
            // window.resizeTo() is pretty badly broken in Linux Chrome...

            // Need to wait for innerWidth to stabilize (Chrome issue #55409)
            pause(500);

            String getSize = getInnerWidthHeight
                    + "; innerWidth+','+innerHeight;";
            String[] newSizes = selenium.getEval(getSize).split(",");
            int newWidth = Integer.parseInt(newSizes[0]);
            int newHeight = Integer.parseInt(newSizes[1]);

            int widthError = requestedCanvasWidth - newWidth;
            int heightError = requestedCanvasHeight - newHeight;

            // Correct the window size
            // Need to resize by error*2 as
            String correctedWidth = USER_WINDOW_JS + ".outerWidth-"
                    + USER_WINDOW_JS + ".innerWidth+"
                    + (requestedCanvasWidth + widthError);
            String correctedHeight = USER_WINDOW_JS + ".outerHeight-"
                    + USER_WINDOW_JS + ".innerHeight+"
                    + (requestedCanvasHeight + heightError);
            String secondResize = USER_WINDOW_JS + ".resizeTo("
                    + correctedWidth + "," + correctedHeight + ");";
            selenium.getEval(secondResize);
        }

        return true;
    }

    private static String getInnerWidthHeightJS() {
        String window = USER_WINDOW_JS;
        String body = DOCUMENT_BODY_JS;

        String getInnerWidth = "var innerWidth = " + window + ".innerWidth;";
        String getInnerHeight = "var innerHeight = " + window + ".innerHeight;";
        // Hide main view scrollbar to get correct measurements in IE
        // (overflow=hidden)
        String IEInnerWidthHeight = ""
                + "if (typeof innerWidth == 'undefined') {" + hideScrollbarJS()
                + "innerWidth = " + body + ".clientWidth;" +

                "innerHeight = " + body + ".clientHeight;" +

                "}";

        return getInnerWidth + getInnerHeight + IEInnerWidthHeight;

    }

    @Override
    public String toString() {
        return "[BrowserDimensions, canvas: " + getCanvasWidth() + ","
                + getCanvasHeight() + "]";
    }
}
