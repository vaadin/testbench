/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

public class Parameters {
    private static boolean isDebug;
    private static boolean isScreenshotComparisonCursorDetection;
    private static String screenshotReferenceDirectory;
    private static String screenshotErrorDirectory;
    private static double screenshotComparisonTolerance;
    private static int maxScreenshotRetries;
    private static int screenshotRetryDelay = 500;
    private static int testsInParallel;
    private static int testSuitesInParallel;
    static {
        isDebug = getSystemPropertyBoolean("debug", false);

        isScreenshotComparisonCursorDetection = getSystemPropertyBoolean(
                "screenshotComparisonCursorDetection", false);
        screenshotReferenceDirectory = getSystemPropertyString(
                "screenshotReferenceDirectory", "reference-screenshots");
        screenshotErrorDirectory = getSystemPropertyString(
                "screenshotErrorDirectory", "error-screenshots");
        screenshotComparisonTolerance = getSystemPropertyDouble(
                "screenshotComparisonTolerance", 0.01);
        maxScreenshotRetries = getSystemPropertyInt("maxScreenshotRetries", 2);
        screenshotRetryDelay = getSystemPropertyInt("screenshotRetryDelay", 500);

        testSuitesInParallel = getSystemPropertyInt("testSuitesInParallel", 20);

        if (hasSystemProperty("testsInParallel")) {
            testsInParallel = getSystemPropertyInt("testsInParallel", 1);
        } else if (isLocalWebDriverUsed()) {
            testsInParallel = 10;
        } else {
            testsInParallel = 50;
        }
    }

    /**
     * Turns debugging info on/off
     *
     * @param isDebug
     */
    public static void setDebug(boolean isDebug) {
        Parameters.isDebug = isDebug;
    }

    private static boolean hasSystemProperty(String unqualifiedName) {
        return System.getProperty(getQualifiedParameter(unqualifiedName)) != null;
    }

    private static String getSystemPropertyString(String unqualifiedName,
            String defaultValue) {
        if (hasSystemProperty(unqualifiedName))
            return System.getProperty(getQualifiedParameter(unqualifiedName));
        else
            return defaultValue;
    }

    private static boolean getSystemPropertyBoolean(String unqualifiedName,
            boolean defaultValue) {
        if (hasSystemProperty(unqualifiedName)) {
            String str = System
                    .getProperty(getQualifiedParameter(unqualifiedName));
            if (str != null && str.equalsIgnoreCase("true"))
                return true;
            else
                return false;
        } else {
            return defaultValue;
        }
    }

    private static int getSystemPropertyInt(String unqualifiedName,
            int defaultValue) {
        if (hasSystemProperty(unqualifiedName)) {
            String str = System
                    .getProperty(getQualifiedParameter(unqualifiedName));
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                System.err.println("Unable to parse parameter '"
                        + getQualifiedParameter(unqualifiedName) + "' value "
                        + str + " to an integer");
            }
        }
        return defaultValue;
    }

    private static double getSystemPropertyDouble(String unqualifiedName,
            double defaultValue) {
        if (hasSystemProperty(unqualifiedName)) {
            String str = System
                    .getProperty(getQualifiedParameter(unqualifiedName));
            try {
                return Double.parseDouble(str);
            } catch (Exception e) {
                System.err.println("Unable to parse parameter '"
                        + getQualifiedParameter(unqualifiedName) + "' value "
                        + str + " to a double");
            }
        }
        return defaultValue;
    }

    private static String getQualifiedParameter(String unqualifiedName) {
        return Parameters.class.getName() + "." + unqualifiedName;
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
     *
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
     * @throws UnsupportedOperationException
     * @deprecated This does nothing, use {@link ScreenshotOnFailureRule}
     */
    @Deprecated
    public static void setCaptureScreenshotOnFailure(
            boolean isCaptureScreenshotOnFailure) {
        throw new UnsupportedOperationException(
                "Deprecated, this method no longer does anything.");
    }

    /**
     * @return whether to capture a screen shot when a test fails or not.
     * @throws UnsupportedOperationException
     * @deprecated This does nothing, use {@link ScreenshotOnFailureRule}
     */
    @Deprecated
    public static boolean isCaptureScreenshotOnFailure() {
        throw new UnsupportedOperationException(
                "Deprecated, this method no longer does anything.");
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
     * milliseconds.
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

    /**
     *
     * @return maximum number of tests to run in parallel.
     * @deprecated Use {@link #getTestsInParallel()}
     */
    @Deprecated
    public static int getMaxThreads() {
        return getTestsInParallel();
    }

    /**
     * Sets the maximum number of tests to run in parallel.
     *
     * @param maxThreads
     *            maximum number of tests to run in parallel.
     * @deprecated Use {@link #setTestsInParallel(int)}
     */
    @Deprecated
    public static void setMaxThreads(int maxThreads) {
        setTestsInParallel(maxThreads);
    }

    /**
     * Sets the maximum number of tests to run in parallel.
     *
     * @param testsInParallel
     *            maximum number of tests to run in parallel.
     */
    public static void setTestsInParallel(int testsInParallel) {
        Parameters.testsInParallel = testsInParallel;
    }

    /**
     *
     * @return maximum number of tests to run in parallel.
     */
    public static int getTestsInParallel() {
        return testsInParallel;
    }

    /**
     * Sets the maximum number of test suites to run in parallel.
     *
     * @param testSuitesInParallel
     *            maximum number of testSuites to run in parallel.
     */
    public static void setTestSuitesInParallel(int testSuitesInParallel) {
        Parameters.testSuitesInParallel = testSuitesInParallel;
    }

    /**
     *
     * @return maximum number of test suites to run in parallel.
     */
    public static int getTestSuitesInParallel() {
        return testSuitesInParallel;
    }

    public static boolean isLocalWebDriverUsed() {
        String useLocalWebDriver = System.getProperty("useLocalWebDriver");

        return useLocalWebDriver != null
                && useLocalWebDriver.toLowerCase().equals("true");
    }
}
