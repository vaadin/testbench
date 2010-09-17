package com.vaadin.testbench.util;

import com.thoughtworks.selenium.Selenium;
import com.vaadin.testbench.Parameters;

public class BrowserUtil {

    /**
     * Gets or calculates the x position of the canvas in the upper left corner
     * on screen
     * 
     * @param selenium
     * @param browser
     * @return
     */
    public static int canvasXPosition(Selenium selenium, BrowserVersion browser) {
        String window = "this.browserbot.getUserWindow()";

        if (browser.isIE()) {
            try {
                // IE
                return Integer.parseInt(selenium.getEval(window
                        + ".screenLeft;"));
            } catch (Exception e) {

            }
        }
        try {
            // TODO Combine into one getEval
            int outerWidth = Integer.parseInt(selenium.getEval(window
                    + ".outerWidth"));
            int innerWidth = Integer.parseInt(selenium
                    .getEval("window.innerWidth;"));

            int horizontalDecorations = outerWidth - innerWidth;

            int screenXOffset = Integer.parseInt(selenium
                    .getEval("window.screenX;"));

            int screenLeftOffset = horizontalDecorations / 2 + screenXOffset;

            return screenLeftOffset;
        } catch (Exception e) {
            if (Parameters.isDebug()) {
                System.out
                        .println("Canvas X position got 0. " + e.getMessage());
            }
            // Probably bad guess but this should never be reached
            return 0;
        }
    }

    /**
     * Gets or calculates the y position of the canvas in the upper left corner
     * on screen
     * 
     * @param selenium
     * @param browser
     * @param canvasHeight
     * @return
     */
    public static int canvasYPosition(Selenium selenium,
            BrowserVersion browser, int canvasHeight) {
        if (browser.isIE()) {
            try {
                return Integer.parseInt(selenium
                        .getEval("this.browserbot.getUserWindow().screenTop;"));
            } catch (Exception e) {
            }
        }

        try {
            // We need to guess a location that is within the canvas. The window
            // is positioned at (0,0) or (1,1) at this point.

            // Using 0.95*canvasHeight we should always be inside the canvas.
            // 0.95 is used because the detection routine used later on also
            // checks some pixels below this position (for some weird reason).
            return (int) (canvasHeight * 0.95);
        } catch (Exception e) {
            if (Parameters.isDebug()) {
                System.out
                        .println("Canvas Y position got 0. " + e.getMessage());
            }
            // Really bad guess but this should never be reached
            return 0;
        }
    }

    /**
     * Get Canvas height for browser
     * 
     * @return Canvas height
     */
    public static int getCanvasHeight(Selenium selenium) {
        // TODO Combine into one getEval
        int canvasHeight = 0;
        try {
            canvasHeight = Integer.parseInt(selenium
                    .getEval("window.innerHeight;"));
        } catch (NumberFormatException nfe) {
            try {
                canvasHeight = Integer
                        .parseInt(selenium
                                .getEval("this.browserbot.getUserWindow().document.body.clientHeight;"));
            } catch (NumberFormatException nfe2) {
                try {
                    canvasHeight = Integer
                            .parseInt(selenium
                                    .getEval("this.browserbot.getUserWindow().document.documentElement.clientHeight;"));
                } catch (NumberFormatException nfe3) {
                    return 0;
                }
            }
        }
        return canvasHeight;
    }

    /**
     * Get canvas width for browser
     * 
     * @return Canvas width
     */
    public static int getCanvasWidth(Selenium selenium) {
        // TODO Combine into one getEval
        int canvasWidth = 0;
        try {
            canvasWidth = Integer.parseInt(selenium
                    .getEval("window.innerWidth;"));
        } catch (NumberFormatException nfe) {
            try {
                canvasWidth = Integer
                        .parseInt(selenium
                                .getEval("this.browserbot.getUserWindow().document.body.clientWidth;"));
            } catch (NumberFormatException nfe2) {
                try {
                    canvasWidth = Integer
                            .parseInt(selenium
                                    .getEval("this.browserbot.getUserWindow().document.documentElement.clientWidth;"));
                } catch (NumberFormatException nfe3) {
                    return 0;
                }
            }
        }
        return canvasWidth;
    }

    /**
     * Parses browser name and major version from user agent information
     * 
     * @param selenium
     * @return browserName_majorNumber
     */
    public static BrowserVersion getBrowserVersion(Selenium selenium) {
        String userAgent = selenium.getEval("navigator.userAgent;");
        BrowserVersion bv = new BrowserVersion(userAgent);
        return bv;
    }
}
