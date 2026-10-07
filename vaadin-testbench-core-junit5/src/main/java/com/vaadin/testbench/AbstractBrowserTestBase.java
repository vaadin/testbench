/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;

/**
 * A superclass with helper methods to aid TestBench developers create a JUnit 5
 * based tests.
 */
public abstract class AbstractBrowserTestBase
        implements HasDriver, HasTestBenchCommandExecutor, HasElementQuery {

    static {
        TestBench.ensureLoaded();
    }

    /**
     * When an error occurs during establishing a WebSocket connection, a severe
     * error is added to the console by the browser. We can't prevent it, so we
     * have to ignore it for now until we figure out a way to supress it.
     */
    private static final String WEB_SOCKET_CONNECTION_ERROR_PREFIX = "WebSocket connection to ";

    /**
     * Returns the {@link WebDriver} instance or (if the previously provided
     * WebDriver instance was not already a {@link TestBenchDriverProxy}
     * instance) a {@link TestBenchDriverProxy} that wraps that driver.
     *
     * @return the active WebDriver instance
     */
    public abstract WebDriver getDriver();

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
     * @param <T>
     *            the type of the {@link TestBenchElement} to return
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
     *         {@link JavascriptExecutor#executeScript(String, Object...)}
     *         returns
     * @throws UnsupportedOperationException
     *             if the underlying driver does not support JavaScript
     *             execution
     * @see JavascriptExecutor#executeScript(String, Object...)
     */
    public Object executeScript(String script, Object... args) {
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
     * @param <T>
     *            The return type of the {@link ExpectedCondition} and this
     *            method
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
    public <T> T waitUntil(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        return new WebDriverWait(getDriver(),
                Duration.ofSeconds(timeoutInSeconds)).until(condition);
    }

    /**
     * Waits up to 10 seconds for the given condition to become neither null nor
     * false. {@link NotFoundException}s are ignored by default.
     * <p>
     * Use e.g. as
     * <code>waitUntil(ExpectedConditions.presenceOfElementLocated(by));</code>
     *
     * @param <T>
     *            The return type of the {@link ExpectedCondition} and this
     *            method
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
    public <T> T waitUntil(ExpectedCondition<T> condition) {
        return waitUntil(condition, 10);
    }

    @Override
    public SearchContext getContext() {
        return getDriver();
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return ((HasTestBenchCommandExecutor) getDriver()).getCommandExecutor();
    }

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
    public static String concatUrl(String baseUrl, String uri) {
        if (baseUrl.endsWith("/") && uri.startsWith("/")) {
            return baseUrl + uri.substring(1);
        }
        return baseUrl + uri;
    }

    /**
     * Waits up to 10s for the given condition to become false. Use e.g. as
     * {@link #waitUntilNot(ExpectedCondition)}.
     *
     * @param condition
     *            the condition to wait for to become false
     * @param <T>
     *            the return type of the expected condition
     */
    protected <T> void waitUntilNot(ExpectedCondition<T> condition) {
        waitUntilNot(condition, 10);
    }

    /**
     * Returns true if an element can be found from the driver with given
     * selector.
     *
     * @param by
     *            the selector used to find element
     * @return true if the element can be found
     */
    public boolean isElementPresent(By by) {
        try {
            WebElement element = getDriver().findElement(by);
            return element != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Simulate DnD of {@code source} element into the {@code target} element.
     *
     * @param source
     * @param target
     */
    public void dragAndDrop(WebElement source, WebElement target) {
        getCommandExecutor().executeScript(LazyDndSimulationLoad.DND_SCRIPT,
                source, target, "DND");
    }

    /**
     * Simulate only a drag of {@code source}.
     *
     * @param source
     */
    public void drag(WebElement source) {
        getCommandExecutor().executeScript(LazyDndSimulationLoad.DND_SCRIPT,
                source, null, "DRAG");
    }

    /**
     * Simulate a drag of {@code source} element and over the {@code target}
     * element.
     *
     * @param source
     * @param target
     */
    public void dragElementOver(WebElement source, WebElement target) {
        getCommandExecutor().executeScript(LazyDndSimulationLoad.DND_SCRIPT,
                source, target, "DRAG_OVER");
    }

    /**
     * Waits the given number of seconds for the given condition to become
     * false. Use e.g. as {@link #waitUntilNot(ExpectedCondition)}.
     *
     * @param condition
     *            the condition to wait for to become false
     * @param timeoutInSeconds
     *            the number of seconds to wait
     * @param <T>
     *            the return type of the expected condition
     */
    protected <T> void waitUntilNot(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        waitUntil(ExpectedConditions.not(condition), timeoutInSeconds);
    }

    protected void waitForElementPresent(final By by) {
        waitUntil(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected void waitForElementNotPresent(final By by) {
        waitUntil(input -> input.findElements(by).isEmpty());
    }

    protected void waitForElementVisible(final By by) {
        waitUntil(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Checks if the given element has the given class name.
     *
     * Matches only full class names, i.e. has ("foo") does not match
     * class="foobar"
     *
     * @param element
     *            the element to test
     * @param className
     *            the class names to match
     * @return <code>true</code> if matches, <code>false</code> if not
     */
    protected boolean hasCssClass(WebElement element, String className) {
        String classes = element.getDomAttribute("class");
        if (classes == null || classes.isEmpty()) {
            return className == null || className.isEmpty();
        }
        return Stream.of(classes.split(" ")).anyMatch(className::equals);
    }

    /**
     * Assert that the two elements are equal.
     * <p>
     * Can be removed if https://dev.vaadin.com/ticket/18484 is fixed.
     *
     * @param expectedElement
     *            the expected element
     * @param actualElement
     *            the actual element
     */
    protected static void assertEquals(WebElement expectedElement,
            WebElement actualElement) {
        WebElement unwrappedExpected = expectedElement;
        WebElement unwrappedActual = actualElement;
        while (unwrappedExpected instanceof WrapsElement) {
            unwrappedExpected = ((WrapsElement) unwrappedExpected)
                    .getWrappedElement();
        }
        while (unwrappedActual instanceof WrapsElement) {
            unwrappedActual = ((WrapsElement) unwrappedActual)
                    .getWrappedElement();
        }
        Assertions.assertEquals(unwrappedExpected, unwrappedActual);
    }

    /**
     * Scrolls the page by given amount of x and y deltas. Actual scroll values
     * can be different if any delta is bigger then the corresponding document
     * dimension.
     *
     * @param deltaX
     *            the offset in pixels to scroll horizontally
     * @param deltaY
     *            the offset in pixels to scroll vertically
     */
    protected void scrollBy(int deltaX, int deltaY) {
        executeScript("window.scrollBy(" + deltaX + ',' + deltaY + ");");
    }

    /**
     * Scrolls the page to the element given using javascript.
     *
     * Standard Selenium api does not work for current newest Chrome and
     * ChromeDriver.
     *
     * @param element
     *            the element to scroll to, not {@code null}
     */
    protected void scrollToElement(WebElement element) {
        Objects.requireNonNull(element,
                "The element to scroll to should not be null");
        getCommandExecutor().executeScript("arguments[0].scrollIntoView(true);",
                element);
    }

    /**
     * Scrolls the page to the element specified and clicks it.
     *
     * @param element
     *            the element to scroll to and click
     */
    protected void scrollIntoViewAndClick(WebElement element) {
        scrollToElement(element);
        element.click();
    }

    /**
     * Gets current scroll position on x axis.
     *
     * @return current scroll position on x axis.
     */
    protected int getScrollX() {
        return ((Number) executeScript("return window.pageXOffset")).intValue();
    }

    /**
     * Gets current scroll position on y axis.
     *
     * @return current scroll position on y axis.
     */
    protected int getScrollY() {
        return ((Number) executeScript("return window.pageYOffset")).intValue();
    }

    /**
     * Clicks on the element, using JS. This method is more convenient then
     * Selenium {@code findElement(By.id(urlId)).click()}, because Selenium
     * method changes scroll position, which is not always needed.
     *
     * @param elementId
     *            id of the
     */
    protected void clickElementWithJs(String elementId) {
        executeScript(String.format("document.getElementById('%s').click();",
                elementId));
    }

    /**
     * Clicks on the element, using JS. This method is more convenient then
     * Selenium {@code element.click()}, because Selenium method changes scroll
     * position, which is not always needed.
     *
     * @param element
     *            the element to be clicked on
     */
    protected void clickElementWithJs(WebElement element) {
        executeScript("arguments[0].click();", element);
    }

    /**
     * Gets the log entries from the browser that have the given logging level
     * or higher.
     *
     * @param level
     *            the minimum severity of logs included
     * @return log entries from the browser
     */
    protected List<LogEntry> getLogEntries(Level level) {
        // https://github.com/vaadin/testbench/issues/1233
        getCommandExecutor().waitForVaadin();

        return getDriver().manage().logs().get(LogType.BROWSER).getAll()
                .stream()
                .filter(logEntry -> logEntry.getLevel().intValue() >= level
                        .intValue())
                // we always have this error
                .filter(logEntry -> !logEntry.getMessage()
                        .contains("favicon.ico"))
                .collect(Collectors.toList());
    }

    /**
     * Checks browser's log entries, throws an error for any client-side error
     * and logs any client-side warnings.
     *
     * @param acceptableMessagePredicate
     *            allows to ignore log entries whose message is accaptable
     *
     * @throws AssertionError
     *             if an error is found in the browser logs
     */
    protected void checkLogsForErrors(
            Predicate<String> acceptableMessagePredicate) {
        getLogEntries(Level.WARNING).forEach(logEntry -> {
            if ((Objects.equals(logEntry.getLevel(), Level.SEVERE)
                    || logEntry.getMessage().contains(" 404 "))
                    && !logEntry.getMessage()
                            .contains(WEB_SOCKET_CONNECTION_ERROR_PREFIX)
                    && !acceptableMessagePredicate
                            .test(logEntry.getMessage())) {
                throw new AssertionError(String.format(
                        "Received error message in browser log console right after opening the page, message: %s",
                        logEntry));
            } else {
                LoggerFactory.getLogger(AbstractBrowserTestBase.class.getName())
                        .warn("This message in browser log console may be a potential error: '{}'",
                                logEntry);
            }
        });
    }

    /**
     * Checks browser's log entries, throws an error for any client-side error
     * and logs any client-side warnings.
     *
     * @throws AssertionError
     *             if an error is found in the browser logs
     */
    protected void checkLogsForErrors() {
        checkLogsForErrors(msg -> false);
    }

    /**
     * If dev server start in progress wait until it's started. Otherwise return
     * immidiately.
     */
    protected void waitForDevServer() {
        Object result;
        do {
            getCommandExecutor().waitForVaadin();
            result = getCommandExecutor().executeScript(
                    "return window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.devServerIsNotLoaded;");
        } while (Boolean.TRUE.equals(result));
    }

    /**
     * Calls the {@code blur()} function on the current active element of the
     * page, if any.
     */
    public void blur() {
        executeScript(
                "!!document.activeElement ? document.activeElement.blur() : 0");
    }

    private static class LazyDndSimulationLoad {
        private static final String DND_SCRIPT = loadDndScript(
                "/dnd-simulation.js");

        private static String loadDndScript(String scriptLocation) {
            InputStream stream = AbstractBrowserTestBase.class
                    .getResourceAsStream(scriptLocation);
            return new BufferedReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining("\n"));
        }
    }

}
