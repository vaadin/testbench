package com.vaadin.testbench.util;

import com.thoughtworks.selenium.Selenium;

public class BrowserUtil {

    public int canvasXPosition(Selenium selenium) {
        try {
            int result = (Integer.parseInt(selenium
                    .getEval("screen.availWidth;")) - Integer.parseInt(selenium
                    .getEval("window.innerWidth;"))) / 2;
            return result;
        } catch (NumberFormatException nfe) {
            return Integer.parseInt(selenium
                    .getEval("this.browserbot.getUserWindow().screenLeft;"));
        }
    }

    public int canvasYPosition(Selenium selenium) {
        try {
            int result = (Integer.parseInt(selenium
                    .getEval("screen.availHeight"))
                    - getCanvasHeight(selenium) - 30);
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
                }
            }
        }
        return canvasWidth;
    }

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

        // + "var fullVersion = ''+parseFloat(navigator.appVersion);"
        // + "var nameOffset, verOffset, ix;"
        // + "if((verOffset=nAgt.indexOf(\"MSIE\")) != -1){"
        // + "browserName = \"Internet_Explorer\";"
        // + "fullVersion = nAgt.substring(verOffset+5);"
        // + "}else if((verOffset=nAgt.indexOf(\"Opera\")) !=1){"
        // + "browserName = \"Opera\";"
        // + "fullVersion = nAgt.substring(verOffset+6);"
        // + "}else if((verOffset=nAgt.indexOf(\"Chrome\")) != 1){"
        // + "browserName = \"Chrome\";"
        // + "fullVersion = nAgt.substring(verOffset+7);"
        // + "}else if((verOffset=nAgt.indexOf(\"Safari\")) != 1){"
        // + "browserName = \"Safari\";"
        // + "fullVersion = nAgt.substring(verOffset+7);"
        // + "}else if((verOffset=nAgt.indexOf(\"Firefox\")) != 1){"
        // + "browserName = \"Firefox\";"
        // + "fullVersion = nAgt.substring(verOffset+8);"
        // +
        // "}else if((nameOffset=nAgt.lastIndexOf(' ')+1) < (verOffset=nAgt.lastIndexOf('/')){"
        // + "browserName = nAgt.substring(nameOffset,verOffset);"
        // + "fullVersion = nAgt.substring(verOffset+1);"
        // + "if(browserName.toLowerCase() == browserName.toUpperCase()){"
        // + "browserName = navigator.appName;"
        // + "}"
        // + "}"
        // +
        // "if((ix=fullVersion.indexOf(\";\") != -1) fullVersion = fullVersion.substring(0,ix);"
        // +
        // "if((ix=fullVersion.indexOf(\" \") != -1) fullVersion = fullVersion.substring(0,ix);"
        // + "return browserName+\"_\"+fullVersion" + "}");
        return result;
    }
}
