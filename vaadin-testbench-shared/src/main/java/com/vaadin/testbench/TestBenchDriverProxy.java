/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Sequence;

import com.vaadin.testbench.backend.selenium.SeleniumBackend;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/**
 * TestBench's view of a Selenium {@link WebDriver}: it waits for Vaadin before
 * each call, except navigation, window management and closing, and turns found
 * elements into {@link TestBenchElement}s.
 * <p>
 * Around a browser on another backend (a driver that is a
 * {@link com.vaadin.testbench.backend.HasBackend}), scripts and finds go
 * through that backend; {@code findElement} takes the {@code By}s Selenium
 * sends as CSS, not {@code xpath} or the link texts, and {@code Actions} do not
 * work.
 * <p>
 * Only the interfaces it implements are there: a cast to anything else the real
 * driver implements needs {@link #getWrappedDriver()}.
 */
public class TestBenchDriverProxy
        implements WebDriver, WrapsDriver, HasTestBenchCommandExecutor,
        HasCapabilities, TakesScreenshot, JavascriptExecutor, Interactive {

    private final WebDriver wrappedDriver;
    private final TestBenchCommandExecutor commandExecutor;

    protected TestBenchDriverProxy(WebDriver webDriver,
            TestBenchCommandExecutor commandExecutor) {
        wrappedDriver = webDriver;
        this.commandExecutor = commandExecutor;
    }

    private void waitForVaadin() {
        commandExecutor.waitForVaadin();
    }

    @Override
    public void close() {
        getWrappedDriver().close();
    }

    /**
     * @return a {@link WebElement} view of a {@link TestBenchElement}, whose
     *         calls wait for Vaadin as the element's do
     */
    @Override
    public WebElement findElement(By by) {
        if (!onSelenium()) {
            List<WebElement> found = findElements(by);
            if (found.isEmpty()) {
                throw new NoSuchElementException(
                        "Unable to locate an element with " + by);
            }
            return found.get(0);
        }
        waitForVaadin();
        return TestBench.createElement(wrappedDriver.findElement(by),
                getCommandExecutor()).asWebElement();
    }

    @Override
    public List<WebElement> findElements(By by) {
        if (!onSelenium()) {
            @SuppressWarnings("unchecked")
            List<TestBenchElement> found = (List<TestBenchElement>) commandExecutor
                    .executeScript(
                            "return Array.from(document.querySelectorAll(arguments[0]))",
                            toCssSelector(by));
            return found.stream().map(TestBenchElement::asWebElement).toList();
        }
        waitForVaadin();
        return wrappedDriver.findElements(by).stream()
                .map(found -> TestBench
                        .createElement(found, getCommandExecutor())
                        .asWebElement())
                .toList();
    }

    @Override
    public void get(String url) {
        wrappedDriver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        waitForVaadin();
        return wrappedDriver.getCurrentUrl();
    }

    @Override
    public String getPageSource() {
        waitForVaadin();
        return wrappedDriver.getPageSource();
    }

    @Override
    public String getTitle() {
        waitForVaadin();
        return wrappedDriver.getTitle();
    }

    @Override
    public String getWindowHandle() {
        waitForVaadin();
        return wrappedDriver.getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
        waitForVaadin();
        return wrappedDriver.getWindowHandles();
    }

    @Override
    public Options manage() {
        return wrappedDriver.manage();
    }

    @Override
    public Navigation navigate() {
        return wrappedDriver.navigate();
    }

    @Override
    public void quit() {
        wrappedDriver.quit();
    }

    @Override
    public TargetLocator switchTo() {
        return wrappedDriver.switchTo();
    }

    @Override
    public TestBenchCommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    @Override
    public Capabilities getCapabilities() {
        waitForVaadin();
        if (wrappedDriver instanceof HasCapabilities) {
            return ((HasCapabilities) wrappedDriver).getCapabilities();
        }
        return null;
    }

    @Override
    public WebDriver getWrappedDriver() {
        return wrappedDriver;
    }

    /**
     * Wraps any {@link WebElement} found inside the object inside a
     * {@link TestBenchElement}.
     * <p>
     * Traverses through any {@link List} found inside the object and wraps any
     * elements inside the list, recursively. The behavior is compatible with
     * what {@link #executeScript(String, Object...)} and
     * {@link #executeAsyncScript(String, Object...)} returns.
     * <p>
     * Does not modify the argument, instead creates a new object containing the
     * wrapped elements and other possible values.
     * <p>
     * This method is protected for testing purposes only.
     *
     * @param elementElementsOrValues
     *            an object containing a {@link WebElement}, a {@link List} of
     *            {@link WebElement WebElements} or something completely
     *            different.
     * @param tbCommandExecutor
     *            the {@link TestBenchCommandExecutor} related to the driver
     *            instance
     * @return a new object containing the wrapped elements and other possible
     *         values
     */
    protected static Object wrapElementOrElements(
            Object elementElementsOrValues,
            TestBenchCommandExecutor tbCommandExecutor) {
        if (elementElementsOrValues instanceof List) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            List<Object> list = (List) elementElementsOrValues;
            List<Object> newList = new ArrayList<>();

            for (Object value : list) {
                newList.add(wrapElementOrElements(value, tbCommandExecutor));
            }
            return newList;
        } else if (elementElementsOrValues instanceof WebElement element) {
            return TestBench.createElement(element, tbCommandExecutor);
        } else {
            return elementElementsOrValues;
        }
    }

    // Off Selenium the browser is reached through the backend, whose scripts
    // and finds are TestBench's own
    private boolean onSelenium() {
        return commandExecutor.getBackend() instanceof SeleniumBackend;
    }

    // Selenium's By.id, className, name, tagName and cssSelector all reach the
    // driver as CSS; xpath and the link texts do not
    private static String toCssSelector(By by) {
        if (by instanceof By.Remotable remotable) {
            By.Remotable.Parameters parameters = remotable
                    .getRemoteParameters();
            if ("css selector".equals(parameters.using())) {
                return String.valueOf(parameters.value());
            }
        }
        throw NotYetImplementedException
                .notYetImplemented("findElement(" + by + ") off Selenium");
    }

    // A WebElementAdapter, at any depth, back to its element
    private static Object[] fromSelenium(Object[] args) {
        return Arrays.stream(args).map(TestBenchDriverProxy::fromSelenium)
                .toArray();
    }

    private static Object fromSelenium(Object value) {
        if (value instanceof WebElementAdapter adapter) {
            return adapter.getElement();
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(TestBenchDriverProxy::fromSelenium)
                    .toList();
        }
        if (value instanceof Object[] array) {
            return fromSelenium(Arrays.asList(array));
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            map.forEach((key, item) -> copy.put(key, fromSelenium(item)));
            return copy;
        }
        return value;
    }

    private static Object[] toSelenium(Object[] args) {
        return Arrays.stream(args).map(TestBenchDriverProxy::toSelenium)
                .toArray();
    }

    // A TestBenchElement is no WebElement, so Selenium cannot send it as a
    // script argument; its WebElement view, at any depth, it can
    private static Object toSelenium(Object value) {
        if (value instanceof TestBenchElement element) {
            return element.asWebElement();
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(TestBenchDriverProxy::toSelenium)
                    .toList();
        }
        if (value instanceof Object[] array) {
            return toSelenium(Arrays.asList(array));
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            map.forEach((key, item) -> copy.put(key, toSelenium(item)));
            return copy;
        }
        return value;
    }

    @Override
    public Object executeScript(String script, Object... args) {
        if (!onSelenium()) {
            return commandExecutor.executeScript(script, fromSelenium(args));
        }
        if (!(getWrappedDriver() instanceof JavascriptExecutor)) {
            throw new RuntimeException(
                    "The driver is not a JavascriptExecutor");
        }
        waitForVaadin();
        return wrapElementOrElements(((JavascriptExecutor) getWrappedDriver())
                .executeScript(script, toSelenium(args)), getCommandExecutor());
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        if (!onSelenium()) {
            return commandExecutor.executeAsyncScript(script,
                    fromSelenium(args));
        }
        if (!(getWrappedDriver() instanceof JavascriptExecutor)) {
            throw new RuntimeException(
                    "The driver is not a JavascriptExecutor");
        }
        waitForVaadin();
        return wrapElementOrElements(((JavascriptExecutor) getWrappedDriver())
                .executeAsyncScript(script, toSelenium(args)),
                getCommandExecutor());
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        waitForVaadin();
        return ((TakesScreenshot) getWrappedDriver()).getScreenshotAs(target);
    }

    @Override
    public void perform(Collection<Sequence> actions) {
        if (!onSelenium()) {
            throw NotYetImplementedException
                    .notYetImplemented("Selenium's Actions off Selenium");
        }
        waitForVaadin();
        ((Interactive) getWrappedDriver()).perform(actions);
    }

    @Override
    public void resetInputState() {
        if (!onSelenium()) {
            throw NotYetImplementedException
                    .notYetImplemented("Selenium's Actions off Selenium");
        }
        waitForVaadin();
        ((Interactive) getWrappedDriver()).resetInputState();
    }

}
