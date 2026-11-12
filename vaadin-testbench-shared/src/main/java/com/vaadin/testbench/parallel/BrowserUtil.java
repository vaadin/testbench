/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import java.util.Set;

import com.vaadin.testbench.Capabilities;
import com.vaadin.testbench.Platform;

/**
 * Provides helper method for selecting the browser to run on
 */
public class BrowserUtil {

    private static TestBenchBrowserFactory browserFactory = new DefaultBrowserFactory();

    // The names Selenium's org.openqa.selenium.remote.Browser matches
    private static final Set<String> CHROME_NAMES = Set.of("chrome",
            "chrome-headless-shell");
    private static final Set<String> EDGE_NAMES = Set.of("MicrosoftEdge",
            "msedge");
    private static final Set<String> FIREFOX_NAMES = Set.of("firefox");
    private static final Set<String> SAFARI_NAMES = Set.of("safari", "Safari");

    /**
     * Gets the capabilities for Safari
     *
     * @return an object describing the capabilities required for running a test
     *         on Safari
     */
    public static Capabilities safari() {
        Capabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.SAFARI);
        return c;
    }

    /**
     * Gets the capabilities for Chrome
     *
     * @return an object describing the capabilities required for running a test
     *         on Chrome
     */
    public static Capabilities chrome() {
        Capabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.CHROME);
        return c;
    }

    /**
     * Gets the capabilities for Firefox
     *
     * @return an object describing the capabilities required for running a test
     *         on Firefox
     */
    public static Capabilities firefox() {
        Capabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.FIREFOX);
        return c;
    }

    /**
     * Gets the capabilities for Edge
     *
     * @return an object describing the capabilities required for running a test
     *         on Edge
     */
    public static Capabilities edge() {
        Capabilities c = browserFactory
                .create(com.vaadin.testbench.parallel.Browser.EDGE);
        return c;
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
        return EDGE_NAMES.contains(capabilities.getBrowserName());
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
        return CHROME_NAMES.contains(capabilities.getBrowserName());
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
        return SAFARI_NAMES.contains(capabilities.getBrowserName());
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
        return FIREFOX_NAMES.contains(capabilities.getBrowserName());
    }

    /**
     * Returns a human readable identifier of the given browser. Used for test
     * naming and screenshots
     *
     * @param capabilities
     *            to obtain the browser identifier from
     * @return a human readable string describing the capabilities
     */
    public static String getBrowserIdentifier(Capabilities capabilities) {
        if (isFirefox(capabilities)) {
            return "Firefox";
        } else if (isChrome(capabilities)) {
            return "Chrome";
        } else if (isSafari(capabilities)) {
            return "Safari";
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
     *            to obtain the platform from
     * @return a human readable string describing the platform
     */
    public static String getPlatform(Capabilities capabilities) {
        if (capabilities == null) {
            return "Unknown";
        }
        Platform p = capabilities.getPlatformName();
        Platform family = p != null ? p.family() : null;
        if (family == Platform.WINDOWS || p == Platform.WINDOWS) {
            return "Windows";
        } else if (family == Platform.MAC || p == Platform.MAC) {
            return "Mac";
        }
        Object rawPlatform = capabilities
                .getCapability(Capabilities.PLATFORM_NAME);
        if (rawPlatform == null) {
            return "Unknown";
        }
        return rawPlatform.toString();
    }

    /**
     * Sets new BrowserFactory to generate default Capabilities for each
     * Browser.<br>
     * Extend BrowserFactory and override its methods in order to add default
     * version, platform or other capabilities.
     *
     * @param browserFactory
     *            BrowserFactory instance to use to generate default
     *            Capabilities
     * @since 9.0
     */
    public static void setBrowserFactory(
            TestBenchBrowserFactory browserFactory) {
        BrowserUtil.browserFactory = browserFactory;
    }

    /**
     * Gets the BrowserFactory used to generate new Capabilities
     *
     * @return BrowserFactory used to generate new Capabilities
     */
    public static TestBenchBrowserFactory getBrowserFactory() {
        return browserFactory;
    }
}
