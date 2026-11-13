/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;

/**
 * A superclass with some helpers to aid TestBench developers.
 */
public abstract class TestBenchTestCase
        implements HasDriver, HasTestBenchCommandExecutor, HasElementQuery {

    static {
        TestBench.ensureLoaded();
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

    protected BrowserDriver driver;

    private static Logger getLogger() {
        return LoggerFactory.getLogger(TestBenchTestCase.class);
    }

    /**
     * Convenience method the return {@link TestBenchCommands} for the current
     * browser.
     */
    public TestBenchCommands testBench() {
        return getDriver().getCommandExecutor();
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

    /** @return {@code null} before {@link #setDriver} */
    @Override
    public BrowserDriver getDriver() {
        return driver;
    }

    public void setDriver(BrowserDriver driver) {
        this.driver = driver;
    }

    /**
     * Sets a driver this test built with a backend's library, as TestBench's
     * {@link BrowserDriver}, on that backend:
     *
     * <pre>{@code
     * setDriver(new ChromeDriver());
     * }</pre>
     *
     * @param driver
     *            a Selenium {@code WebDriver}, a Playwright {@code Browser},
     *            {@code BrowserContext} or {@code Page}, or a
     *            {@link BrowserDriver}
     * @see TestBench#createDriver(Object)
     */
    public void setDriver(Object driver) {
        setDriver(driver == null || driver instanceof BrowserDriver
                ? (BrowserDriver) driver
                : TestBench.createDriver(driver));
    }

    /** Searches the page. */
    @Override
    public <T extends TestBenchElement> ElementQuery<T> $(Class<T> clazz) {
        return new ElementQuery<>(clazz).onPage(getCommandExecutor());
    }

    /** Searches the page. */
    @Override
    public ElementQuery<TestBenchElement> $(String tagName) {
        return new ElementQuery<>(TestBenchElement.class, tagName)
                .onPage(getCommandExecutor());
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return getDriver().getCommandExecutor();
    }

    /** As {@link BrowserDriver#findElement(By)}. */
    public TestBenchElement findElement(By by) {
        return getDriver().findElement(by);
    }

    /** As {@link BrowserDriver#findElements(By)}. */
    public List<TestBenchElement> findElements(By by) {
        return getDriver().findElements(by);
    }

    /**
     * Decorates the element with the specified Element type, making it possible
     * to use component-specific API on an element found as a plain
     * {@link TestBenchElement}.
     *
     * @param <T>
     *            the type of the {@link TestBenchElement} to return
     * @param elementType
     *            The type (class) containing the API to decorate with
     * @param element
     *            The element instance to decorate
     * @return The element wrapped in an instance of the specified element type.
     */
    public <T extends TestBenchElement> T wrap(Class<T> elementType,
            TestBenchElement element) {
        return TestBench.wrap(element, elementType);
    }

    /**
     * Executes the given JavaScript in the context of the currently selected
     * frame or window. The script fragment provided will be executed as the
     * body of an anonymous function.
     * <p>
     * A DOM node comes back as a {@link TestBenchElement}.
     *
     * @param script
     *            the script to execute
     * @param args
     *            the arguments, available in the script as
     *            {@code arguments[0]...arguments[N]}
     * @see BrowserDriver#executeScript(String, Object...)
     */
    protected Object executeScript(String script, Object... args) {
        return getCommandExecutor().executeScript(script, args);
    }

    /**
     * Waits until {@code condition} returns neither {@code null} nor
     * {@code false}, asking it every 500 ms; a {@link NoSuchElementException}
     * it throws counts as not yet.
     *
     * <pre>{@code
     * waitUntil(driver -> $(SideNavElement.class).exists(), 1);
     * }</pre>
     *
     * @return what {@code condition} returned last
     * @throws TimeoutException
     *             if the time is up
     */
    protected <T> T waitUntil(Function<BrowserDriver, T> condition,
            long timeoutInSeconds) {
        BrowserDriver browser = getDriver();
        // The condition's toString names it in the timeout message, as in
        // Selenium's WebDriverWait
        return Wait.until(new Supplier<>() {
            @Override
            public T get() {
                return condition.apply(browser);
            }

            @Override
            public String toString() {
                return condition.toString();
            }
        }, Duration.ofSeconds(timeoutInSeconds));
    }

    /** As {@link #waitUntil(Function, long)}, for up to 10 seconds. */
    protected <T> T waitUntil(Function<BrowserDriver, T> condition) {
        return waitUntil(condition, 10);
    }

}
