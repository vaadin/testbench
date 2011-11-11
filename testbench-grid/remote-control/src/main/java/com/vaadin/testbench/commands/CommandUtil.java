package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.FrameGroupCommandQueueSet;

import com.vaadin.testbench.util.BrowserDimensions;

public class CommandUtil {

    private static final Log LOGGER = LogFactory.getLog(CommandUtil.class);
    private static final int MIN_LINES_FOR_MATCH = 10;

    public static String eval(String cmd, String sessionId) {
        final FrameGroupCommandQueueSet queue;
        final String response;

        LOGGER.debug("Executing '" + cmd
                + "' selenium core command on session " + sessionId);
        try {
            LOGGER.debug("Session " + sessionId
                    + " going to doCommand('getEval', '" + cmd + "')");
            queue = FrameGroupCommandQueueSet.getQueueSet(sessionId);
            response = queue.doCommand("getEval", cmd, "");
            LOGGER.debug("Got result: " + response + " on session " + sessionId);

            return response;
        } catch (Exception e) {
            LOGGER.error("Exception running '" + cmd + " 'command on session "
                    + sessionId, e);
            return "ERROR Server Exception: " + e.getMessage();
        }
    }

    public static void pause(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Finds the physical display where the browser window was opened. This is
     * done by first grabbing a screen shot of every attached screen and then
     * changing the background color of the browser window to red. After this, a
     * new screen shot is grabbed of each screen in turn until the shot where a
     * large area of changed color is found.
     * 
     * @return the index of the display in the GraphicsEnvironment's list of
     *         GraphicsDevices.
     * @throws TimeoutException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static int findPhysicalDisplay(String sessionId)
            throws InterruptedException, ExecutionException, TimeoutException {
        int numDevices = ScreenShot.getNumScreenDevices();
        if (numDevices == 1) {
            return 0;
        }
        eval("selenium.browserbot.getUserWindow().document.body.bgColor='red';",
                sessionId);
        pause(500);
        BufferedImage[] references = new BufferedImage[numDevices];
        for (int ix = 0; ix < numDevices; ix++) {
            references[ix] = ScreenShot.capture(ix);
        }
        eval("selenium.browserbot.getUserWindow().document.body.bgColor='white';",
                sessionId);
        pause(500);
        for (int ix = 0; ix < numDevices; ix++) {
            BufferedImage shot = ScreenShot.capture(ix);
            if (isBrowserWindowPresent(shot, references[ix])) {
                return ix;
            }
        }
        // Couldn't find the correct one, just use the primary display.
        return 0;
    }

    /**
     * @param shot
     * @param reference
     * @return true if the browser window has been detected in the screen shot
     *         by swapping its background color from red (reference) to white
     *         (shot)
     */
    static boolean isBrowserWindowPresent(BufferedImage shot,
            BufferedImage reference) {
        int x = 0;
        int w = shot.getWidth() / 2;

        for (int y = 0; y < shot.getHeight(); y += 10) {
            int[] shotBlock = new int[w];
            int[] refBlock = new int[w];
            shotBlock = shot.getRGB(x, y, w, 1, shotBlock, 0, w);
            refBlock = reference.getRGB(x, y, w, 1, refBlock, 0, w);
            if (blockMostly(refBlock, 0xFF0000)
                    && blockMostly(shotBlock, 0xFFFFFF)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param refBlock
     * @param i
     * @return true if refBlock mostly contains the specified rgb color.
     */
    private static boolean blockMostly(int[] refBlock, int rgb) {
        return blockMostly(refBlock, rgb, 100);
    }

    /**
     * @param refBlock
     * @param i
     * @param tolerance
     * @return true if refBlock mostly contains the specified rgb color.
     */
    private static boolean blockMostly(int[] refBlock, int rgb, int tolerance) {
        float r = 0, g = 0, b = 0;
        float len = refBlock.length;
        for (int pixel : refBlock) {
            r = r + ((pixel >> 16) & 0xFF) / len;
            g = g + ((pixel >> 8) & 0xFF) / len;
            b = b + (pixel & 0xFF) / len;
        }
        if ((r - ((rgb >> 16) & 0xFF)) > tolerance) {
            return false;
        }
        if ((g - ((rgb >> 8) & 0xFF)) > tolerance) {
            return false;
        }
        if ((b - (rgb & 0xFF)) > tolerance) {
            return false;
        }
        return true;
    }

    public static void findCanvasPositionByScreenshot(
            BrowserDimensions dimensions) throws InterruptedException,
            ExecutionException, TimeoutException {
        BufferedImage screenshot = ScreenShot.capture(dimensions
                .getDisplayIndex());
        CanvasPositionFinder finder = new CanvasPositionFinder(screenshot,
                dimensions.getCanvasWidth(), dimensions.getCanvasHeight());
        finder.find();
        dimensions.setCanvasXPosition(finder.getX());
        dimensions.setCanvasYPosition(finder.getY());
    }

    /**
     * Finds the position of a white canvas when it's width and height are
     * known.
     */
    public static class CanvasPositionFinder {

        private static final int MAX_CORNER_RADIUS = 5;
        private static final int MAX_DRAG_HANDLE_SIZE = 15;
        /**
         * The amount of non-white pixels allowed on a canvas (in the lower
         * corners) in order to still accept it as a solution.
         */
        private static final int NR_ALLOWED_NON_WHITE_PIXELS = ((MAX_DRAG_HANDLE_SIZE * MAX_DRAG_HANDLE_SIZE) / 2)
                + (MAX_CORNER_RADIUS * MAX_CORNER_RADIUS);

        private final BufferedImage image;
        private final int canvasWidth;
        private final int canvasHeight;
        private int[] pixels;
        private int x = -1;
        private int y = -1;

        public CanvasPositionFinder(BufferedImage image, int canvasWidth,
                int canvasHeight) {
            this.image = image;
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;

            if (image != null) {
                pixels = image.getRGB(0, 0, image.getWidth(),
                        image.getHeight(), null, 0, image.getWidth());
            }
        }

        /**
         * @return the x position of the located canvas
         */
        public int getX() {
            return x;
        }

        /**
         * @return the y position of the located canvas
         */
        public int getY() {
            return y;
        }

        /**
         * Knowing the width and height of the canvas, find it's top left corner
         * in an image. The canvas is all white, but might be obstructed by a
         * popup (e.g. the Flash updater).
         */
        public void find() {
            findPosition();
            throwExceptionUnlessCorrect();
        }

        private void findPosition() {
            int pixelRow = 0;

            while (pixelRow < image.getHeight()) {
                int startOfLine = findStartOfWhiteLineInPixelRow(pixelRow,
                        canvasWidth);
                if (isFound(startOfLine)) {
                    if (looksCorrect(startOfLine, pixelRow)) {
                        setSolutionCandidate(startOfLine, pixelRow);
                        return;
                    }
                }
                pixelRow++;
            }
        }

        private boolean looksCorrect(int left, int top) {
            int nrNonWhitePixels = 0;
            for (int y = top; y < top + canvasHeight; y++) {
                for (int x = left; x < left + canvasWidth; x++) {
                    int color = pixels[x + y * image.getWidth()];
                    if (!isWhite(color)) {
                        nrNonWhitePixels++;
                        // account for possible rounded corners and drag handles
                        boolean mightBeOk = pointInsideCornerRadiusArea(top,
                                left, x, y)
                                || pointInsideDragHandleArea(top, left, x, y);
                        if (!mightBeOk
                                && nrNonWhitePixels < NR_ALLOWED_NON_WHITE_PIXELS) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        /**
         * Returns true if the point at (x,y) might be part of a drag handle
         * (MAX_DRAG_HANDLE_SIZE x MAX_DRAG_HANDLE_SIZE (15x15) pixel part of
         * the window at the lower right corner)
         * 
         * @param top
         * @param left
         * @param x
         * @param y
         * @return
         */
        private boolean pointInsideDragHandleArea(int top, int left, int x,
                int y) {
            return (y > top + canvasHeight - MAX_DRAG_HANDLE_SIZE)
                    && (x > left + canvasWidth - MAX_DRAG_HANDLE_SIZE);
        }

        /**
         * Returns true if the point at (x,y) might be part of a rounded window
         * corner. This assumes a maximum corner radius of MAX_CORNER_RADIUS (5)
         * pixels.
         * 
         * @param top
         * @param left
         * @param x
         * @param y
         * @return
         */
        private boolean pointInsideCornerRadiusArea(int top, int left, int x,
                int y) {
            return (y > top + canvasHeight - MAX_CORNER_RADIUS)
                    && (x < left + MAX_CORNER_RADIUS || x > left + canvasWidth
                            - MAX_CORNER_RADIUS);
        }

        private boolean isFound(int startOfLine) {
            return startOfLine != -1;
        }

        private void setSolutionCandidate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int findStartOfWhiteLineInPixelRow(int y, int lineWidth) {
            int imgWidth = image.getWidth();
            int[] testBlock = new int[imgWidth];
            try {
                System.arraycopy(pixels, y * imgWidth, testBlock, 0, imgWidth);
                return findStartOfWhiteLine(testBlock, lineWidth);
            } catch (ArrayIndexOutOfBoundsException e) {
                return -1;
            }
        }

        int findStartOfWhiteLine(int[] pixels, int length) {
            int startIx = -1;
            for (int i = 0; i < pixels.length; i++) {
                if (startIx < 0) {
                    if (isWhite(pixels[i])) {
                        startIx = i;
                    }
                } else {
                    if (!isWhite(pixels[i])) {
                        if (i - startIx >= length) {
                            return startIx;
                        } else {
                            startIx = -1;
                        }
                    }
                }
            }
            if (length == pixels.length && startIx == 0) {
                // the entire row of pixels is white and we were asked for just
                // that
                return 0;
            }
            return -1;
        }

        private boolean isWhite(int pixel) {
            // Linux firefox has a default background color varying every second
            // pixel rgb(255,255,255) and every second rgb(254,254,254)
            return (pixel & 0xFFFFFF) == 0xFFFFFF
                    || (pixel & 0xFFFFFF) == 0xFEFEFE;
        }

        private void throwExceptionUnlessCorrect() {
            if (x == -1 && y == -1) {
                throw new CanvasObstructedException(
                        "Looks like the browser window might be obstructed by some other window or popup!");
            }
            int lastLineY = y + canvasHeight - 1;
            if (findStartOfWhiteLineInPixelRow(lastLineY, canvasWidth) == -1) {
                // Some windows have rounded corners, e.g. newer Firefox on osx
                if (!isLastLineOfPixelsOnRoundedCornerWindow(lastLineY)) {
                    throw new CanvasNotFoundException(
                            String.format(
                                    "Failed to find the correct coordinates of the canvas with dimensions %dx%d, an origin at %d,%d was deemed incorrect",
                                    canvasWidth, canvasHeight, x, y));
                }
            }
        }

        private boolean isLastLineOfPixelsOnRoundedCornerWindow(int lastLineY) {
            for (int cornerRadius = 1; cornerRadius <= 5; cornerRadius++) {
                if (lineIsFound(lastLineY, canvasWidth - 2 * cornerRadius)) {
                    return true;
                }
            }
            return false;
        }

        private boolean lineIsFound(int y, int lineWidth) {
            return isFound(findStartOfWhiteLineInPixelRow(y, lineWidth));
        }
    }
}
