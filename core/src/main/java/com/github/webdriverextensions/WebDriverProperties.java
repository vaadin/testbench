package com.github.webdriverextensions;

public interface WebDriverProperties {
    String CHROME_DRIVER_PROPERTY_NAME = "webdriver.chrome.driver";
    String FIREFOX_DRIVER_PROPERTY_NAME = "webdriver.gecko.driver";
    String IE_DRIVER_PROPERTY_NAME = "webdriver.ie.driver";
    String OPERA_DRIVER_PROPERTY_NAME = "webdriver.opera.driver";
    String EDGE_DRIVER_PROPERTY_NAME = "webdriver.edge.driver";

    String CHROME_BINARY_PROPERTY_NAME = "chrome.binary.path";
    String GECKO_BINARY_PROPERTY_NAME = "gecko.binary.path";
    String IE_BINARY_PROPERTY_NAME = "ie.binary.path";
    String OPERA_BINARY_PROPERTY_NAME = "opera.binary.path";
    String EDGE_BINARY_PROPERTY_NAME = "edge.binary.path";

    String PHANTOMJS_BINARY_PROPERTY_NAME = "phantomjs.binary.path";

    String DISABLED_BROWSERS_PROPERTY_NAME = "webdriverextensions.disabledbrowsers";
}
