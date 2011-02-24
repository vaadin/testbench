/**
 * 
 */
package com.vaadin.testbench.util;

public class BrowserVersion {
    private String browserName = "";
    private String fullVersion;
    private String majorVersion;
    private int majorVersionInt;
    private int minorVersionInt;
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
            if (userAgent.contains("Trident/4")) {
                versionString = "8";
            } else if (userAgent.contains("Trident/5")) {
                versionString = "9";
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
                versionString = userAgent.substring(
                        userAgent.indexOf("Version") + 8,
                        userAgent.indexOf("Safari"));
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
            versionString = versionString.substring(0,
                    versionString.indexOf(";"));
        }
        if (versionString.indexOf(" ") != -1) {
            versionString = versionString.substring(0,
                    versionString.indexOf(" "));
        }

        fullVersion = versionString;

        int dotPos = versionString.indexOf(".");
        if (dotPos != -1) {
            majorVersion = versionString.substring(0, dotPos);
            try {
                majorVersionInt = Integer.parseInt(majorVersion);
            } catch (Exception e) {
                majorVersionInt = -1;
            }
            try {
                minorVersionInt = Integer.parseInt(versionString
                        .substring(dotPos + 1));
            } catch (Exception e) {
                minorVersionInt = -1;
            }
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

    public boolean isFirefox() {
        return browserName.equals("Firefox");
    }

    public String getPlatform() {
        return platform;
    }

    /**
     * Checks if the major/minor version of the browser is older than the given
     * version (exclusive). Returns true if the current browser version is older
     * than the given.
     * 
     * @param oldMajorVersion
     * @param oldMinorVersion
     * @return true if the current browser version is older than the given,
     *         false otherwise
     */
    public boolean isOlderVersion(int oldMajorVersion, int oldMinorVersion) {
        if (majorVersionInt < oldMajorVersion) {
            return true;
        }

        if (majorVersionInt == oldMajorVersion
                && minorVersionInt < oldMinorVersion) {
            return true;
        }

        return false;
    }

    public boolean isLinux() {
        return platform.equals("Linux");
    }

    public boolean isWindows() {
        return platform.equals("Windows");
    }

    public boolean isMac() {
        return platform.equals("Mac");
    }
}