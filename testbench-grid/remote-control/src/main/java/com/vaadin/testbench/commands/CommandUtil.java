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
}
