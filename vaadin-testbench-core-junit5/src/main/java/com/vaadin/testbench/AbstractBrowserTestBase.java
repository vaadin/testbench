/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.backend.selenium.SeleniumBackend;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;

/**
 * A superclass with helper methods to aid TestBench developers create a JUnit
 * 5+ based tests.
 *
 * @since 9.0
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

    /** @return {@code null} before the browser is set up */
    @Override
    public abstract BrowserDriver getDriver();

    /** As {@link BrowserDriver#findElement(By)}. */
    @SuppressWarnings("deprecation")
    public TestBenchElement findElement(By by) {
        return getDriver().findElement(by);
    }

    /** As {@link BrowserDriver#findElements(By)}. */
    @SuppressWarnings("deprecation")
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
    public Object executeScript(String script, Object... args) {
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
    public <T> T waitUntil(Function<BrowserDriver, T> condition,
            long timeoutInSeconds) {
        BrowserDriver browser = getDriver();
        return Wait.until(() -> condition.apply(browser),
                Duration.ofSeconds(timeoutInSeconds));
    }

    /** As {@link #waitUntil(Function, long)}, for up to 10 seconds. */
    public <T> T waitUntil(Function<BrowserDriver, T> condition) {
        return waitUntil(condition, 10);
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
    public static String concatUrl(String baseUrl, String uri) {
        if (baseUrl.endsWith("/") && uri.startsWith("/")) {
            return baseUrl + uri.substring(1);
        }
        return baseUrl + uri;
    }

    /**
     * Waits up to 10s for the given condition to become false.
     *
     * @param condition
     *            the condition to wait for to become false
     * @param <T>
     *            the return type of the expected condition
     */
    protected <T> void waitUntilNot(Function<BrowserDriver, T> condition) {
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
    @SuppressWarnings("deprecation")
    public boolean isElementPresent(By by) {
        try {
            return getDriver().findElement(by) != null;
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
    public void dragAndDrop(TestBenchElement source, TestBenchElement target) {
        getCommandExecutor().executeScript(LazyDndSimulationLoad.DND_SCRIPT,
                source, target, "DND");
    }

    /**
     * Simulate only a drag of {@code source}.
     *
     * @param source
     */
    public void drag(TestBenchElement source) {
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
    public void dragElementOver(TestBenchElement source,
            TestBenchElement target) {
        getCommandExecutor().executeScript(LazyDndSimulationLoad.DND_SCRIPT,
                source, target, "DRAG_OVER");
    }

    /**
     * Waits the given number of seconds for the given condition to become
     * false: {@code null}, {@code false}, or an exception.
     *
     * @param condition
     *            the condition to wait for to become false
     * @param timeoutInSeconds
     *            the number of seconds to wait
     * @param <T>
     *            the return type of the expected condition
     */
    protected <T> void waitUntilNot(Function<BrowserDriver, T> condition,
            long timeoutInSeconds) {
        // As Selenium's ExpectedConditions.not
        waitUntil(driver -> {
            try {
                T result = condition.apply(driver);
                return result == null || Boolean.FALSE.equals(result);
            } catch (RuntimeException e) {
                return true;
            }
        }, timeoutInSeconds);
    }

    @SuppressWarnings("deprecation")
    protected void waitForElementPresent(final By by) {
        waitUntil(driver -> driver.findElement(by));
    }

    @SuppressWarnings("deprecation")
    protected void waitForElementNotPresent(final By by) {
        waitUntil(driver -> driver.findElements(by).isEmpty());
    }

    // As Selenium's ExpectedConditions.visibilityOfElementLocated
    @SuppressWarnings("deprecation")
    protected void waitForElementVisible(final By by) {
        waitUntil(driver -> {
            try {
                return driver.findElement(by).isDisplayed();
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
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
    protected boolean hasCssClass(TestBenchElement element, String className) {
        String classes = element.getDomAttribute("class");
        if (classes == null || classes.isEmpty()) {
            return className == null || className.isEmpty();
        }
        return Stream.of(classes.split(" ")).anyMatch(className::equals);
    }

    /**
     * Assert that the two elements are equal.
     * <p>
     * This is a workaround from a time when {@link TestBenchElement} did not
     * implement {@link Object#equals(Object)} and wrapped elements therefore
     * had to be unwrapped before they could be compared.
     *
     * @param expectedElement
     *            the expected element
     * @param actualElement
     *            the actual element
     * @deprecated since 25.3, for removal; use
     *             {@link Assertions#assertEquals(Object, Object)} instead.
     *             {@link TestBenchElement} implements
     *             {@link Object#equals(Object)}, so wrapped and unwrapped
     *             elements are compared correctly without unwrapping them
     *             first. This method also hides a statically imported
     *             {@code assertEquals} in subclasses, which is another reason
     *             to use {@link Assertions} directly.
     */
    @Deprecated(since = "25.3", forRemoval = true)
    protected static void assertEquals(TestBenchElement expectedElement,
            TestBenchElement actualElement) {
        Assertions.assertEquals(expectedElement, actualElement);
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
    protected void scrollToElement(TestBenchElement element) {
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
    protected void scrollIntoViewAndClick(TestBenchElement element) {
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
    protected void clickElementWithJs(TestBenchElement element) {
        executeScript("arguments[0].click();", element);
    }

    /**
     * Gets the log entries from the browser that have the given logging level
     * or higher. Selenium only, so far.
     *
     * @param level
     *            the minimum severity of logs included
     * @return log entries from the browser
     */
    protected List<LogEntry> getLogEntries(Level level) {
        // https://github.com/vaadin/testbench/issues/1233
        getCommandExecutor().waitForVaadin();

        if (!(getCommandExecutor()
                .getBackend() instanceof SeleniumBackend selenium)) {
            throw NotYetImplementedException
                    .notYetImplemented("getLogEntries off Selenium");
        }
        return selenium.getDriver().manage().logs().get(LogType.BROWSER)
                .getAll().stream()
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
            try (InputStream stream = AbstractBrowserTestBase.class
                    .getResourceAsStream(scriptLocation)) {
                return new String(stream.readAllBytes(),
                        StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

}
