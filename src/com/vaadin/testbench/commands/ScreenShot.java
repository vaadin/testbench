/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.commands;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A class that captures screen shots
 * 
 * @author Vaadin Ltd
 */
public class ScreenShot {

    private final int deviceIx;

    public ScreenShot(int deviceIx) {
        this.deviceIx = deviceIx;
    }

    /**
     * @return the number of attached screen devices.
     */
    public static int getNumScreenDevices() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getScreenDevices().length;
    }

    /**
     * Captures a screen shot of the entire screen on the specified screen
     * device.
     * 
     * @param ix
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public static BufferedImage capture(int ix) throws InterruptedException,
            ExecutionException, TimeoutException {
        return new ScreenShot(ix).capture();
    }

    /**
     * Captures a screen shot of the entire screen.
     * 
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public BufferedImage capture() throws InterruptedException,
            ExecutionException, TimeoutException {
        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices()[deviceIx];
        return capture(0, 0, device.getDisplayMode().getWidth(), device
                .getDisplayMode().getHeight());
    }

    /**
     * Captures a screen shot of the specified size starting at (0,0)
     * 
     * @param width
     * @param height
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public BufferedImage capture(int width, int height)
            throws InterruptedException, ExecutionException, TimeoutException {
        return capture(0, 0, width, height);
    }

    /**
     * Captures a screen shot of the specified region of the screen.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public BufferedImage capture(int x, int y, int width, int height)
            throws InterruptedException, ExecutionException, TimeoutException {
        Robot robot = getRobot();
        robot.mouseMove(0, 0);
        return robot.createScreenCapture(new Rectangle(x, y, width, height));
    }

    private Robot getRobot() throws InterruptedException, ExecutionException,
            TimeoutException {
        GraphicsDevice[] devices = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getScreenDevices();
        return RobotRetriever.getRobot(devices[deviceIx]);
    }

    /**
     * A specialized version of org.openqa.selenium.server.RobotRetriever that
     * can return a robot capable of grabbing screen shots of any specified
     * display.
     */
    private static class RobotRetriever {

        private static final Log LOGGER = LogFactory
                .getLog(RobotRetriever.class);
        private static HashMap<GraphicsDevice, Robot> robots = new HashMap<GraphicsDevice, Robot>();

        private static class Retriever implements Callable<Robot> {

            private final GraphicsDevice device;

            public Retriever(GraphicsDevice device) {
                this.device = device;
            }

            public Robot call() throws Exception {
                return new Robot(device);
            }

        }

        public static synchronized Robot getRobot(GraphicsDevice device)
                throws InterruptedException, ExecutionException,
                TimeoutException {
            final FutureTask<Robot> robotRetriever;
            final Thread retrieverThread;

            if (!robots.containsKey(device)) {
                robotRetriever = new FutureTask<Robot>(new Retriever(device));
                LOGGER.info("Creating Robot");
                retrieverThread = new Thread(robotRetriever, "robotRetriever");
                retrieverThread.start();
                robots.put(device, robotRetriever.get(10, TimeUnit.SECONDS));
            }

            return robots.get(device);
        }

    }

}
