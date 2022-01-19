/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Rule;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.BuildInfo;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.pro.licensechecker.LicenseChecker;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;

/**
 * A superclass with some helpers to aid TestBench developers.
 */
public abstract class TestBenchTestCase
        implements HasDriver, HasTestBenchCommandExecutor, HasElementQuery {

    public static final String testbenchVersion;
    static {
        Properties properties = new Properties();
        try {
            properties.load(TestBenchTestCase.class
                    .getResourceAsStream("testbench.properties"));
        } catch (Exception e) {
            Logger.getLogger(TestBenchTestCase.class.getName()).log(
                    Level.WARNING, "Unable to read TestBench properties file",
                    e);
            throw new ExceptionInInitializerError(e);
        }

        String seleniumVersion = new BuildInfo().getReleaseLabel();
        testbenchVersion = properties.getProperty("testbench.version");
        String expectedVersion = properties.getProperty("selenium.version");
        if (seleniumVersion == null
                || !seleniumVersion.equals(expectedVersion)) {
            Logger.getLogger(TestBenchTestCase.class.getName()).warning(
                    "This version of TestBench depends on Selenium version "
                            + expectedVersion + " but version "
                            + seleniumVersion
                            + " was found. Make sure you do not have multiple versions of Selenium on the classpath.");
        }

        LicenseChecker.checkLicenseFromStaticBlock("vaadin-testbench",
                TestBenchTestCase.testbenchVersion);
    }

    /**
     * Specifies retry count, which is used to run same test several times. Can
     * be changed by setting "com.vaadin.testbench.Parameters.maxAttempts"
     * system property.
     *
     * Default: 1
     */
    @Rule
    public RetryRule maxAttempts = new RetryRule(Parameters.getMaxAttempts());

    protected WebDriver driver;

    /**
     * Convenience method the return {@link TestBenchCommands} for the default
     * {@link WebDriver} instance.
     *
     * @return The driver cast to a TestBenchCommands instance.
     */
    public TestBenchCommands testBench() {
        return ((TestBenchDriverProxy) getDriver()).getCommandExecutor();
    }

    /**
     * Combines a base URL with an URI to create a final URL. This removes
     * possible double slashes if the base URL ends with a slash and the URI
     * begins with a slash.
     *
     * @param baseUrl
     *            the base URL
     * @param uri
     *            the URI
     * @return the URL resulting from the combination of base URL and URI
     */
    protected String concatUrl(String baseUrl, String uri) {
        if (baseUrl.endsWith("/") && uri.startsWith("/")) {
            return baseUrl + uri.substring(1);
        }
        return baseUrl + uri;
    }

    /**
     * Returns the {@link WebDriver} instance previously specified by
     * {@link #setDriver(org.openqa.selenium.WebDriver)}, or (if the previously
     * provided WebDriver instance was not already a
     * {@link TestBenchDriverProxy} instance) a {@link TestBenchDriverProxy}
     * that wraps that driver.
     *
     * @return the active WebDriver instance
     */
    @Override
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Sets the active {@link WebDriver} that is used by this this case
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
    public SearchContext getContext() {
        return getDriver();
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return ((HasTestBenchCommandExecutor) getDriver()).getCommandExecutor();
    }

    public WebElement findElement(org.openqa.selenium.By by) {
        return getContext().findElement(by);
    }

    public List<WebElement> findElements(org.openqa.selenium.By by) {
        return getContext().findElements(by);
    }

    /**
     * Decorates the element with the specified Element type, making it possible
     * to use component-specific API on elements found using standard Selenium
     * API.
     *
     * @param elementType
     *            The type (class) containing the API to decorate with
     * @param element
     *            The element instance to decorate
     * @return The element wrapped in an instance of the specified element type.
     */
    public <T extends TestBenchElement> T wrap(Class<T> elementType,
            WebElement element) {
        return ((TestBenchElement) element).wrap(elementType);
    }

    /**
     * Executes the given JavaScript in the context of the currently selected
     * frame or window. The script fragment provided will be executed as the
     * body of an anonymous function.
     * <p>
     * This method wraps any returned {@link WebElement} as
     * {@link TestBenchElement}.
     *
     * @param script
     *            the script to execute
     * @param args
     *            the arguments, available in the script as
     *            {@code arguments[0]...arguments[N]}
     * @return whatever
     *         {@link org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)}
     *         returns
     * @throws UnsupportedOperationException
     *             if the underlying driver does not support JavaScript
     *             execution
     * @see JavascriptExecutor#executeScript(String, Object...)
     */
    protected Object executeScript(String script, Object... args) {
        return getCommandExecutor().executeScript(script, args);
    }

    /**
     * Waits the given number of seconds for the given condition to become
     * neither null nor false. {@link NotFoundException}s are ignored by
     * default.
     * <p>
     * Use e.g. as
     * <code>waitUntil(ExpectedConditions.presenceOfElementLocated(by), 10);</code>
     *
     * @param condition
     *            Models a condition that might reasonably be expected to
     *            eventually evaluate to something that is neither null nor
     *            false.
     * @param timeoutInSeconds
     *            The timeout in seconds for the wait.
     * @return The condition's return value if it returned something different
     *         from null or false before the timeout expired.
     *
     * @throws TimeoutException
     *             If the timeout expires.
     *
     * @see FluentWait#until
     * @see ExpectedCondition
     */
    protected <T> T waitUntil(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        return new WebDriverWait(getDriver(), timeoutInSeconds)
                .until(condition);
    }

    /**
     * Waits up to 10 seconds for the given condition to become neither null nor
     * false. {@link NotFoundException}s are ignored by default.
     * <p>
     * Use e.g. as
     * <code>waitUntil(ExpectedConditions.presenceOfElementLocated(by));</code>
     *
     * @param condition
     *            Models a condition that might reasonably be expected to
     *            eventually evaluate to something that is neither null nor
     *            false.
     * @return The condition's return value if it returned something different
     *         from null or false before the timeout expired.
     *
     * @throws TimeoutException
     *             If 10 seconds passed.
     *
     * @see FluentWait#until
     * @see ExpectedCondition
     */
    protected <T> T waitUntil(ExpectedCondition<T> condition) {
        return waitUntil(condition, 10);
    }

}
