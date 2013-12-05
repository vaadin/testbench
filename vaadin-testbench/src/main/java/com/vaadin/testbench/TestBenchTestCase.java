package com.vaadin.testbench;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.commands.TestBenchElementCommands;

/**
 * A superclass with some helpers to aid TestBench developers. This superclass
 * is also used by tests created by the Recorder.
 */
public abstract class TestBenchTestCase extends
        AbstractHasTestBenchCommandExecutor implements HasDriver {

    protected WebDriver driver;

    /**
     * Convenience method that casts the specified {@link WebDriver} instance to
     * an instance of {@link TestBenchCommands}, making it easy to access the
     * special TestBench commands.
     * 
     * @param webDriver
     *            The WebDriver instance to cast.
     * @return a WebDriver cast to TestBenchCommands
     */
    public static TestBenchCommands testBench(WebDriver webDriver) {
        return (TestBenchCommands) webDriver;
    }

    /**
     * Convenience method the return {@link TestBenchCommands} for the default
     * {@link WebDriver} instance.
     * 
     * @return The driver cast to a TestBenchCommands instance.
     */
    public TestBenchCommands testBench() {
        return (TestBenchCommands) getDriver();
    }

    /**
     * Convenience method that casts the specified {@link WebElement} instance
     * to an instance of {@link TestBenchElementCommands}, making it easy to
     * access the special TestBench commands.
     * 
     * @param webElement
     *            The WebElement to cast.
     * @return The WebElement cast to a TestBenchElementCommands instance.
     */
    public TestBenchElementCommands testBenchElement(WebElement webElement) {
        return (TestBenchElementCommands) webElement;
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
     * Returns the {@link WebDriver} instance previously specified by {@link
     * setDriver()}, or (if the previously provided WebDriver instance was not
     * already a {@link TestBenchDriverProxy} instance) a
     * {@link TestBenchDriverProxy} that wraps that driver.
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
        if (!(driver instanceof TestBenchDriverProxy)) {
            driver = TestBench.createDriver(driver);
        }
        this.driver = driver;
    }

    @Override
    public SearchContext getContext() {
        return driver;
    }

    @Override
    public TestBenchCommandExecutor getTestBenchCommandExecutor() {
        return ((HasTestBenchCommandExecutor) driver)
                .getTestBenchCommandExecutor();
    }
}
