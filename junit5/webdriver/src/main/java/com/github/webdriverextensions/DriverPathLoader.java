package com.github.webdriverextensions;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.webdriverextensions.WebDriverExtensionsProperties.*;
import static com.github.webdriverextensions.WebDriverProperties.*;

public class DriverPathLoader {

  private static final String DRIVER_DIR     = "_data/webdrivers/";
  private static final String REL_DRIVER_DIR = "./" + DRIVER_DIR;

  private DriverPathLoader() { }

  public static void loadDriverPaths() {
    PropertyUtils.setPropertyIfNotExists(CHROME_DRIVER_PROPERTY_NAME, getChromeDriverDefaultPath());
    PropertyUtils.setPropertyIfNotExists(FIREFOX_DRIVER_PROPERTY_NAME,
                                         getFirefoxDriverDefaultPath()
    );
    PropertyUtils.setPropertyIfNotExists(EDGE_DRIVER_PROPERTY_NAME, getEdgeDefaultPath());
    loadInternetExplorerDriverPath();
    PropertyUtils.setPropertyIfNotExists(PHANTOMJS_BINARY_PROPERTY_NAME, getPhantomJsDefaultPath());
    makeSureDriversAreExecutable();
  }


  private static void loadInternetExplorerDriverPath() {
    PropertyUtils.setPropertyIfNotExists(IE_DRIVER_PROPERTY_NAME,
                                         System.getProperty(INTERNET_EXPLORER_DRIVER_PROPERTY_NAME)
    ); // Alternative property name
    // that follows naming
    // convention

    PropertyUtils.setPropertyIfNotExists(IE_DRIVER_PROPERTY_NAME,
                                         getInternetExplorerDriverDefaultPath()
    );
  }

  private static void makeSureDriversAreExecutable() {
    FileUtils.makeExecutable(System.getProperty(CHROME_DRIVER_PROPERTY_NAME));
    FileUtils.makeExecutable(System.getProperty(IE_DRIVER_PROPERTY_NAME));
  }

  private static String getChromeDriverDefaultPath() {
    if (OsUtils.isWindows()) {
      return DRIVER_DIR + "chromedriver-windows-32bit.exe";
    } else if (OsUtils.isMac()) {
      if (OsUtils.is64Bit() && (Files.exists(Paths.get(REL_DRIVER_DIR + "chromedriver-mac-64bit"))
                                || Files.notExists(Paths.get(REL_DRIVER_DIR + "chromedriver-mac-32bit")))) {
        return DRIVER_DIR + "chromedriver-mac-64bit";
      } else {
        return DRIVER_DIR + "chromedriver-mac-32bit";
      }
    } else if (OsUtils.isLinux()) {
      if (OsUtils.is64Bit() && (Files.exists(Paths.get(REL_DRIVER_DIR + "chromedriver-linux-64bit"))
                                || Files.notExists(Paths.get(REL_DRIVER_DIR + "chromedriver-linux-32bit")))) {
        return DRIVER_DIR + "chromedriver-linux-64bit";
      } else {
        return DRIVER_DIR + "chromedriver-linux-32bit";
      }
    }
    return null;
  }

  private static String getFirefoxDriverDefaultPath() {
    if (OsUtils.isWindows()) {
      if (OsUtils.is64Bit()
          && (Files.exists(Paths.get(REL_DRIVER_DIR + "geckodriver-windows-64bit.exe"))
              || Files.notExists(Paths.get(REL_DRIVER_DIR + "geckodriver-windows-32bit.exe")))) {
        return DRIVER_DIR + "geckodriver-windows-64bit.exe";
      } else {
        return DRIVER_DIR + "geckodriver-windows-32bit.exe";
      }
    } else if (OsUtils.isMac()) {
      if (OsUtils.is64Bit() && (Files.exists(Paths.get(REL_DRIVER_DIR + "geckodriver-mac-64bit"))
                                || Files.notExists(Paths.get(REL_DRIVER_DIR + "geckodriver-mac-32bit")))) {
        return DRIVER_DIR + "geckodriver-mac-64bit";
      } else {
        return DRIVER_DIR + "geckodriver-mac-32bit";
      }
    } else if (OsUtils.isLinux()) {
      if (OsUtils.is64Bit() && (Files.exists(Paths.get(REL_DRIVER_DIR + "geckodriver-linux-64bit"))
                                || Files.notExists(Paths.get(REL_DRIVER_DIR + "geckodriver-linux-32bit")))) {
        return DRIVER_DIR + "geckodriver-linux-64bit";
      } else {
        return DRIVER_DIR + "geckodriver-linux-32bit";
      }
    }
    return null;
  }

  private static String getEdgeDefaultPath() {
    if (OsUtils.isWindows()) {
      if (OsUtils.is64Bit()
          && (Files.exists(Paths.get(REL_DRIVER_DIR + "edgedriver-windows-64bit.exe"))
              || Files.notExists(Paths.get(REL_DRIVER_DIR + "edgedriver-windows-32bit.exe")))) {
        return DRIVER_DIR + "edgedriver-windows-64bit.exe";
      } else {
        return DRIVER_DIR + "edgedriver-windows-32bit.exe";
      }
    }
    return null;
  }

  private static String getPhantomJsDefaultPath() {
    if (OsUtils.isWindows()) {
      return DRIVER_DIR + "phantomjs-windows-64bit.exe";
    } else if (OsUtils.isMac()) {
      return DRIVER_DIR + "phantomjs-mac-64bit";
    } else if (OsUtils.isLinux()) {
      if (OsUtils.is64Bit() && (Files.exists(Paths.get(REL_DRIVER_DIR + "phantomjs-linux-64bit"))
                                || Files.notExists(Paths.get(REL_DRIVER_DIR + "phantomjs-linux-32bit")))) {
        return DRIVER_DIR + "phantomjs-linux-64bit";
      } else {
        return DRIVER_DIR + "phantomjs-linux-32bit";
      }
    }
    return null;
  }

  private static String getInternetExplorerDriverDefaultPath() {
    if (OsUtils.isWindows()) {
      if (!PropertyUtils.propertyExists(IE_DRIVER_USE64BIT_PROPERTY_NAME)
          || !PropertyUtils.propertyExists(INTERNET_EXPLORER_DRIVER_USE64BIT_PROPERTY_NAME)) {
        if (OsUtils.isWindows10()) {
          return DRIVER_DIR + "internetexplorerdriver-windows-64bit.exe";
        } else {
          return DRIVER_DIR + "internetexplorerdriver-windows-32bit.exe";
        }
      } else {
        if (PropertyUtils.isTrue(IE_DRIVER_USE64BIT_PROPERTY_NAME)
            || PropertyUtils.isTrue(INTERNET_EXPLORER_DRIVER_USE64BIT_PROPERTY_NAME)) {
          return DRIVER_DIR + "internetexplorerdriver-windows-64bit.exe";
        } else {
          return DRIVER_DIR + "internetexplorerdriver-windows-32bit.exe";
        }
      }
    }
    return null;
  }
}
