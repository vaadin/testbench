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
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;

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
        // This is in practice the same thing as actualDriver because of proxy.
        // Selenium can use us exactly the same way.
        return this;
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
        return TestBenchElement.wrapElement(actualDriver.findElement(arg0),
                this);
    }

    @Override
    public List<WebElement> findElements(By arg0) {
        return (List) TestBenchElement
                .wrapElements(actualDriver.findElements(arg0), this);
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
    public TestBenchCommandExecutor getCommandExecutor() {
        return this;
    }

    @Override
    public WebDriver getDriver() {
        return this;
    }

}
