package com.vaadin.testbench.util;

import com.thoughtworks.selenium.Selenium;

public class BrowserUtil {

    /**
     * Gets or calculates the x position of the canvas in the upper left corner
     * on screen
     * 
     * @param selenium
     * @return
     */
    public int canvasXPosition(Selenium selenium) {
        try {
            int result = ((Integer.parseInt(selenium
                    .getEval("screen.availWidth;")) - Integer.parseInt(selenium
                    .getEval("window.innerWidth;"))) / 2)
                    + Integer.parseInt(selenium.getEval("window.pageXOffset;"));
            return result;
        } catch (NumberFormatException nfe) {
            return Integer.parseInt(selenium
                    .getEval("this.browserbot.getUserWindow().screenLeft;"));
        }
    }

    /**
     * Gets or calculates the y position of the canvas in the upper left corner
     * on screen
     * 
     * @param selenium
     * @return
     */
    public int canvasYPosition(Selenium selenium) {
        try {
            int result = (Integer.parseInt(selenium
                    .getEval("screen.availHeight"))
                    - getCanvasHeight(selenium)
                    + Integer.parseInt(selenium.getEval("window.pageYOffset")) - 30);
            return result;

        } catch (NumberFormatException nfe) {
            return Integer.parseInt(selenium
                    .getEval("this.browserbot.getUserWindow().screenTop;"));
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
    public String browserVersion(Selenium selenium) {
        String result = "";
        String fullVersion = "";
        String userAgent = selenium.getEval("navigator.userAgent;");
        if (userAgent.contains("MSIE")) {
            result = "InternetExplorer_";
            fullVersion = userAgent.substring(userAgent.indexOf("MSIE") + 5);
        } else if (userAgent.contains("Opera")) {
            result = "Opera_";
            fullVersion = userAgent.substring(userAgent.indexOf("Opera") + 6);
        } else if (userAgent.contains("Chrome")) {
            result = "Chrome_";
            fullVersion = userAgent.substring(userAgent.indexOf("Chrome") + 7);
        } else if (userAgent.contains("Safari")) {
            result = "Safari_";
            fullVersion = userAgent.substring(userAgent.indexOf("Safari") + 7);
        } else if (userAgent.contains("Firefox")) {
            result = "Firefox_";
            fullVersion = userAgent.substring(userAgent.indexOf("Firefox") + 8);
        } else if (userAgent.lastIndexOf(' ') + 1 < userAgent.lastIndexOf('/')) {
            result = userAgent.substring(userAgent.lastIndexOf(' ') + 1,
                    userAgent.lastIndexOf('/'))
                    + "_";
            fullVersion = userAgent.substring(userAgent.lastIndexOf('/') + 1);
        }

        if (fullVersion.indexOf(";") != -1) {
            fullVersion = fullVersion.substring(0, fullVersion.indexOf(";"));
        }
        if (fullVersion.indexOf(" ") != -1) {
            fullVersion = fullVersion.substring(0, fullVersion.indexOf(" "));
        }

        if (fullVersion.indexOf(".") != -1) {
            result = result
                    + fullVersion.substring(0, fullVersion.indexOf("."));
        } else {
            result = result + fullVersion;
        }

        return result;
    }
}
