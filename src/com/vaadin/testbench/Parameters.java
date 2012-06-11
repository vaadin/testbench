package com.vaadin.testbench;

public class Parameters {
    private static boolean isDebug = false;
    private static boolean isScreenshotComparisonCursorDetection = false;
    private static String screenshotReferenceDirectory = null;
    private static String screenshotErrorDirectory = null;
    private static double screenshotComparisonTolerance = 0.01;
    private static boolean isCaptureScreenshotOnFailure = true;
    private static int maxScreenshotRetries = 2;
    private static int screenshotRetryDelay = 500;

    /**
     * Turns debugging info on/off
     * 
     * @param isDebug
     */
    public static void setDebug(boolean isDebug) {
        Parameters.isDebug = isDebug;
    }

    /**
     * @return true if debugging info is to be shown
     */
    public static boolean isDebug() {
        return isDebug;
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
        Parameters.isScreenshotComparisonCursorDetection = isScreenshotComparisonCursorDetection;
    }

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
     * Sets the directory to search for reference images.
     * 
     * @param screenshotReferenceDirectory
     */
    public static void setScreenshotReferenceDirectory(
            String screenshotReferenceDirectory) {
        Parameters.screenshotReferenceDirectory = screenshotReferenceDirectory;
    }

    /**
     * @return the directory to search for reference images.
     */
    public static String getScreenshotReferenceDirectory() {
        return screenshotReferenceDirectory;
    }

    /**
     * Sets the directory where error screen shots are stored.
     * 
     * @param screenshotErrorDirectory
     */
    public static void setScreenshotErrorDirectory(
            String screenshotErrorDirectory) {
        Parameters.screenshotErrorDirectory = screenshotErrorDirectory;
    }

    /**
     * @return the directory where error screen shots are stored.
     */
    public static String getScreenshotErrorDirectory() {
        return screenshotErrorDirectory;
    }

    /**
     * Sets the error tolerance for screen shot comparisons. The tolerance is a
     * value between 0 and 1, where 0 means that the images must be a pixel
     * perfect match and 1 means that any changes are accepted.
     * 
     * @param tolerance
     *            the error tolerance.
     */
    public static void setScreenshotComparisonTolerance(double tolerance) {
        Parameters.screenshotComparisonTolerance = tolerance;
    }

    /**
     * @return the error tolerance to use for screen shots. The default
     *         tolerance is 0.01
     */
    public static double getScreenshotComparisonTolerance() {
        return screenshotComparisonTolerance;
    }

    /**
     * Sets whether to capture a screen shot when a test fails or not.
     * 
     * @param isCaptureScreenshotOnFailure
     */
    public static void setCaptureScreenshotOnFailure(
            boolean isCaptureScreenshotOnFailure) {
        Parameters.isCaptureScreenshotOnFailure = isCaptureScreenshotOnFailure;
    }

    /**
     * @return whether to capture a screen shot when a test fails or not.
     */
    public static boolean isCaptureScreenshotOnFailure() {
        return isCaptureScreenshotOnFailure;
    }

    /**
     * Sets the maximum allowed retries when comparing screen shots. This is
     * useful since in some situations it might take a little bit longer for all
     * the elements to settle into place.
     * 
     * @param maxRetries
     */
    public static void setMaxScreenshotRetries(int maxRetries) {
        maxScreenshotRetries = maxRetries;
    }

    /**
     * @return the maximum amount of times to retry screen shot comparison.
     */
    public static int getMaxScreenshotRetries() {
        return maxScreenshotRetries;
    }

    /**
     * Sets the delay between screen shot comparison retries. The default is 500
     * ms.
     * 
     * @param retryDelay
     *            the delay in milliseconds.
     */
    public static void setScreenshotRetryDelay(int retryDelay) {
        screenshotRetryDelay = retryDelay;
    }

    /**
     * @return the delay between screen shot comparison retries.
     */
    public static int getScreenshotRetryDelay() {
        return screenshotRetryDelay;
    }
}
