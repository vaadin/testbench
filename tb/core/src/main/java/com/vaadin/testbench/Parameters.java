/**
 * Copyright (C) 2012 Vaadin Ltd
 * <p>
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * <p>
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * <p>
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

//import com.vaadin.testbench.annotations.RunLocally;
//import com.vaadin.testbench.annotations.RunOnHub;
//import com.vaadin.testbench.parallel.Browser;

public class Parameters {
  private static boolean isDebug;
  private static boolean isScreenshotComparisonCursorDetection;
  private static String screenshotReferenceDirectory;
  private static String screenshotErrorDirectory;
  private static double screenshotComparisonTolerance;
  private static int maxScreenshotRetries;
  private static int screenshotRetryDelay = 500;
  private static int maxAttempts;

  //    private static int testsInParallel;
  //    private static int testSuitesInParallel;
//    private static String testbenchGridBrowsers;
  static {
    isDebug = getSystemPropertyBoolean("debug" , false);

    isScreenshotComparisonCursorDetection = getSystemPropertyBoolean(
        "screenshotComparisonCursorDetection" , false);
    screenshotReferenceDirectory = getSystemPropertyString(
        "screenshotReferenceDirectory" , "reference-screenshots");
    screenshotErrorDirectory = getSystemPropertyString(
        "screenshotErrorDirectory" , "error-screenshots");
    screenshotComparisonTolerance = getSystemPropertyDouble(
        "screenshotComparisonTolerance" , 0.01);
    maxScreenshotRetries = getSystemPropertyInt("maxScreenshotRetries" , 2);
    screenshotRetryDelay = getSystemPropertyInt("screenshotRetryDelay" ,
                                                500);

    maxAttempts = getSystemPropertyInt("maxAttempts" , 1);

//        testSuitesInParallel = getSystemPropertyInt("testSuitesInParallel", 20);
//        if (hasSystemProperty("testsInParallel")) {
//            testsInParallel = getSystemPropertyInt("testsInParallel", 1);
//        } else if (isLocalWebDriverUsed()) {
//            testsInParallel = 10;
//        } else {
//            testsInParallel = 50;
//        }
//        testbenchGridBrowsers = getSystemPropertyString("gridBrowsers",
//                                                        System.getenv("TESTBENCH_GRID_BROWSERS"));
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
    return System
               .getProperty(getQualifiedParameter(unqualifiedName)) != null;
  }

  private static String getSystemPropertyString(String unqualifiedName ,
                                                String defaultValue) {
    if (hasSystemProperty(unqualifiedName)) {
      return System.getProperty(getQualifiedParameter(unqualifiedName));
    } else {
      return defaultValue;
    }
  }

  private static boolean getSystemPropertyBoolean(String unqualifiedName ,
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

  private static int getSystemPropertyInt(String unqualifiedName ,
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

  private static double getSystemPropertyDouble(String unqualifiedName ,
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
   * @param tolerance the error tolerance.
   */
  public static void setScreenshotComparisonTolerance(double tolerance) {
    Parameters.screenshotComparisonTolerance = tolerance;
  }

  /**
   * @return the error tolerance to use for screen shots. The default
   * tolerance is 0.01
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
   * @param retryDelay the delay in milliseconds.
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
   * not been set
   */
  public static String getRunLocallyBrowserName() {
    String browserAndVersion = getSystemPropertyString("runLocally" , null);
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
   * has not been set
   */
  public static String getRunLocallyBrowserVersion() {
    String browserAndVersion = getSystemPropertyString("runLocally" , null);
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
   * @param browserAndVersion the string to parse
   * @return an array with two items, the browser name and the browser
   * version. Does not contain nulls
   */
  static String[] parseRunLocally(String browserAndVersion) {
    if (browserAndVersion.contains("-")) {
      return browserAndVersion.split("-" , 2);
    } else {
      return new String[]{browserAndVersion , ""};
    }
  }

  public static boolean isLocalWebDriverUsed() {
    String useLocalWebDriver = System.getProperty("useLocalWebDriver");

    return useLocalWebDriver != null
           && useLocalWebDriver.toLowerCase().equals("true");
  }

  /**
   * Gets the maximum number of times to run the test in case of a random
   * failure. See {@link RetryRule} for details.
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
   * @param maxAttempts maximum attempts the test can be run.
   */
  public static void setMaxAttempts(int maxAttempts) {
    Parameters.maxAttempts = maxAttempts;
  }

}
