/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.browser;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.HasDriver;
import com.vaadin.testbench.ParameterizedBrowserTest;
import com.vaadin.testbench.Parameters;
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
public class BrowserExtension implements Extension, BeforeEachCallback,
        ExecutionCondition, HasDriver, ParameterResolver {

    static {
        TestBench.ensureLoaded();
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger(BrowserExtension.class);
    }

    private final SetupDriver driverConfiguration = new SetupDriver();

    private final DesiredCapabilities desiredCapabilities;

    protected WebDriver driver;

    public BrowserExtension(Capabilities capabilities) {
        desiredCapabilities = new DesiredCapabilities(capabilities);
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            SauceLabsIntegration.setDesiredCapabilities(desiredCapabilities);
        }

        driverConfiguration.setDesiredCapabilities(desiredCapabilities);
    }

    public BrowserExtension() {
        this(new DesiredCapabilities());
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

    /**
     * Returns active {@link WebDriver} that used by this test case.
     *
     * @return currently used {@link WebDriver}
     */
    @Override
    public WebDriver getDriver() {
        return driver;
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
     * This method uses {@link #getHubHostname(Class)} to build the complete
     * address of the Hub. Override in order to define a different hub
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
    protected String getHubURL(Class<?> testClass) {
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            return SauceLabsIntegration.getHubUrl();
        } else {
            return String.format("http://%s:%d/wd/hub",
                    getHubHostname(testClass), Parameters.getHubPort());
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
     * This method is used by {@link #getHubURL(Class)} to get the full URL of
     * the hub to run tests on.
     * </p>
     *
     * @return the hostname of the hub where test is to be run on.
     */
    protected String getHubHostname(Class<?> testClass) {
        String hubSystemProperty = Parameters.getHubHostname();
        if (hubSystemProperty != null) {
            return hubSystemProperty;
        }

        RunLocally runLocally = testClass.getAnnotation(RunLocally.class);
        if (runLocally != null) {
            return "localhost";
        }

        RunOnHub runOnHub = getRunOnHub(testClass);
        return runOnHub == null ? null : runOnHub.value();
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
        if (isParameterizedTest(context)) {
            // For parameterized test a new driver should be created for every
            // test when resolving parameters
            return;
        }

        SauceLabsIntegration.setSauceLabsOption(desiredCapabilities,
                SauceLabsIntegration.CapabilityType.NAME,
                context.getDisplayName());

        setupDriver(context);
    }

    static boolean isParameterizedTest(ExtensionContext context) {
        return context.getTestMethod().filter(
                m -> m.isAnnotationPresent(ParameterizedBrowserTest.class))
                .isPresent();
    }

    private void setupDriver(ExtensionContext context) throws Exception {
        setDriver(createDriver(context, driverConfiguration));
    }

    private WebDriver createDriver(ExtensionContext context,
            SetupDriver driverConfig) throws Exception {

        Object testInstance = context.getRequiredTestInstance();

        WebDriver webDriver;

        // use WebDriver provided by test instance
        if (testInstance instanceof DriverSupplier supplier) {
            webDriver = supplier.createDriver();
            if (webDriver != null) {
                return webDriver;
            }
        }

        Class<?> testClass = context.getRequiredTestClass();
        // Always give priority to @RunLocally annotation
        if ((getRunLocallyBrowser(testClass) != null)) {
            webDriver = driverConfig.setupLocalDriver(
                    getRunLocallyBrowser(testClass),
                    getRunLocallyBrowserVersion(testClass));
        } else if (Parameters.isLocalWebDriverUsed()) {
            webDriver = driverConfig.setupLocalDriver();
        } else if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            webDriver = driverConfig.setupRemoteDriver(getHubURL(testClass));
        } else if (getRunOnHub(testClass) != null
                || Parameters.getHubHostname() != null) {
            webDriver = driverConfig.setupRemoteDriver(getHubURL(testClass));
        } else {
            getLogger().info(
                    "Did not find a configuration to run locally, on Sauce Labs or on other test grid. Falling back to running locally on Chrome.");
            webDriver = driverConfig.setupLocalDriver(Browser.CHROME);
        }
        return webDriver;
    }

    /**
     * @param testClass
     *            the test class to get the {@link RunOnHub} annotation from
     * @return Value of the {@link RunOnHub} annotation of passed Class, or null
     *         if annotation is not present.
     */
    protected RunOnHub getRunOnHub(Class<?> testClass) {
        if (testClass == null) {
            return null;
        }

        return testClass.getAnnotation(RunOnHub.class);
    }

    /**
     * @return Browser value of the {@link RunLocally} annotation of current
     *         Class, or null if annotation is not present.
     */
    protected Browser getRunLocallyBrowser(Class<?> testClass) {
        return CapabilitiesUtil.getRunLocallyBrowserName(testClass);
    }

    /**
     * @return Version value of the {@link RunLocally} annotation of current
     *         Class, or empty String if annotation is not present.
     */
    protected String getRunLocallyBrowserVersion(Class<?> testClass) {
        return CapabilitiesUtil.getRunLocallyBrowserVersion(testClass);
    }

    /**
     * @return Current instance of {@link DesiredCapabilities}.
     */
    public DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType() == BrowserTestInfo.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext)
            throws ParameterResolutionException {
        if (isParameterizedTest(extensionContext)) {
            // For parameterized tests create a new driver for each test
            // execution
            return getStore(extensionContext).getOrComputeIfAbsent(
                    BrowserTestInfo.class,
                    k -> createTestInfoForParameterizedTest(extensionContext));
        }
        Class<?> testClass = extensionContext.getRequiredTestClass();
        return new BrowserTestInfo(driver,
                new ImmutableCapabilities(desiredCapabilities),
                getHubHostname(testClass), getRunLocallyBrowser(testClass),
                getRunLocallyBrowserVersion(testClass));
    }

    private BrowserTestInfo createTestInfoForParameterizedTest(
            ExtensionContext context) {
        DesiredCapabilities capabilities = CapabilitiesUtil
                .getDesiredCapabilities(context).stream().findFirst()
                .orElseThrow(() -> new ParameterResolutionException(
                        "Cannot get capabilities"));
        if (SauceLabsIntegration.isConfiguredForSauceLabs()) {
            SauceLabsIntegration.setDesiredCapabilities(capabilities);
        }
        SauceLabsIntegration.setSauceLabsOption(capabilities,
                SauceLabsIntegration.CapabilityType.NAME,
                context.getDisplayName());

        WebDriver webDriver;
        try {
            SetupDriver driverConfig = new SetupDriver();
            driverConfig.setDesiredCapabilities(capabilities);
            webDriver = createDriver(context, driverConfig);
        } catch (Exception e) {
            throw new ParameterResolutionException("Cannot create WebDriver",
                    e);
        }

        Class<?> testClass = context.getRequiredTestClass();
        return new BrowserTestInfo(webDriver,
                new ImmutableCapabilities(capabilities),
                getHubHostname(testClass), getRunLocallyBrowser(testClass),
                getRunLocallyBrowserVersion(testClass));
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace
                .create(BrowserExtension.class, context.getUniqueId()));
    }

}
