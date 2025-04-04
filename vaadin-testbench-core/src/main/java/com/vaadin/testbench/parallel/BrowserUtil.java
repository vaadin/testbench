/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Provides helper method for selecting the browser to run on
 */
public class BrowserUtil {

    private static TestBenchBrowserFactory browserFactory = new DefaultBrowserFactory();

    /**
     * Gets the capabilities for Safari
     *
     * @return an object describing the capabilities required for running a test
     *         on Safari
     */
    public static DesiredCapabilities safari() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.SAFARI);
        return c;
    }

    /**
     * Gets the capabilities for Chrome
     *
     * @return an object describing the capabilities required for running a test
     *         on Chrome
     */
    public static DesiredCapabilities chrome() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.CHROME);
        return c;
    }

    /**
     * Gets the capabilities for Firefox
     *
     * @return an object describing the capabilities required for running a test
     *         on Firefox
     */
    public static DesiredCapabilities firefox() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.FIREFOX);
        return c;
    }

    /**
     * Gets the capabilities for Internet Explorer 8
     *
     * @return an object describing the capabilities required for running a test
     *         on Internet Explorer 8
     */
    public static DesiredCapabilities ie8() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.IE8);
        return c;
    }

    /**
     * Gets the capabilities for Internet Explorer 9
     *
     * @return an object describing the capabilities required for running a test
     *         on Internet Explorer 9
     */
    public static DesiredCapabilities ie9() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.IE9);
        return c;
    }

    /**
     * Gets the capabilities for Internet Explorer 10
     *
     * @return an object describing the capabilities required for running a test
     *         on Internet Explorer 10
     */
    public static DesiredCapabilities ie10() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.IE10);
        return c;
    }

    /**
     * Gets the capabilities for Internet Explorer 11
     *
     * @return an object describing the capabilities required for running a test
     *         on Internet Explorer 11
     */
    public static DesiredCapabilities ie11() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.IE11);
        return c;
    }

    /**
     * Gets the capabilities for Edge
     *
     * @return an object describing the capabilities required for running a test
     *         on Edge
     */
    public static DesiredCapabilities edge() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.EDGE);
        return c;
    }

    /**
     * Gets the capabilities for PhantomJS
     *
     * @return an object describing the capabilities required for running a test
     *         on PhantomJS
     */
    public static DesiredCapabilities phantomJS() {
        DesiredCapabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.PHANTOMJS);
        return c;
    }

    /**
     * Checks if the given capabilities refer to Internet Explorer 8
     *
     * @param capabilities
     *            the capabilities to check
     * @return true if the capabilities refer to IE8, false otherwise
     */
    public static boolean isIE8(Capabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        return isIE(capabilities, 8);
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Internet Explorer, false
     *         otherwise
     */
    public static boolean isIE(Capabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        return Browser.IE.browserName().equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @param version
     *            Version number as an integer
     * @return true if the capabilities refer to correct version of Internet
     *         Explorer, false otherwise
     */
    public static boolean isIE(Capabilities capabilities, int version) {
        if (capabilities == null) {
            return false;
        }
        return isIE(capabilities)
                && ("" + version).equals(capabilities.getBrowserVersion());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Edge, false otherwise
     */
    public static boolean isEdge(Capabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        return Browser.EDGE.browserName().equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Chrome, false otherwise
     */
    public static boolean isChrome(Capabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        return capabilities.getBrowserName()
                .startsWith(Browser.CHROME.browserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Safari, false otherwise
     */
    public static boolean isSafari(Capabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        return Browser.SAFARI.browserName()
                .equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Firefox, false otherwise
     */
    public static boolean isFirefox(Capabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        return Browser.FIREFOX.browserName()
                .equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to PhantomJS, false otherwise
     */
    public static boolean isPhantomJS(Capabilities capabilities) {
        if (capabilities == null) {
            return false;
        }
        return "phantomjs".equals(capabilities.getBrowserName());
    }

    /**
     * Returns a human readable identifier of the given browser. Used for test
     * naming and screenshots
     *
     * @param capabilities
     *            the capabilities to check
     * @return a human readable string describing the capabilities
     */
    public static String getBrowserIdentifier(Capabilities capabilities) {
        if (isIE(capabilities)) {
            return "InternetExplorer";
        } else if (isFirefox(capabilities)) {
            return "Firefox";
        } else if (isChrome(capabilities)) {
            return "Chrome";
        } else if (isSafari(capabilities)) {
            return "Safari";
        } else if (isPhantomJS(capabilities)) {
            return "PhantomJS";
        } else if (isEdge(capabilities)) {
            return "Edge";
        } else if (capabilities == null) {
            return "Unknown";
        } else {
            return capabilities.getBrowserName();
        }
    }

    /**
     * Returns a human readable identifier of the platform described by the
     * given capabilities. Used mainly for screenshots
     *
     * @param capabilities
     *            the capabilities to check
     * @return a human readable string describing the platform
     */
    public static String getPlatform(Capabilities capabilities) {
        if (capabilities == null) {
            return "Unknown";
        }
        try {
            Platform p = capabilities.getPlatformName();
            Platform family = p != null ? p.family() : null;
            if (family == Platform.WINDOWS || p == Platform.WINDOWS) {
                return "Windows";
            } else if (family == Platform.MAC || p == Platform.MAC) {
                return "Mac";
            }

        } catch (Exception e) {
        }
        Object rawPlatform = capabilities
                .getCapability(CapabilityType.PLATFORM_NAME);
        if (rawPlatform == null) {
            return "Unknown";
        } else if (rawPlatform instanceof Platform) {
            return ((Platform) rawPlatform).name();
        }
        return rawPlatform.toString();
    }

    /**
     * Sets new BrowserFactory to generate default DesiredCapabilities for each
     * Browser.<br>
     * Extend BrowserFactory and override its methods in order to add default
     * version, platform or other capabilities.
     *
     * @param browserFactory
     *            BrowserFactory instance to use to generate default
     *            DesiredCapabilities
     */
    static void setBrowserFactory(TestBenchBrowserFactory browserFactory) {
        BrowserUtil.browserFactory = browserFactory;
    }

    /**
     * Gets the BrowserFactory used to generate new DesiredCapabilities
     *
     * @return BrowserFactory used to generate new DesiredCapabilities
     */
    public static TestBenchBrowserFactory getBrowserFactory() {
        return browserFactory;
    }
}
