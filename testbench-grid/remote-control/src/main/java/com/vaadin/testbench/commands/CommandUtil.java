package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.FrameGroupCommandQueueSet;

import com.vaadin.testbench.util.BrowserDimensions;

public class CommandUtil {
    private static final Log LOGGER = LogFactory.getLog(CommandUtil.class);

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
        BufferedImage[] references = new BufferedImage[numDevices];
        for (int ix = 0; ix < numDevices; ix++) {
            references[ix] = ScreenShot.capture(ix);
        }
        eval("selenium.browserbot.getUserWindow().document.body.bgColor='red';",
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
     *         by swapping its background color from white (reference) to red
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
            if (blockMostly(refBlock, 0xFFFFFF)
                    && blockMostly(shotBlock, 0xFF0000)) {
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
        int tolerance = 100;
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
        /* Find out canvas y position */
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
        /*
         * if (Parameters.isDebug()) { System.out.println("availWidth: " +
         * dimensions.getScreenWidth() + "\navailHeight: " +
         * dimensions.getScreenHeight() + "\ncanvasWidth: " +
         * dimensions.getCanvasWidth() + "\ncanvasHeight: " +
         * dimensions.getCanvasHeight() + "\ncanvasX: " +
         * dimensions.getCanvasXPosition() + "\ncanvasY: " +
         * dimensions.getCanvasYPosition()); }
         */
    }

    public static void pause(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
        }
    }
}
