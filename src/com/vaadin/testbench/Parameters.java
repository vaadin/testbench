package com.vaadin.testbench;

public class Parameters {
    private static boolean isDebug = false;
    private static boolean isScreenshotComparisonCursorDetection = false;
    private static String screenshotDirectory = null;
    private static double screenshotComparisonTolerance = 0.25;
    private static boolean isCaptureScreenshotOnFailure = true;
    private static int maxScreenshotRetries = 2;
    private static int screenshotRetryDelay = 500;

    public static void setDebug(boolean isDebug) {
        Parameters.isDebug = isDebug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setScreenshotComparisonCursorDetection(
            boolean isScreenshotComparisonCursorDetection) {
        Parameters.isScreenshotComparisonCursorDetection = isScreenshotComparisonCursorDetection;
    }

    public static boolean isScreenshotComparisonCursorDetection() {
        return isScreenshotComparisonCursorDetection;
    }

    public static void setScreenshotDirectory(String screenshotDirectory) {
        Parameters.screenshotDirectory = screenshotDirectory;
    }

    public static String getScreenshotDirectory() {
        return screenshotDirectory;
    }

    public static void setScreenshotComparisonTolerance(double tolerance) {
        Parameters.screenshotComparisonTolerance = tolerance;
    }

    /**
     * Returns user defined tolerance to use for screenshots or the default
     * tolerance.
     * 
     * @return
     */
    public static double getScreenshotComparisonTolerance() {
        return screenshotComparisonTolerance;
    }

    public static void setCaptureScreenshotOnFailure(
            boolean isCaptureScreenshotOnFailure) {
        Parameters.isCaptureScreenshotOnFailure = isCaptureScreenshotOnFailure;
    }

    public static boolean isCaptureScreenshotOnFailure() {
        return isCaptureScreenshotOnFailure;
    }

    public static void setMaxScreenshotRetries(int maxRetries) {
        maxScreenshotRetries = maxRetries;
    }

    public static int getMaxScreenshotRetries() {
        return maxScreenshotRetries;
    }

    public static void setScreenshotRetryDelay(int retryDelay) {
        screenshotRetryDelay = retryDelay;
    }

    public static int getScreenshotRetryDelay() {
        return screenshotRetryDelay;
    }
}
