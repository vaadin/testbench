/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.http.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.Browser;

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
    private static int maxAttempts;
    private static String testbenchGridBrowsers;
    private static boolean headless;
    private static int readTimeout;
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
        screenshotRetryDelay = getSystemPropertyInt("screenshotRetryDelay",
                500);

        testSuitesInParallel = getSystemPropertyInt("testSuitesInParallel", 20);
        maxAttempts = getSystemPropertyInt("maxAttempts", 1);
        if (hasSystemProperty("testsInParallel")) {
            testsInParallel = getSystemPropertyInt("testsInParallel", 1);
        } else if (isLocalWebDriverUsed()) {
            testsInParallel = 10;
        } else {
            testsInParallel = 50;
        }
        testbenchGridBrowsers = getSystemPropertyString("gridBrowsers",
                System.getenv("TESTBENCH_GRID_BROWSERS"));
        headless = getSystemPropertyBoolean("headless", false);
        readTimeout = getSystemPropertyInt("readTimeout",
                (int) ClientConfig.defaultConfig().readTimeout().toSeconds());
    }

    /**
     * Turns debugging info on/off
     *
     * @param isDebug
     *            {@code true} if debugging info should be turned on,
     *            {@code false} otherwise
     */
    public static void setDebug(boolean isDebug) {
        Parameters.isDebug = isDebug;
    }

    private static boolean hasSystemProperty(String unqualifiedName) {
        return System
                .getProperty(getQualifiedParameter(unqualifiedName)) != null;
    }

    private static String getSystemPropertyString(String unqualifiedName,
            String defaultValue) {
        if (hasSystemProperty(unqualifiedName)) {
            return System.getProperty(getQualifiedParameter(unqualifiedName));
        } else {
            return defaultValue;
        }
    }

    private static boolean getSystemPropertyBoolean(String unqualifiedName,
            boolean defaultValue) {
        if (hasSystemProperty(unqualifiedName)) {
            String str = System
                    .getProperty(getQualifiedParameter(unqualifiedName));
            if (str != null && str.equalsIgnoreCase("true")) {
                return true;
            } else {
                return false;
            }
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
                getLogger().error("Unable to parse parameter '"
                        + getQualifiedParameter(unqualifiedName) + "' value "
                        + str + " to an integer");
            }
        }
        return defaultValue;
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(Parameters.class);
    }

    private static double getSystemPropertyDouble(String unqualifiedName,
            double defaultValue) {
        if (hasSystemProperty(unqualifiedName)) {
            String str = System
                    .getProperty(getQualifiedParameter(unqualifiedName));
            try {
                return Double.parseDouble(str);
            } catch (Exception e) {
                getLogger().error("Unable to parse parameter '"
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
     * @return {@code true} if debugging info should be shown, {@code false}
     *         otherwise
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
     *            {@code true} to enable cursor detection
     */
    public static void setScreenshotComparisonCursorDetection(
            boolean isScreenshotComparisonCursorDetection) {
        Parameters.isScreenshotComparisonCursorDetection = isScreenshotComparisonCursorDetection;
    }

    /**
     * Tells whether to treat screen shots with the only difference being a text
     * input cursor as equal or not. If true, they will be treated as equal.
     *
     * @return {@code true} if cursor detection is used, {@code false} otherwise
     */
    public static boolean isScreenshotComparisonCursorDetection() {
        return isScreenshotComparisonCursorDetection;
    }

    /**
     * Sets the directory to search for reference images.
     *
     * @param screenshotReferenceDirectory
     *            the directory to search for reference images
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
     *            the directory path
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
     * Sets the maximum allowed retries when comparing screen shots. This is
     * useful since in some situations it might take a little bit longer for all
     * the elements to settle into place.
     *
     * @param maxRetries
     *            the maximum number of retries for screenshot comparisons
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

    /**
     * Gets host name of the hub to run tests on.
     * <p>
     * This will override any {@link RunOnHub @RunOnHub} annotation present on
     * the test class.
     *
     * @return the host name of the hub, or null if no host name has been
     *         defined
     */
    public static String getHubHostname() {
        return getSystemPropertyString("hubHostname", null);
    }

    /**
     * Gets the name of the browser to use for a local test.
     * <p>
     * Reads the value from the
     * {@code com.vaadin.testbench.Parameters.runLocally} system property. If
     * the parameter is defined, it will override any
     * {@link RunLocally @RunLocally} annotation.
     * <p>
     * The format of the system property is "[browsername]" or
     * "[browsername]-[version]" where the version is optional.
     * <p>
     * If the system property is defined, the test will be run locally and not
     * on a hub. The property effectively does the same thing as a
     * {@link RunLocally @RunLocally} annotation on the test class.
     *
     * @return the browser name read from the system property, or null if it has
     *         not been set
     */
    public static String getRunLocallyBrowserName() {
        String browserAndVersion = getSystemPropertyString("runLocally", null);
        if (browserAndVersion == null) {
            return null;
        }
        return parseRunLocally(browserAndVersion)[0];
    }

    /**
     * Gets the version of the browser to use for a local test.
     * <p>
     * Reads the value from the
     * {@code com.vaadin.testbench.Parameters.runLocally} system property. If
     * the parameter is defined, it will override any
     * {@link RunLocally @RunLocally} annotation.
     * <p>
     * The format of the system property is "[browsername]" or
     * "[browsername]-[version]" where the version is optional.
     *
     * @return the browser version read from the system property, or null if it
     *         has not been set
     */
    public static String getRunLocallyBrowserVersion() {
        String browserAndVersion = getSystemPropertyString("runLocally", null);
        if (browserAndVersion == null) {
            return null;
        }
        return parseRunLocally(browserAndVersion)[1];
    }

    /**
     * Parses a string given to
     * {@code com.vaadin.testbench.Parameters.runLocally} system property.
     * <p>
     * The format of the system property is "[browsername]" or
     * "[browsername]-[version]" where the version is optional.
     *
     * @param browserAndVersion
     *            the string to parse
     * @return an array with two items, the browser name and the browser
     *         version. Does not contain nulls
     */
    static String[] parseRunLocally(String browserAndVersion) {
        if (browserAndVersion.contains("-")) {
            return browserAndVersion.split("-", 2);
        } else {
            return new String[] { browserAndVersion, "" };
        }
    }

    public static boolean isLocalWebDriverUsed() {
        String useLocalWebDriver = System.getProperty("useLocalWebDriver");

        return useLocalWebDriver != null
                && useLocalWebDriver.toLowerCase().equals("true");
    }

    /**
     *
     * Gets the maximum number of times to run the test in case of a random
     * failure.
     *
     * @return maximum attempts the test can be run.
     */
    public static int getMaxAttempts() {
        return maxAttempts;
    }

    /**
     * Sets the maximum number of times to run the test in case of a random
     * failure
     *
     * @param maxAttempts
     *            maximum attempts the test can be run.
     */
    public static void setMaxAttempts(int maxAttempts) {
        Parameters.maxAttempts = maxAttempts;
    }

    /**
     *
     * @return The configuration string of browsers (and their versions) as a
     *         comma separated list. Defaults to null string.
     */
    public static String getGridBrowsersString() {
        return testbenchGridBrowsers;
    }

    /**
     * Parses the grid browsers string and returns a new List of
     * DesiredCapabilities
     *
     * @return a list of DesiredCapabilities based on getGridBrowsersString.
     *         Empty list if nothing configured.
     */
    public static List<DesiredCapabilities> getGridBrowsers() {
        String browsers = testbenchGridBrowsers;
        List<DesiredCapabilities> finalList = new ArrayList<>();
        if (browsers != null && !browsers.isEmpty()) {
            for (String browserStr : browsers.split(",")) {
                String[] browserStrSplit = browserStr.split("-");
                Browser browser = Browser.valueOf(
                        browserStrSplit[0].toUpperCase(Locale.ENGLISH).trim());
                DesiredCapabilities capabilities = browser
                        .getDesiredCapabilities();
                if (browserStrSplit.length > 1) {
                    capabilities.setVersion(browserStrSplit[1].trim());
                }
                finalList.add(capabilities);
            }
        }
        return finalList;
    }

    /**
     * Sets the default browsers used in test grid if not overridden in a
     * ParallelTest
     *
     * @param browsers
     *            comma separated list of browsers e.g.
     *            "chrome,safari-9,firefox-53"
     */
    public static void setGridBrowsers(String browsers) {
        testbenchGridBrowsers = browsers;
    }

    /**
     * Checks if requested to run browsers in headless mode when runnning on a
     * local machine using a browser supporting it (currently Chrome and
     * Firefox).
     *
     * @return <code>true</code>, if requested to run in headless mode,
     *         <code>false</code> otherwise
     */
    public static boolean isHeadless() {
        return headless;
    }

    /**
     * Sets whether to run browsers in headless mode when runnning on a local
     * machine using a browser supporting it (currently Chrome and Firefox).
     *
     * @param headless
     *            <code>true</code>, to run in headless mode, <code>false</code>
     *            otherwise
     */
    public static void setHeadless(boolean headless) {
        Parameters.headless = headless;
    }

    /**
     * Sets the time to wait for a response to a command sent to a remote web
     * driver.
     * <p>
     * If you are running on a hub that limits the number of concurrent sessions
     * and you are requesting more sessions than that, your session request
     * might time out before the hub responds with a new session. In this case,
     * you can increase this timeout to make it wait longer.
     * <p>
     * Note that this value must be set before a remote web driver is created,
     * which typically happens during the "before" phase of a unit test.
     *
     * @param readTimeout
     *            the timeout in seconds
     */
    public static void setReadTimeout(int readTimeout) {
        Parameters.readTimeout = readTimeout;
    }

    /**
     * Gets the time to wait for a response to a command sent to a remote web
     * driver.
     *
     * @return the timeout in seconds
     */
    public static int getReadTimeout() {
        return readTimeout;
    }
}
