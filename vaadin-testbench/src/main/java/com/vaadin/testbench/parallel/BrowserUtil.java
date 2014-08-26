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
package com.vaadin.testbench.parallel;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.BrowserType;
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
        DesiredCapabilities c = browserFactory.create(Browser.SAFARI);
        return c;
    }

    /**
     * Gets the capabilities for Chrome
     *
     * @return an object describing the capabilities required for running a test
     *         on Chrome
     */
    public static DesiredCapabilities chrome() {
        DesiredCapabilities c = browserFactory.create(Browser.CHROME);
        return c;
    }

    /**
     * Gets the capabilities for Opera
     *
     * @return an object describing the capabilities required for running a test
     *         on Opera
     */
    public static DesiredCapabilities opera() {
        DesiredCapabilities c = browserFactory.create(Browser.OPERA);
        return c;
    }

    /**
     * Gets the capabilities for Firefox
     *
     * @return an object describing the capabilities required for running a test
     *         on Firefox
     */
    public static DesiredCapabilities firefox() {
        DesiredCapabilities c = browserFactory.create(Browser.FIREFOX);
        return c;
    }

    /**
     * Gets the capabilities for Internet Explorer 8
     *
     * @return an object describing the capabilities required for running a test
     *         on Internet Explorer 8
     */
    public static DesiredCapabilities ie8() {
        DesiredCapabilities c = browserFactory.create(Browser.IE8);
        return c;
    }

    /**
     * Gets the capabilities for Internet Explorer 9
     *
     * @return an object describing the capabilities required for running a test
     *         on Internet Explorer 9
     */
    public static DesiredCapabilities ie9() {
        DesiredCapabilities c = browserFactory.create(Browser.IE9);
        return c;
    }

    /**
     * Gets the capabilities for Internet Explorer 10
     *
     * @return an object describing the capabilities required for running a test
     *         on Internet Explorer 10
     */
    public static DesiredCapabilities ie10() {
        DesiredCapabilities c = browserFactory.create(Browser.IE10);
        return c;
    }

    /**
     * Gets the capabilities for Internet Explorer 11
     *
     * @return an object describing the capabilities required for running a test
     *         on Internet Explorer 11
     */
    public static DesiredCapabilities ie11() {
        DesiredCapabilities c = browserFactory.create(Browser.IE11);
        return c;
    }

    /**
     * Gets the capabilities for PhantomJS
     *
     * @return an object describing the capabilities required for running a test
     *         on PhantomJS
     */
    public static DesiredCapabilities phantomJS() {
        DesiredCapabilities c = browserFactory.create(Browser.PHANTOMJS);
        return c;
    }

    /**
     * Checks if the given capabilities refer to Internet Explorer 8
     *
     * @param capabilities
     * @return true if the capabilities refer to IE8, false otherwise
     */
    public static boolean isIE8(DesiredCapabilities capabilities) {
        return isIE(capabilities) && "8".equals(capabilities.getVersion());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Internet Explorer, false
     *         otherwise
     */
    public static boolean isIE(DesiredCapabilities capabilities) {
        return BrowserType.IE.equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Chrome, false otherwise
     */
    public static boolean isChrome(DesiredCapabilities capabilities) {
        return BrowserType.CHROME.equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Opera, false otherwise
     */
    public static boolean isOpera(DesiredCapabilities capabilities) {
        return BrowserType.OPERA.equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Safari, false otherwise
     */
    public static boolean isSafari(DesiredCapabilities capabilities) {
        return BrowserType.SAFARI.equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to Firefox, false otherwise
     */
    public static boolean isFirefox(DesiredCapabilities capabilities) {
        return BrowserType.FIREFOX.equals(capabilities.getBrowserName());
    }

    /**
     * @param capabilities
     *            The capabilities to check
     * @return true if the capabilities refer to PhantomJS, false otherwise
     */
    public static boolean isPhantomJS(DesiredCapabilities capabilities) {
        return BrowserType.PHANTOMJS.equals(capabilities.getBrowserName());
    }

    /**
     * Returns a human readable identifier of the given browser. Used for test
     * naming and screenshots
     *
     * @param capabilities
     * @return a human readable string describing the capabilities
     */
    public static String getBrowserIdentifier(DesiredCapabilities capabilities) {
        if (isIE(capabilities)) {
            return "InternetExplorer";
        } else if (isFirefox(capabilities)) {
            return "Firefox";
        } else if (isChrome(capabilities)) {
            return "Chrome";
        } else if (isSafari(capabilities)) {
            return "Safari";
        } else if (isOpera(capabilities)) {
            return "Opera";
        } else if (isPhantomJS(capabilities)) {
            return "PhantomJS";
        }

        return capabilities.getBrowserName();
    }

    /**
     * Returns a human readable identifier of the platform described by the
     * given capabilities. Used mainly for screenshots
     *
     * @param capabilities
     * @return a human readable string describing the platform
     */
    public static String getPlatform(DesiredCapabilities capabilities) {
        if (capabilities.getPlatform() == Platform.WIN8
                || capabilities.getPlatform() == Platform.WINDOWS
                || capabilities.getPlatform() == Platform.VISTA
                || capabilities.getPlatform() == Platform.XP) {
            return "Windows";
        } else if (capabilities.getPlatform() == Platform.MAC) {
            return "Mac";
        }
        return capabilities.getPlatform().toString();
    }

    /**
     * Returns a string which uniquely (enough) identifies this browser. Used
     * mainly in screenshot names.
     *
     * @param capabilities
     *
     * @return a unique string for each browser
     */
    public static String getUniqueIdentifier(DesiredCapabilities capabilities) {
        return getUniqueIdentifier(getPlatform(capabilities),
                getBrowserIdentifier(capabilities), capabilities.getVersion());
    }

    /**
     * Returns a string which uniquely (enough) identifies this browser. Used
     * mainly in screenshot names.
     *
     * @param capabilities
     *
     * @return a unique string for each browser
     */
    public static String getUniqueIdentifier(DesiredCapabilities capabilities,
            String versionOverride) {
        return getUniqueIdentifier(getPlatform(capabilities),
                getBrowserIdentifier(capabilities), versionOverride);
    }

    private static String getUniqueIdentifier(String platform, String browser,
            String version) {
        return platform + "_" + browser + "_" + version;
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
    public static void setBrowserFactory(TestBenchBrowserFactory browserFactory) {
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
