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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;

public class TestBenchDriverProxy
        implements WebDriver, WrapsDriver, HasTestBenchCommandExecutor,
        HasCapabilities, TakesScreenshot, JavascriptExecutor {

    private final WebDriver wrappedDriver;
    private final TestBenchCommandExecutor commandExecutor;

    protected TestBenchDriverProxy(WebDriver webDriver,
            TestBenchCommandExecutor commandExecutor) {
        wrappedDriver = webDriver;
        this.commandExecutor = commandExecutor;
    }

    // ----------------- WebDriver methods for convenience.

    @Override
    public void close() {
        getWrappedDriver().close();
    }

    @Override
    public WebElement findElement(By arg0) {
        return TestBenchElement.wrapElement(wrappedDriver.findElement(arg0),
                getCommandExecutor());
    }

    @Override
    public List<WebElement> findElements(By arg0) {
        return (List) TestBenchElement.wrapElements(
                wrappedDriver.findElements(arg0), getCommandExecutor());
    }

    @Override
    public void get(String arg0) {
        wrappedDriver.get(arg0);
    }

    @Override
    public String getCurrentUrl() {
        return wrappedDriver.getCurrentUrl();
    }

    @Override
    public String getPageSource() {
        return wrappedDriver.getPageSource();
    }

    @Override
    public String getTitle() {
        return wrappedDriver.getTitle();
    }

    @Override
    public String getWindowHandle() {
        return wrappedDriver.getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
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
        } else if (elementElementsOrValues instanceof WebElement) {
            if (elementElementsOrValues instanceof TestBenchElement) {
                return elementElementsOrValues;
            } else {
                return TestBench.createElement(
                        (WebElement) elementElementsOrValues,
                        tbCommandExecutor);
            }
        } else {
            return elementElementsOrValues;
        }
    }

    @Override
    public Object executeScript(String script, Object... args) {
        if (!(getWrappedDriver() instanceof JavascriptExecutor)) {
            throw new RuntimeException(
                    "The driver is not a JavascriptExecutor");
        }

        return wrapElementOrElements(((JavascriptExecutor) getWrappedDriver())
                .executeScript(script, args), getCommandExecutor());
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        if (!(getWrappedDriver() instanceof JavascriptExecutor)) {
            throw new RuntimeException(
                    "The driver is not a JavascriptExecutor");
        }

        return wrapElementOrElements(((JavascriptExecutor) getWrappedDriver())
                .executeAsyncScript(script, args), getCommandExecutor());
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        return ((TakesScreenshot) getWrappedDriver()).getScreenshotAs(target);
    }

}
