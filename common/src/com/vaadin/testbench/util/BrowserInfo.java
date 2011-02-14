package com.vaadin.testbench.util;

import java.util.HashMap;

/**
 * The BrowserInfo class acts as a cache for information about browsers. It
 * caches the inner and outer dimensions of each browser with the userAgent
 * string as the key. The outer dimensions are cached in order to only calculate
 * them once for each browser during a test run.
 * 
 * @author Jonatan Kronqvist / Vaadin
 */
public class BrowserInfo {

    /**
     * A simple data class that holds a width and a height.
     * 
     * @author Jonatan Kronqvist / Vaadin
     */
    public static class SimpleDimensions {
        private int width;
        private int height;

        public SimpleDimensions(int width, int height) {
            setWidth(width);
            setHeight(height);
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }
    }

    /**
     * Cache for inner dimensions and offsets as well as screen size.
     */
    private static HashMap<String, BrowserDimensions> userAgentToDimensions = new HashMap<String, BrowserDimensions>();

    /**
     * Cache for outer width / height
     */
    private static HashMap<String, HashMap<String, SimpleDimensions>> userAgentToOuterDimensions = new HashMap<String, HashMap<String, SimpleDimensions>>();

    /**
     * Sets the outer dimensions
     * 
     * @param userAgent
     *            the user agent string
     * @param canvasWidth
     *            the inner width of the browser
     * @param canvasHeight
     *            the inner height of the browser
     * @param width
     *            the outer width of the browser
     * @param height
     *            the outer height of the browser
     */
    public static void setOuterDimensions(String userAgent, int canvasWidth,
            int canvasHeight, int width, int height) {
        if (userAgentToOuterDimensions.get(userAgent) == null) {
            userAgentToOuterDimensions.put(userAgent,
                    new HashMap<String, SimpleDimensions>());
        }
        userAgentToOuterDimensions.get(userAgent).put(
                genCanvasKey(canvasWidth, canvasHeight),
                new SimpleDimensions(width, height));
    }

    private static String genCanvasKey(int canvasWidth, int canvasHeight) {
        return "" + canvasWidth + "," + canvasHeight;
    }

    /**
     * Returns the outer width of the browser with the specified user agent
     * string.
     * 
     * @param userAgent
     *            the user agent string representing the browser
     * @param canvasWidth
     *            the inner width of the browser
     * @param canvasHeight
     *            the inner height of the browser
     * @return the outer width of the browser.
     */
    public static int getOuterWidth(String userAgent, int canvasWidth,
            int canvasHeight) {
        if (userAgentToOuterDimensions.get(userAgent) != null) {
            SimpleDimensions dim = userAgentToOuterDimensions.get(userAgent)
                    .get(genCanvasKey(canvasWidth, canvasHeight));
            if (dim != null) {
                return dim.getWidth();
            }
        }
        return -1;
    }

    /**
     * Returns the outer height of the browser with the specified user agent
     * string.
     * 
     * @param userAgent
     *            the user agent string representing the browser
     * @param canvasWidth
     *            the inner width of the browser
     * @param canvasHeight
     *            the inner height of the browser
     * @return the outer height of the browser
     */
    public static int getOuterHeight(String userAgent, int canvasWidth,
            int canvasHeight) {
        if (userAgentToOuterDimensions.get(userAgent) != null) {
            SimpleDimensions dim = userAgentToOuterDimensions.get(userAgent)
                    .get(genCanvasKey(canvasWidth, canvasHeight));
            if (dim != null) {
                return dim.getHeight();
            }
        }
        return -1;
    }

    /**
     * Stores the browser dimensions in the cache.
     * 
     * @param userAgent
     *            the user agent string representing the browser
     * @param dims
     *            the browser dimensions
     */
    public static void setBrowserDimensions(String userAgent,
            BrowserDimensions dims) {
        userAgentToDimensions.put(userAgent, dims);
    }

    /**
     * Retrieves the browser dimensions from the cache.
     * 
     * @param userAgent
     *            the user agent string representing the browser for which to
     *            retrieve the dimensions.
     * @return the browser dimensions or null if none cached.
     */
    public static BrowserDimensions getBrowserDimensions(String userAgent) {
        return userAgentToDimensions.get(userAgent);
    }
}
