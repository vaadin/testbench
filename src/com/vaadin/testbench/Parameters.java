package com.vaadin.testbench;

public class Parameters {

    private static final String BASE_PACKAGE = "com.vaadin.testbench.";
    private static final String SCREENSHOT_PACKAGE = BASE_PACKAGE
            + "screenshot.";

    // TODO Move all parameters constants here

    private static final String DEBUG = BASE_PACKAGE + "debug";

    // Screenshot related parameters
    public static final String SCREENSHOT_DIRECTORY = SCREENSHOT_PACKAGE
            + "directory";
    private static final String CAPTURE_SCREENSHOT_ON_FAILURE = SCREENSHOT_PACKAGE
            + ".onfail";
    public static final String SCREENSHOT_SOFT_FAIL = BASE_PACKAGE
            + "screenshot.softfail";
    public static final String SCREENSHOT_REFERENCE_DEBUG = SCREENSHOT_PACKAGE
            + "reference.debug";
    public static final String SCREENSHOT_RESOLUTION = SCREENSHOT_PACKAGE
            + "resolution";
    public static final String SCREENSHOT_COMPARISON_TOLERANCE = SCREENSHOT_PACKAGE
            + "block.error";
    public static final String SCREENSHOT_COMPARISON_CURSOR_DETECTION = SCREENSHOT_PACKAGE
            + "cursor";

    // Other parameters
    public static final String BROWSER_STRING = BASE_PACKAGE + "browsers";
    public static final String REMOTE_CONTROL_HOST_NAME = BASE_PACKAGE
            + "tester.host";
    public static final String DEPLOYMENT_URL = BASE_PACKAGE + "deployment.url";
    private static final String TEST_FILE_ENCODING = BASE_PACKAGE + "encoding";
    private static final String PARAMETER_FILE = BASE_PACKAGE
            + "converter.parameterFile";

    public static boolean isDebug() {
        return ("true".equalsIgnoreCase(System.getProperty(DEBUG)));
    }

    public static boolean isScreenshotSoftFail() {
        return ("true".equals(System.getProperty(SCREENSHOT_SOFT_FAIL)));
    }

    public static boolean isScreenshotComparisonCursorDetection() {
        return ("true".equals(System
                .getProperty(SCREENSHOT_COMPARISON_CURSOR_DETECTION)));
    }

    public static String getScreenshotDirectory() {
        return System.getProperty(SCREENSHOT_DIRECTORY);
    }

    public static String getRemoteControlHostName() {
        return System.getProperty(REMOTE_CONTROL_HOST_NAME);
    }

    public static String getDeploymentURL() {
        return System.getProperty(DEPLOYMENT_URL);
    }

    public static String getScreenshotResolution() {
        return System.getProperty(SCREENSHOT_RESOLUTION);
    }

    public static Double getScreenshotComparisonTolerance() {
        String p = System.getProperty(SCREENSHOT_COMPARISON_TOLERANCE);
        if (p != null) {
            return Double.parseDouble(p);
        }

        return null;
    }

    public static boolean isCaptureScreenshotOnFailure() {
        // Default is true. Only false if explicitly defined
        if ("false".equalsIgnoreCase(System
                .getProperty(CAPTURE_SCREENSHOT_ON_FAILURE))) {
            return false;
        } else {
            return true;
        }
    }

    public static String getFileEncoding() {
        return System.getProperty(TEST_FILE_ENCODING);
    }

    public static String[] getBrowsers() {
        String browserString = System.getProperty(BROWSER_STRING);
        if (browserString == null) {
            return null;
        }

        return browserString.split(",");
    }

    public static boolean hasParameterFile() {
        return System.getProperty(PARAMETER_FILE) != null
                && System.getProperty(PARAMETER_FILE).length() > 0;
    }

    public static String getParameterFile() {
        return System.getProperty(PARAMETER_FILE);
    }
}
