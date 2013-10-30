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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.internal.WrapsDriver#getWrappedDriver()
     */
    @Override
    public WebDriver getWrappedDriver() {
        return actualDriver;
    }

    // ----------------- WebDriver methods for convenience.

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#close()
     */
    @Override
    public void close() {
        actualDriver.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#findElement(org.openqa.selenium.By)
     */
    @Override
    public WebElement findElement(By arg0) {
        if (arg0 instanceof ByVaadin) {
            return arg0.findElement(this);
        }
        return TestBench.createElement(actualDriver.findElement(arg0), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#findElements(org.openqa.selenium.By)
     */
    @Override
    public List<WebElement> findElements(By arg0) {

        List<WebElement> elements;

        if (arg0 instanceof ByVaadin) {
            elements = arg0.findElements(this);
        } else {
            elements = actualDriver.findElements(arg0);
        }

        List<WebElement> testBenchElements = new ArrayList<WebElement>(
                elements.size());

        for (WebElement e : elements) {
            WebElement el = TestBench.createElement(e, this);
            testBenchElements.add(el);
        }
        return testBenchElements;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#get(java.lang.String)
     */
    @Override
    public void get(String arg0) {
        actualDriver.get(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#getCurrentUrl()
     */
    @Override
    public String getCurrentUrl() {
        return actualDriver.getCurrentUrl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#getPageSource()
     */
    @Override
    public String getPageSource() {
        return actualDriver.getPageSource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#getTitle()
     */
    @Override
    public String getTitle() {
        return actualDriver.getTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#getWindowHandle()
     */
    @Override
    public String getWindowHandle() {
        return actualDriver.getWindowHandle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#getWindowHandles()
     */
    @Override
    public Set<String> getWindowHandles() {
        return actualDriver.getWindowHandles();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#manage()
     */
    @Override
    public Options manage() {
        return actualDriver.manage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#navigate()
     */
    @Override
    public Navigation navigate() {
        return actualDriver.navigate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#quit()
     */
    @Override
    public void quit() {
        actualDriver.quit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#switchTo()
     */
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
