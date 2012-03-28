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
    public static final String CAPTURE_SCREENSHOT_ON_FAILURE = SCREENSHOT_PACKAGE
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
    public static final String SCREENSHOT_MAX_RETRIES = SCREENSHOT_PACKAGE
            + "max.retries";
    public static final String SCREENSHOT_RETRY_DELAY = SCREENSHOT_PACKAGE
            + "retry.delay";

    // Other parameters
    public static final String REMOTE_CONTROL_HOST_NAME = BASE_PACKAGE
            + "tester.host";
    public static final String DEPLOYMENT_URL = BASE_PACKAGE + "deployment.url";
    private static final String TEST_FILE_ENCODING = BASE_PACKAGE + "encoding";
    private static final String PARAMETER_FILE = BASE_PACKAGE
            + "converter.parameterFile";
    public static final String TEST_MAX_RETRIES = BASE_PACKAGE + "test.retries";

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

    /**
     * Returns user defined tolerance to use for screenshots or the default
     * tolerance.
     * 
     * @return
     */
    public static double getScreenshotComparisonTolerance() {
        String p = System.getProperty(SCREENSHOT_COMPARISON_TOLERANCE);
        double tolerance = 0.025;
        if (p != null) {
            tolerance = Double.parseDouble(p);

            // Check that [difference] value inside allowed range.
            // if false set [difference] to default value.
            if (tolerance < 0 || tolerance > 1) {
                tolerance = 0.025;
            }
        }

        return tolerance;
    }

    public static boolean isCaptureScreenshotOnFailure() {
        // Default is true. Only false if explicitly defined
        if ("false".equalsIgnoreCase(System
                .getProperty(CAPTURE_SCREENSHOT_ON_FAILURE))) {
            return false;
        }
        return true;
    }

    public static int getMaxRetries() {
        String p = System.getProperty(SCREENSHOT_MAX_RETRIES);
        int retries = 2;
        if (p != null && p.length() > 0) {
            retries = Integer.parseInt(p);
        }
        return retries;
    }

    public static int getRetryDelay() {
        String p = System.getProperty(SCREENSHOT_RETRY_DELAY);
        int retryDelay = 500;
        if (p != null && p.length() > 0) {
            retryDelay = Integer.parseInt(p);
        }
        return retryDelay;
    }

    // Retry test
    public static int getMaxTestRetries() {
        String p = System.getProperty(TEST_MAX_RETRIES);
        int retries = 0;
        if (p != null && p.length() > 0) {
            retries = Integer.parseInt(p);
        }
        return retries;
    }

    // --- end retry test ---
    public static String getFileEncoding() {
        return System.getProperty(TEST_FILE_ENCODING);
    }

    public static boolean hasParameterFile() {
        return System.getProperty(PARAMETER_FILE) != null
                && System.getProperty(PARAMETER_FILE).length() > 0;
    }

    public static String getParameterFile() {
        return System.getProperty(PARAMETER_FILE);
    }

}
