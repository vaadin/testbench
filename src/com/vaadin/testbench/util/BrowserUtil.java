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
    public int canvasXPosition(Selenium selenium, BrowserVersion browser) {
        if (browser.isIE()) {
            try {
                // IE
                return Integer
                        .parseInt(selenium
                                .getEval("this.browserbot.getUserWindow().screenLeft;"));
            } catch (Exception e) {

            }
        }
        try {

            int screenWidth = Integer.parseInt(selenium
                    .getEval("screen.availWidth;"));
            int innerWidth = Integer.parseInt(selenium
                    .getEval("window.innerWidth;"));
            int pageXOffset = Integer.parseInt(selenium
                    .getEval("window.pageXOffset;"));
            int screenLeftOffset = (screenWidth - innerWidth) / 2;

            return screenLeftOffset + pageXOffset;
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
    public int canvasYPosition(Selenium selenium, BrowserVersion browser) {
        if (browser.isIE()) {
            try {
                return Integer.parseInt(selenium
                        .getEval("this.browserbot.getUserWindow().screenTop;"));
            } catch (Exception e) {
            }
        }

        try {
            int screenHeight = Integer.parseInt(selenium
                    .getEval("screen.availHeight"));
            int canvasHeight = getCanvasHeight(selenium);
            int statusbarGuess = getBrowserVersion(selenium)
                    .getStatusbarEstimate();
            int pageYOffset = Integer.parseInt(selenium
                    .getEval("window.pageYOffset"));

            return screenHeight - canvasHeight + pageYOffset - statusbarGuess;
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
    public int getCanvasHeight(Selenium selenium) {
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
    public int getCanvasWidth(Selenium selenium) {
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
    public BrowserVersion getBrowserVersion(Selenium selenium) {
        String userAgent = selenium.getEval("navigator.userAgent;");
        BrowserVersion bv = new BrowserVersion(userAgent);
        return bv;
    }
}
