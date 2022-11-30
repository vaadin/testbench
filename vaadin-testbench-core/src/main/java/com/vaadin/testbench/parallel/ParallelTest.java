/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import java.util.Collections;
import java.util.List;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.setup.SetupDriver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit tests should extend {@link ParallelTest} if they are to be run in
 * several browser configurations. For each browser configuration, a
 * {@link WebDriver} is properly created with the desired configuration.
 * <p>
 * You can configure your tests to be run in Sauce Labs. See details at
 * <a href="https://wiki.saucelabs.com">https://wiki.saucelabs.com</a> and
 * <a href=
 * "https://github.com/vaadin/testbench-demo">https://github.com/vaadin/testbench-demo</a>.
 * </p>
 */
@RunWith(ParallelRunner.class)
public class ParallelTest extends TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailure = new ScreenshotOnFailureRule(
            this, true);

    private static Logger getLogger() {
        return LoggerFactory.getLogger(ParallelTest.class);
    }

    private SetupDriver driverConfiguration = new SetupDriver();

    /**
     * <p>
     * Returns the complete URL of the hub where the tests will be run on. Used
     * by {@link #setup()}, for the creation of the {@link WebDriver}.
     * </p>
     * <p>
     * This method uses {@link #getHubHostname()} to build the complete address
     * of the Hub. Override in order to define a different hub address.<br>
     * </p>
     * <p>
     * You can provide sauce.user and sauce.sauceAccessKey system properties or
     * SAUCE_USERNAME and SAUCE_ACCESS_KEY environment variables to run the
     * tests in Sauce Labs. If both system property and environment variable is
     * defined, system property is prioritised.
     * </p>
     *
     * @return the complete URL of the hub where the tests will be run on. Used
     *         by {@link #setup()}, for the creation of the {@link WebDriver}.
     */
    protected String getHubURL() {
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            return SauceLabsIntegration.getHubUrl();
        } else {
            return "http://" + getHubHostname() + ":4444/wd/hub";
        }
    }

    /**
     * <p>
     * Returns the hostname of the hub where test is to be run on. If unit test
     * is annotated by {@link RunLocally}, this method returns localhost.
     * Otherwise, it will return the host defined by the
     * {@code com.vaadin.testbench.Parameters.hubHostname} system parameter or
     * the host defined using a {@link RunOnHub} annotation.
     * </p>
     * <p>
     * This method is used by {@link #getHubURL()} to get the full URL of the
     * hub to run tests on.
     * </p>
     *
     * @return the hostname of the hub where test is to be run on.
     */
    protected String getHubHostname() {
        String hubSystemProperty = Parameters.getHubHostname();
        if (hubSystemProperty != null) {
            return hubSystemProperty;
        }

        RunLocally runLocally = getClass().getAnnotation(RunLocally.class);
        if (runLocally != null) {
            return "localhost";
        }

        RunOnHub runOnHub = getRunOnHub(getClass());
        return runOnHub.value();
    }

    /**
     * <p>
     * Sets the driver for this test instance. Uses
     * {@link SetupDriver#setupRemoteDriver(String)} or
     * {@link SetupDriver#setupLocalDriver(Browser)} according to the
     * annotations found in current test case.
     * </p>
     * <p>
     * {@link RunOnHub} annotation can be used on the test case class to define
     * a test hub's hostname for the driver to connect to it.<br>
     * {@link RunLocally} annotation can be used on the test case class to force
     * the driver to connect to localhost ({@link RunLocally} annotation
     * overrides {@link RunOnHub} annotation).
     * </p>
     *
     * @throws Exception
     *             if unable to instantiate {@link WebDriver}
     */
    @Before
    public void setup() throws Exception {
        // Always give priority to @RunLocally annotation
        if ((getRunLocallyBrowser() != null)) {
            WebDriver driver = driverConfiguration.setupLocalDriver(
                    getRunLocallyBrowser(), getRunLocallyBrowserVersion());
            setDriver(driver);
        } else if (Parameters.isLocalWebDriverUsed()) {
            WebDriver driver = driverConfiguration.setupLocalDriver();
            setDriver(driver);
        } else if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            WebDriver driver = driverConfiguration
                    .setupRemoteDriver(getHubURL());
            setDriver(driver);

        } else if (getRunOnHub(getClass()) != null
                || Parameters.getHubHostname() != null) {
            WebDriver driver = driverConfiguration
                    .setupRemoteDriver(getHubURL());
            setDriver(driver);
        } else {
            getLogger().info(
                    "Did not find a configuration to run locally, on Sauce Labs or on other test grid. Falling back to running locally on Chrome.");
            WebDriver driver = driverConfiguration
                    .setupLocalDriver(Browser.CHROME);
            setDriver(driver);
        }
    }

    /**
     * @param klass
     *            the test class to get the {@link RunOnHub} annotation from
     * @return Value of the {@link RunOnHub} annotation of passed Class, or null
     *         if annotation is not present.
     */
    protected RunOnHub getRunOnHub(Class<?> klass) {
        if (klass == null) {
            return null;
        }

        return klass.getAnnotation(RunOnHub.class);
    }

    /**
     * @return Browser value of the {@link RunLocally} annotation of current
     *         Class, or null if annotation is not present.
     */
    protected Browser getRunLocallyBrowser() {
        return ParallelRunner.getRunLocallyBrowserName(getClass());
    }

    /**
     * @return Version value of the {@link RunLocally} annotation of current
     *         Class, or empty empty String if annotation is not present.
     */
    protected String getRunLocallyBrowserVersion() {
        return ParallelRunner.getRunLocallyBrowserVersion(getClass());
    }

    /**
     * @return default capabilities, used if no {@link BrowserConfiguration}
     *         method was found
     */
    public static List<DesiredCapabilities> getDefaultCapabilities() {
        return Collections.singletonList(BrowserUtil.chrome());
    }

    /**
     * Sets the requested {@link DesiredCapabilities} (usually browser name and
     * version)
     *
     * @param desiredCapabilities
     *            to be set
     */
    public void setDesiredCapabilities(
            DesiredCapabilities desiredCapabilities) {
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            SauceLabsIntegration.setDesiredCapabilities(desiredCapabilities);
        }
        driverConfiguration.setDesiredCapabilities(desiredCapabilities);
    }

    @BrowserConfiguration
    List<DesiredCapabilities> getBrowserConfigurationFromParameterOrDefault() {
        if (Parameters.getGridBrowsers().isEmpty()) {
            return getDefaultCapabilities();
        } else {
            return Parameters.getGridBrowsers();
        }
    }

    /**
     * Gets the {@link DesiredCapabilities} (usually browser name and version)
     *
     * @return the {@link DesiredCapabilities}
     */
    protected DesiredCapabilities getDesiredCapabilities() {
        return driverConfiguration.getDesiredCapabilities();
    }
}
