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
package com.vaadin.testbench.parallel.setup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.parallel.Browser;

/**
 * <p>
 * Uses {@link RemoteDriver} or {@link LocalDriver} to provide remote or local
 * {@link WebDriver} to run tests on.<br>
 * </p>
 * <p>
 * {@link RemoteDriver} and {@link LocalDriver} can be subclassed in order to
 * extend their functionalities.
 * </p>
 */
public class SetupDriver {

    /**
     * <p>
     * Sets up and returns a {@link WebDriver} to run test on hubURL. <br>
     * The test must set the driver through
     * {@link TestBenchTestCase#setDriver(webDriver)}
     * </p>
     *
     * @param remoteDriver
     *            {@link RemoteDriver} instance to use to setup the
     *            {@link WebDriver}
     * @param hubURL
     *            URL of the Hub to run the tests on
     * @return {@link WebDriver} properly setup
     * @throws Exception
     *             If anything goes wrong
     */
    public WebDriver setupRemoteDriver(RemoteDriver remoteDriver, String hubURL)
            throws Exception {
        DesiredCapabilities capabilities = getDesiredCapabilities();
        WebDriver driver = remoteDriver.createDriver(hubURL, capabilities);
        return driver;
    }

    /**
     * Sets up and returns a {@link WebDriver} to run test on hubURL. <br>
     * The test must set the driver through
     * {@link TestBenchTestCase#setDriver(webDriver)}
     *
     * @param hubURL
     *            URL of the Hub to run the tests on
     * @return {@link WebDriver} properly setup
     * @throws Exception
     *             If anything goes wrong
     */
    public WebDriver setupRemoteDriver(String hubURL) throws Exception {
        return setupRemoteDriver(new RemoteDriver(), hubURL);
    }

    /**
     * <p>
     * Sets up and returns a {@link WebDriver} to run test.
     * </p>
     * <p>
     * The test will run on browser specified by
     * {@link #getDesiredCapabilities()}<br>
     * The test must set the driver through
     * {@link TestBenchTestCase#setDriver(webDriver)}
     * </p>
     *
     * @param localDriver
     *            {@link LocalDriver} instance used to setup the
     *            {@link WebDriver}
     * @return {@link WebDriver} properly setup
     */
    public WebDriver setupLocalDriver(LocalDriver localDriver) {
        DesiredCapabilities capabilities = getDesiredCapabilities();
        WebDriver driver = localDriver.createDriver(capabilities);
        return driver;
    }

    /**
     * <p>
     * Sets up and returns a {@link WebDriver} to run test.
     * </p>
     * <p>
     * The test will run on browser specified by
     * {@link #getDesiredCapabilities()}<br>
     * The test must set the driver through
     * {@link TestBenchTestCase#setDriver(webDriver)}
     * </p>
     *
     * @return {@link WebDriver} properly setup
     */
    public WebDriver setupLocalDriver() {
        return setupLocalDriver(new LocalDriver());
    }

    /**
     * <p>
     * Sets up and returns a {@link WebDriver} to run test. This driver will run
     * the test on the {@link Browser} provided in the {@link RunLocally}
     * annotation.
     * </p>
     *
     * @param runLocallyBrowser
     *            Browser to run test on
     * @param version
     *            version of the browser
     * @param localDriver
     *            {@link LocalDriver} instance used to setup the
     *            {@link WebDriver}
     * @return {@link WebDriver} properly setup
     */
    public WebDriver setupLocalDriver(Browser runLocallyBrowser,
            String version, LocalDriver localDriver) {
        assert (runLocallyBrowser != null);
        DesiredCapabilities capabilities = runLocallyBrowser
                .getDesiredCapabilities();
        capabilities.setVersion(version);
        WebDriver driver = localDriver.createDriver(capabilities);
        return driver;
    }

    /**
     * <p>
     * Sets up and returns a {@link WebDriver} to run test. This driver will run
     * the test on the {@link Browser} provided in the {@link RunLocally}
     * annotation.
     * </p>
     *
     * @param runLocallyBrowser
     *            Browser to run test on
     * @param localDriver
     *            {@link LocalDriver} instance used to setup the
     *            {@link WebDriver}
     * @return {@link WebDriver} properly setup
     */
    public WebDriver setupLocalDriver(Browser runLocallyBrowser,
            LocalDriver localDriver) {
        return setupLocalDriver(runLocallyBrowser, "", localDriver);
    }

    /**
     * <p>
     * Sets up and returns a {@link WebDriver} to run test. This driver will run
     * the test on the {@link Browser} provided in the {@link RunLocally}
     * annotation.
     * </p>
     *
     * @param runLocallyBrowser
     *            Browser to run test on
     * @param version
     *            version of the browser
     * @return {@link WebDriver} properly setup
     */
    public WebDriver setupLocalDriver(Browser runLocallyBrowser, String version) {
        return setupLocalDriver(runLocallyBrowser, version, new LocalDriver());
    }

    /**
     * <p>
     * Sets up and returns a {@link WebDriver} to run test. This driver will run
     * the test on the {@link Browser} provided in the {@link RunLocally}
     * annotation.
     * </p>
     *
     * @param runLocallyBrowser
     *            Browser to run test on
     * @return {@link WebDriver} properly setup
     */
    public WebDriver setupLocalDriver(Browser runLocallyBrowser) {
        return setupLocalDriver(runLocallyBrowser, "", new LocalDriver());
    }

    private static DesiredCapabilities desiredCapabilities = Browser.FIREFOX
            .getDesiredCapabilities();

    /**
     * Used to determine which capabilities should be used when setting up a
     * {@link WebDriver} for this test. Typically set by a test runner or left
     * at its default (Firefox 24). If you want to run a test on a single
     * browser other than Firefox 24 you can override this method.
     *
     * @return the requested browser capabilities
     */
    public static DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    /**
     * Sets the requested browser capabilities (typically browser name and
     * version)
     *
     * @param desiredCapabilities
     */
    public static void setDesiredCapabilities(
            DesiredCapabilities desiredCapabilities) {
        SetupDriver.desiredCapabilities = desiredCapabilities;
    }
}
