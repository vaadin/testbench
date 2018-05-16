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
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;

import com.vaadin.testbench.By.ByVaadin;
import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

public class TestBenchDriverProxy extends TestBenchCommandExecutor implements
        WebDriver, WrapsDriver, HasTestBenchCommandExecutor, HasDriver {

    private final WebDriver actualDriver;

    /**
     * Constructs a TestBenchDriverProxy using the provided web driver for the
     * actual driving.
     *
     * @param webDriver
     */
    protected TestBenchDriverProxy(WebDriver webDriver) {
        super(webDriver, new ImageComparison(), new ReferenceNameGenerator());
        actualDriver = webDriver;
    }

    @Override
    public WebDriver getWrappedDriver() {
        return actualDriver;
    }

    public WebDriver getActualDriver() {
        return actualDriver;
    }

    // ----------------- WebDriver methods for convenience.

    @Override
    public void close() {
        actualDriver.close();
    }

    @Override
    public WebElement findElement(By arg0) {
        if (arg0 instanceof ByVaadin) {
            return TestBenchElement.wrapElement(arg0.findElement(this), this);
        }
        return TestBenchElement.wrapElement(actualDriver.findElement(arg0),
                this);
    }

    @Override
    public List<WebElement> findElements(By arg0) {

        List<WebElement> elements = new ArrayList<WebElement>();

        // We can Wrap It!
        if (arg0 instanceof ByVaadin) {
            elements.addAll(TestBenchElement.wrapElements(
                    arg0.findElements(this), this));
        } else {
            elements.addAll(TestBenchElement.wrapElements(
                    actualDriver.findElements(arg0), this));
        }

        return elements;
    }

    /**
     * Finds an element by a Vaadin selector string.
     *
     * @param selector
     *            TestBench4 style Vaadin selector.
     * @param context
     *            a suitable search context - either a
     *            {@link TestBenchDriverProxy} or a {@link TestBenchElement}
     *            instance.
     * @return the first element identified by the selector
     */
    protected static WebElement findElementByVaadinSelector(String selector,
            SearchContext context) {
        // This is needed for 7.1 and earlier Vaadin versions.
        List<WebElement> elements = executeSearch(selector, context,
                "getElementByPath");

        if (elements.isEmpty()) {
            final String errorString = "Vaadin could not find elements with the selector "
                    + selector;
            throw new NoSuchElementException(
                    errorString,
                    new Exception(
                            "Client could not identify elements with the provided selector"));
        }

        return elements.get(0);
    }

    /**
     * Finds a list of elements by a Vaadin selector string.
     *
     * @param selector
     *            TestBench4 style Vaadin selector.
     * @param context
     *            a suitable search context - either a
     *            {@link TestBenchDriverProxy} or a {@link TestBenchElement}
     *            instance.
     * @return the list of elements identified by the selector
     */
    protected static List<WebElement> findElementsByVaadinSelector(
            String selector, SearchContext context) {
        return executeSearch(selector, context, "getElementsByPath");
    }

    private static List<WebElement> executeSearch(String selector,
            SearchContext context, String jsFunction) {
        final String errorString = "Vaadin could not find elements with the selector "
                + selector;

        // Construct elementSelectionString script fragment based on type of
        // search context
        String elementSelectionString = "var element = clients[client]."
                + jsFunction;
        if (context instanceof WebDriver) {
            elementSelectionString += "(arguments[0]);";
        } else {
            elementSelectionString += "StartingAt(arguments[0], arguments[1]);";
        }

        String findByVaadinScript = "var clients = window.vaadin.clients;"
                + "var elements = [];"
                + "for (client in clients) {" + elementSelectionString
                + "  if (element) {" + " elements = elements.concat(element);" + "  }" + "}"
                + "return elements;";

        WebDriver driver = ((HasDriver) context).getDriver();

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        List<WebElement> elements = new ArrayList<WebElement>();

        if (selector.contains("::")) {
            // We've been given specifications to access a specific client on
            // the page; the client ApplicationConnection is managed by the
            // JavaScript running on the page, so we use the driver's
            // JavaScriptExecutor to query further...
            String client = selector.substring(0, selector.indexOf("::"));
            String path = selector.substring(selector.indexOf("::") + 2);
            try {
                Object output = jse
                        .executeScript("return window.vaadin.clients." + client
                                + "." + jsFunction + "(\"" + path + "\");");
                elements.addAll(extractWebElements(output));
            } catch (Exception e) {
                throw new NoSuchElementException(errorString, e);
            }
        } else {
            try {
                if (context instanceof WebDriver) {
                    Object output = jse.executeScript(findByVaadinScript,
                            selector);

                    elements.addAll(extractWebElements(output));
                } else {
                    Object output = jse.executeScript(findByVaadinScript,
                            selector, context);
                    elements.addAll(extractWebElements(output));
                }
            } catch (Exception e) {
                throw new NoSuchElementException(errorString, e);
            }
        }

        return elements;
    }

    private static List<WebElement> extractWebElements(Object elementList) {
        List<WebElement> result = new ArrayList<WebElement>();
        if (elementList instanceof WebElement) {
            result.add((WebElement) elementList);
        } else if (elementList instanceof List<?>) {
            for (Object o : (List<?>) elementList) {
                if (null != o && o instanceof WebElement) {
                    result.add((WebElement) o);
                }
            }
        }
        return result;
    }

    @Override
    public void get(String arg0) {
        actualDriver.get(arg0);
    }

    @Override
    public String getCurrentUrl() {
        return actualDriver.getCurrentUrl();
    }

    @Override
    public String getPageSource() {
        return actualDriver.getPageSource();
    }

    @Override
    public String getTitle() {
        return actualDriver.getTitle();
    }

    @Override
    public String getWindowHandle() {
        return actualDriver.getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
        return actualDriver.getWindowHandles();
    }

    @Override
    public Options manage() {
        return actualDriver.manage();
    }

    @Override
    public Navigation navigate() {
        return actualDriver.navigate();
    }

    @Override
    public void quit() {
        actualDriver.quit();
    }

    @Override
    public TargetLocator switchTo() {
        return actualDriver.switchTo();
    }

    @Override
    public SearchContext getContext() {
        return this;
    }

    @Override
    public TestBenchCommandExecutor getTestBenchCommandExecutor() {
        return this;
    }

    @Override
    public WebDriver getDriver() {
        return this;
    }

}
