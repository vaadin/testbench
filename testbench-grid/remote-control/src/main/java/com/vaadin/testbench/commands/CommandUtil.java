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

        private final BufferedImage image;
        private final int canvasWidth;
        private final int canvasHeight;
        private int x = -1;
        private int y = -1;

        public CanvasPositionFinder(BufferedImage image, int canvasWidth,
                int canvasHeight) {
            this.image = image;
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;
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
            int successiveLines = 0;
            int pixelRow = 0;

            while (pixelRow < image.getHeight()
                    && successiveLines < MIN_LINES_FOR_MATCH) {
                int startOfLine = findStartOfWhiteLineInPixelRow(pixelRow,
                        canvasWidth);
                if (isFound(startOfLine)) {
                    successiveLines++;
                    if (y == -1) {
                        setSolutionCandidate(startOfLine, pixelRow);
                    }
                } else if (y != -1) {
                    // Reset, since we didn't find enough successive lines
                    setSolutionCandidate(-1, -1);
                    successiveLines = 0;
                }
                pixelRow++;
            }
        }

        private boolean isFound(int startOfLine) {
            return startOfLine != -1;
        }

        private void setSolutionCandidate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        private int findStartOfWhiteLineInPixelRow(int y, int lineWidth) {
            int[] testBlock = new int[image.getWidth()];
            image.getRGB(0, y, image.getWidth(), 1, testBlock, 0, 1);
            return findStartOfWhiteLine(testBlock, lineWidth);
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
                        if (i - startIx == length) {
                            return startIx;
                        } else {
                            startIx = -1;
                        }
                    }
                }
            }
            return -1;
        }

        private boolean isWhite(int pixel) {
            return (pixel & 0xFFFFFF) == 0xFFFFFF;
        }

        private void throwExceptionUnlessCorrect() {
            int lastLineY = y + canvasHeight - 1;
            if (findStartOfWhiteLineInPixelRow(lastLineY, canvasWidth) == -1) {
                // Some windows have rounded corners, e.g. newer Firefox on osx
                if (!isLastLineOfPixelsOnRoundedCornerWindow(lastLineY)) {
                    throw new CanvasNotFoundException(
                            String.format(
                                    "Failed to find the correct coordinates of the canvas, %d,%d were not correct",
                                    x, y));
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
