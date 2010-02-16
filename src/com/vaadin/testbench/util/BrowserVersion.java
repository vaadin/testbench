/**
 * 
 */
package com.vaadin.testbench.util;

public class BrowserVersion {
    private String browserName = "";
    private String fullVersion;
    private String majorVersion;
    private String platform = "Other";

    public BrowserVersion(String userAgent) {
        setFromUserAgent(userAgent);
    }

    public int getStatusbarEstimate() {
        if (browserName.equals("Chrome")) {
            return 0;
        } else if (browserName.equals("Firefox")) {
            return 26;
        } else if (browserName.equals("Safari")) {
            return 26;
        } else if (browserName.equals("Opera")) {
            return 19;
        } else if (browserName.equals("Chrome")) {
            return 2;
        } else {
            return 30;
        }
    }

    private void setFromUserAgent(String userAgent) {
        browserName = "";
        String versionString = "";

        if (userAgent.contains("MSIE")) {
            browserName = "InternetExplorer";
            if (userAgent.contains("Trident")) {
                versionString = "8";
            } else {
                versionString = userAgent
                        .substring(userAgent.indexOf("MSIE") + 5);
            }
        } else if (userAgent.contains("Opera")) {
            browserName = "Opera";
            if (userAgent.contains("Version")) {
                versionString = userAgent.substring(userAgent
                        .indexOf("Version") + 8);
            } else {
                versionString = userAgent
                        .substring(userAgent.indexOf("Opera") + 6);
            }
        } else if (userAgent.contains("Chrome")) {
            browserName = "Chrome";
            versionString = userAgent
                    .substring(userAgent.indexOf("Chrome") + 7);
        } else if (userAgent.contains("Safari")) {
            browserName = "Safari";
            if (userAgent.contains("Version")) {
                versionString = userAgent.substring(userAgent
                        .indexOf("Version") + 8, userAgent.indexOf("Safari"));
            } else {
                versionString = userAgent
                        .substring(userAgent.indexOf("Safari") + 7);
            }
        } else if (userAgent.contains("Firefox")) {
            browserName = "Firefox";
            versionString = userAgent
                    .substring(userAgent.indexOf("Firefox") + 8);
        } else if (userAgent.lastIndexOf(' ') + 1 < userAgent.lastIndexOf('/')) {
            browserName = userAgent.substring(userAgent.lastIndexOf(' ') + 1,
                    userAgent.lastIndexOf('/'));
            versionString = userAgent.substring(userAgent.lastIndexOf('/') + 1);
        }

        if (versionString.indexOf(";") != -1) {
            versionString = versionString.substring(0, versionString
                    .indexOf(";"));
        }
        if (versionString.indexOf(" ") != -1) {
            versionString = versionString.substring(0, versionString
                    .indexOf(" "));
        }

        fullVersion = versionString;

        if (versionString.indexOf(".") != -1) {
            majorVersion = versionString.substring(0, versionString
                    .indexOf("."));
        } else {
            majorVersion = fullVersion;
        }

        if (userAgent.contains("Windows")) {
            platform = "Windows";
        } else if (userAgent.contains("Macintosh")) {
            platform = "Mac";
        } else if (userAgent.contains("Linux")) {
            platform = "Linux";
        }

    }

    public String getIdentifier() {
        return browserName + "_" + majorVersion;
    }

    public boolean isIE() {
        return browserName.equals("InternetExplorer");
    }

    public boolean isSafari() {
        return browserName.equals("Safari");
    }

    public boolean isChrome() {
        return browserName.equals("Chrome");
    }

    public boolean isOpera() {
        return browserName.equals("Opera");
    }

    public String getPlatform() {
        return platform;
    }
}