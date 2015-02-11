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

import static com.vaadin.testbench.Parameters.isLocalWebDriverUsed;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.setup.SetupDriver;

/**
 * Unit tests should extend {@link ParallelTest} if they are to be run in
 * several browser configurations. For each browser configuration, a
 * {@link WebDriver} is properly created with the desired configuration.
 */
@RunWith(ParallelRunner.class)
public class ParallelTest extends TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule screenshotOnFailure = new ScreenshotOnFailureRule(
            this, true);

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
     * 
     * @return the complete URL of the hub where the tests will be run on. Used
     *         by {@link #setup()}, for the creation of the {@link WebDriver}.
     */
    protected String getHubURL() {
        return "http://" + getHubHostname() + ":4444/wd/hub";

    }

    /**
     * <p>
     * Returns the hostname of the hub where test is to be run on. If unit test
     * is annotated by {@link RunLocally}, this method returns localhost.
     * Otherwise, it will return the host defined by {@link RunOnHub}
     * annotation.
     * </p>
     * <p>
     * This method is used by {@link #getHubURL()} to get the full URL of the
     * hub to run tests on.
     * </p>
     * 
     * @return the hostname of the hub where test is to be run on.
     */
    protected String getHubHostname() {
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
        } else if (isLocalWebDriverUsed()) {
            WebDriver driver = driverConfiguration.setupLocalDriver();
            setDriver(driver);
        } else if (getRunOnHub(getClass()) != null) {
            WebDriver driver = driverConfiguration
                    .setupRemoteDriver(getHubURL());
            setDriver(driver);
        } else {
            // can't find any configuration to setup WebDriver
            throw new IllegalArgumentException(
                    "Can't instantiate WebDriver: No configuration found. Test case was not annotated with @RunLocally annotation nor @RunOnHub annotation, and system variable 'useLocalWebDriver' was not found or not set to true.");
        }
    }

    /**
     * @return Value of the {@link RunOnHub} annotation of current Class, or
     *         null if annotation is not present.
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
        RunLocally runLocally = getClass().getAnnotation(RunLocally.class);
        if (runLocally != null) {
            return runLocally.value();
        } else {
            return null;
        }
    }

    /**
     * @return Version value of the {@link RunLocally} annotation of current
     *         Class, or empty empty String if annotation is not present.
     */
    protected String getRunLocallyBrowserVersion() {
        RunLocally runLocally = getClass().getAnnotation(RunLocally.class);
        if (runLocally != null) {
            return runLocally.version();
        } else {
            return "";
        }
    }

    /**
     * 
     * @return default capabilities, used if no {@link BrowserConfiguration}
     *          method was found
     */
    public static List<DesiredCapabilities> getDefaultCapabilities() {
        return Collections.singletonList(BrowserUtil.firefox());
    }

    /**
     * Sets the requested {@link DesiredCapabilities} (usually browser name and
     * version)
     * 
     * @param desiredCapabilities
     */
    public void setDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        driverConfiguration.setDesiredCapabilities(desiredCapabilities);
    }

    /**
     * Gets the {@link DesiredCapabilities} (usually browser name and version)
     * 
     * @return
     */
    protected DesiredCapabilities getDesiredCapabilities() {
        return driverConfiguration.getDesiredCapabilities();
    }
}
