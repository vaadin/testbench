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

import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elementsbase.AbstractElement;

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

    public WebElement findElement(org.openqa.selenium.By by) {
        return getContext().findElement(by);
    }

    public List<WebElement> findElements(org.openqa.selenium.By by) {
        return getContext().findElements(by);
    }

    /**
     * Decorates the element with the specified Element type, making it possible
     * to use Vaadin component-specific API on elements found using standard
     * selenium API.
     * <p>
     * Example: <code>
     *     TableElement table = e.wrap(TableElement.class, driver.findElement(By.id("my-table")));
     *     assertEquals("Foo", table.getHeaderCell(1).getText());
     * </code>
     * 
     * @param elementType
     *            The type (class) containing the API to decorate with. Must
     *            extend {@link com.vaadin.testbench.elementsbase.AbstractElement}.
     * @param element
     *            The element instance to decorate
     * @return The element wrapped in an instance of the specified element type.
     */
    public <T extends AbstractElement> T wrap(Class<T> elementType,
            WebElement element) {
        return ((TestBenchElement) element).wrap(elementType);
    }
}
