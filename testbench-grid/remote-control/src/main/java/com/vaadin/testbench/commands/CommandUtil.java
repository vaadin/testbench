package com.vaadin.testbench.commands;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.FrameGroupCommandQueueSet;
import org.openqa.selenium.server.RobotRetriever;

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

    public static void findCanvasPositionByScreenshot(
            BrowserDimensions dimensions) throws InterruptedException,
            ExecutionException, TimeoutException {
        BufferedImage screenshot = captureScreenshot(
                dimensions.getScreenWidth(), dimensions.getScreenHeight());
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

    private static BufferedImage captureScreenshot(int width, int height)
            throws InterruptedException, ExecutionException, TimeoutException {
        return RobotRetriever.getRobot().createScreenCapture(
                new Rectangle(width, height));
    }

    public static void pause(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
        }
    }
}
