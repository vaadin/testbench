package com.vaadin.testbench.util;

import com.thoughtworks.selenium.Selenium;

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
     * @return
     */
    public static int canvasYPosition(Selenium selenium, BrowserVersion browser) {
        if (browser.isIE()) {
            try {
                return Integer.parseInt(selenium
                        .getEval("this.browserbot.getUserWindow().screenTop;"));
            } catch (Exception e) {
            }
        }

        try {
            int screenHeight = Integer.parseInt(selenium
                    .getEval("screen.availHeight;"));
            int canvasHeight = getCanvasHeight(selenium);

            return screenHeight - canvasHeight;
        } catch (Exception e) {
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
