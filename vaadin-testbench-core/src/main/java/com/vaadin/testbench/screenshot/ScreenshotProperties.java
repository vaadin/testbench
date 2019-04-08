package com.vaadin.testbench.screenshot;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

public final class ScreenshotProperties {

    public static final String SCREENSHOT_COMPARISON_CURSOR_DETECTION = "screenshotComparisonCursorDetection";
    public static final String SCREENSHOT_REFERENCE_DIRECTORY = "screenshotReferenceDirectory";
    public static final String SCREENSHOT_ERROR_DIRECTORY = "screenshotErrorDirectory";
    public static final String SCREENSHOT_COMPARISON_TOLERANCE = "screenshotComparisonTolerance";
    public static final String SCREENSHOT_RETRIES_MAX = "maxScreenshotRetries";
    public static final String SCREENSHOT_RETRY_DELAY = "screenshotRetryDelay";
    public static final String IMAGE_FILE_NAME_ENDING = "png";
    public static final String SCREENSHOT_COMPARISON_TOLERANCE_DEFAULT = "0.01";
    public static final String SCREENSHOT_RETRIES_MAX_DEFAULT = "2";
    public static final int SCREENSHOT_RETRY_DELAY_DEFAULT = 500;
    public static final String SCREENSHOT_REFERENCE_DIRECTORY_DEFAULT = "reference-screenshots";
    public static final String SCREENSHOT_ERROR_DIRECTORY_DEFAULT = "target/error-screenshots";

    private static String qualifiedKey(String key) {
        return ScreenshotProperties.class.getName() + "." + key;
    }

    private static boolean isScreenshotComparisonCursorDetection = Boolean.valueOf(
            System.getProperty(qualifiedKey(SCREENSHOT_COMPARISON_CURSOR_DETECTION),
                    Boolean.FALSE.toString()));

    private static String screenshotReferenceDirectory = System.getProperty(
            qualifiedKey(SCREENSHOT_REFERENCE_DIRECTORY), SCREENSHOT_REFERENCE_DIRECTORY_DEFAULT);

    private static String screenshotErrorDirectory = System.getProperty(
            qualifiedKey(SCREENSHOT_ERROR_DIRECTORY), SCREENSHOT_ERROR_DIRECTORY_DEFAULT);

    private static double screenshotComparisonTolerance = Double.valueOf(
            System.getProperty(qualifiedKey(SCREENSHOT_COMPARISON_TOLERANCE),
                    SCREENSHOT_COMPARISON_TOLERANCE_DEFAULT));

    private static int maxScreenshotRetries = Integer.valueOf(
            System.getProperty(qualifiedKey(SCREENSHOT_RETRIES_MAX), SCREENSHOT_RETRIES_MAX_DEFAULT));

    private static int screenshotRetryDelay = Integer.valueOf(
            System.getProperty(qualifiedKey(SCREENSHOT_RETRY_DELAY)), SCREENSHOT_RETRY_DELAY_DEFAULT);

    /**
     * Tells whether to treat screen shots with the only difference being a text
     * input cursor as equal or not. If true, they will be treated as equal.
     *
     * @return true if cursor detection is used
     */
    public static boolean isScreenshotComparisonCursorDetection() {
        return isScreenshotComparisonCursorDetection;
    }

    /**
     * Turns cursor detection on/off when comparing screen shots. If on, the
     * screen shot comparison will pass if the only difference is a text input
     * cursor.
     *
     * @param isScreenshotComparisonCursorDetection
     */
    public static void setScreenshotComparisonCursorDetection(
            boolean isScreenshotComparisonCursorDetection) {
        ScreenshotProperties.isScreenshotComparisonCursorDetection = isScreenshotComparisonCursorDetection;
    }

    /**
     * @return the directory to search for reference images.
     */
    public static String getScreenshotReferenceDirectory() {
        return screenshotReferenceDirectory;
    }

    /**
     * Sets the directory to search for reference images.
     *
     * @param screenshotReferenceDirectory
     */
    public static void setScreenshotReferenceDirectory(
            String screenshotReferenceDirectory) {
        ScreenshotProperties.screenshotReferenceDirectory = screenshotReferenceDirectory;
    }

    /**
     * @return the directory where error screen shots are stored.
     */
    public static String getScreenshotErrorDirectory() {
        return screenshotErrorDirectory;
    }

    /**
     * Sets the directory where error screen shots are stored.
     *
     * @param screenshotErrorDirectory
     */
    public static void setScreenshotErrorDirectory(
            String screenshotErrorDirectory) {
        ScreenshotProperties.screenshotErrorDirectory = screenshotErrorDirectory;
    }

    /**
     * @return the error tolerance to use for screen shots. The default
     * tolerance is 0.01
     */
    public static double getScreenshotComparisonTolerance() {
        return screenshotComparisonTolerance;
    }

    /**
     * Sets the error tolerance for screen shot comparisons. The tolerance is a
     * value between 0 and 1, where 0 means that the images must be a pixel
     * perfect match and 1 means that any changes are accepted.
     *
     * @param tolerance the error tolerance.
     */
    public static void setScreenshotComparisonTolerance(double tolerance) {
        ScreenshotProperties.screenshotComparisonTolerance = tolerance;
    }

    /**
     * @return the maximum amount of times to retry screen shot comparison.
     */
    public static int getScreenshotRetriesMax() {
        return maxScreenshotRetries;
    }

    /**
     * Sets the maximum allowed retries when comparing screen shots. This is
     * useful since in some situations it might take a little bit longer for all
     * the elements to settle into place.
     *
     * @param maxRetries
     */
    public static void setScreenshotRetriesMax(int maxRetries) {
        maxScreenshotRetries = maxRetries;
    }

    /**
     * @return the delay between screen shot comparison retries.
     */
    public static int getScreenshotRetryDelay() {
        return screenshotRetryDelay;
    }

    /**
     * Sets the delay between screen shot comparison retries. The default is 500
     * milliseconds.
     *
     * @param retryDelay the delay in milliseconds.
     */
    public static void setScreenshotRetryDelay(int retryDelay) {
        screenshotRetryDelay = retryDelay;
    }
}
