/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.testbench;

import com.vaadin.testbench.commands.TestBenchCommands;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * A superclass with some helpers to aid TestBench developers. This superclass
 * is also used by tests created by the Recorder.
 */
public abstract class TestBenchTestCase implements HasDriver {

    protected WebDriver driver;

    /**
     * Convenience method that casts the specified {@link WebDriver} instance to
     * an instance of {@link TestBenchCommands}, making it easy to access the
     * special TestBench commands.
     *
     * @param webDriver The WebDriver instance to cast.
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
     * @param webElement The WebElement to cast.
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
     * @param baseUrl the base URL
     * @param uri     the URI
     * @return the URL resulting from the combination of base URL and URI
     */
    protected String concatUrl(String baseUrl, String uri) {
        if (baseUrl.endsWith("/") && uri.startsWith("/")) {
            return baseUrl + uri.substring(1);
        }
        return baseUrl + uri;
    }

    /**
     * Returns true if an element can be found from the driver with given
     * selector.
     *
     * @param by the selector used to find element
     * @return true if the element can be found
     */
    public boolean isElementPresent(By by) {
        return !getDriver().findElements(by).isEmpty();
    }

    /**
     * @return the active WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Sets the active {@link WebDriver} that is used by this this case
     *
     * @param driver The WebDriver instance to set.
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }


}
