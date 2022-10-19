/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.capabilities;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.SetCapabilities;
import com.vaadin.testbench.SetDriver;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.annotations.RunOnHub;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.SauceLabsIntegration;
import com.vaadin.testbench.parallel.setup.SetupDriver;

/**
 * <p>
 * TestBench {@link Extension} that provides {@link WebDriver} configuration and
 * startup according to given test configuration and desired capabilities.
 * </p>
 * <p>
 * See {@link #beforeEach(ExtensionContext)} for more detailed information about
 * test preparation.
 * </p>
 */
public class CapabilitiesExtension
        implements Extension, BeforeEachCallback, ExecutionCondition {

    private static Logger getLogger() {
        return LoggerFactory.getLogger(CapabilitiesExtension.class);
    }

    private final SetupDriver driverConfiguration = new SetupDriver();

    private final DesiredCapabilities desiredCapabilities;

    protected WebDriver driver;

    public CapabilitiesExtension(Capabilities capabilities) {
        desiredCapabilities = new DesiredCapabilities(capabilities);
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            SauceLabsIntegration.setDesiredCapabilities(desiredCapabilities);
        }

        driverConfiguration.setDesiredCapabilities(desiredCapabilities);
    }

    /**
     * Sets the active {@link WebDriver} that is used by this test case
     *
     * @param driver
     *            The WebDriver instance to set.
     */
    public void setDriver(WebDriver driver) {
        if (driver != null && !(driver instanceof TestBenchDriverProxy)) {
            driver = TestBench.createDriver(driver);
        }
        this.driver = driver;
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(
            ExtensionContext context) {
        return CapabilitiesUtil.evaluateExecutionCondition(context);
    }

    /**
     * <p>
     * Returns the complete URL of the hub where the tests will be run on. Used
     * by {@link #beforeEach(ExtensionContext)} ()}, for the creation of the
     * {@link WebDriver}.
     * </p>
     * <p>
     * This method uses {@link #getHubHostname(ExtensionContext)} to build the
     * complete address of the Hub. Override in order to define a different hub
     * address.<br>
     * </p>
     * <p>
     * You can provide sauce.user and sauce.sauceAccessKey system properties or
     * SAUCE_USERNAME and SAUCE_ACCESS_KEY environment variables to run the
     * tests in Sauce Labs. If both system property and environment variable is
     * defined, system property is prioritised.
     * </p>
     *
     * @return the complete URL of the hub where the tests will be run on. Used
     *         by {@link #beforeEach(ExtensionContext)} ()}, for the creation of
     *         the {@link WebDriver}.
     */
    protected String getHubURL(ExtensionContext context) {
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            return SauceLabsIntegration.getHubUrl();
        } else {
            return "http://" + getHubHostname(context) + ":4444/wd/hub";
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
     * This method is used by {@link #getHubURL(ExtensionContext)} to get the
     * full URL of the hub to run tests on.
     * </p>
     *
     * @return the hostname of the hub where test is to be run on.
     */
    protected String getHubHostname(ExtensionContext context) {
        String hubSystemProperty = Parameters.getHubHostname();
        if (hubSystemProperty != null) {
            return hubSystemProperty;
        }

        RunLocally runLocally = context.getRequiredTestClass()
                .getAnnotation(RunLocally.class);
        if (runLocally != null) {
            return "localhost";
        }

        RunOnHub runOnHub = getRunOnHub(context.getRequiredTestClass());
        return runOnHub.value();
    }

    /**
     * <p>
     * Sets test name while using SauceLabs integration, injects
     * {@link WebDriver} and {@link Capabilities} references and sets the driver
     * for this test instance. Uses
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
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        SauceLabsIntegration.setSauceLabsOption(desiredCapabilities,
                SauceLabsIntegration.CapabilityType.NAME,
                context.getDisplayName());

        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof DriverSupplier) {
            setDriver(((DriverSupplier) testInstance).driverSupplier().get());
        } else {
            setupDriver(context);
        }

        if (testInstance instanceof SetDriver) {
            ((SetDriver) testInstance).setDriver(driver);
        }

        if (testInstance instanceof SetCapabilities) {
            ((SetCapabilities) testInstance)
                    .setCapabilities(desiredCapabilities);
        }

    }

    private void setupDriver(ExtensionContext context) throws Exception {
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
                    .setupRemoteDriver(getHubURL(context));
            setDriver(driver);

        } else if (getRunOnHub(getClass()) != null
                || Parameters.getHubHostname() != null) {
            WebDriver driver = driverConfiguration
                    .setupRemoteDriver(getHubURL(context));
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
        return CapabilitiesUtil.getRunLocallyBrowserName(getClass());
    }

    /**
     * @return Version value of the {@link RunLocally} annotation of current
     *         Class, or empty String if annotation is not present.
     */
    protected String getRunLocallyBrowserVersion() {
        return CapabilitiesUtil.getRunLocallyBrowserVersion(getClass());
    }

}
