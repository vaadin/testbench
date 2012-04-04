package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

public class TestBenchDriver extends TestBenchCommandExecutor implements
        WebDriver, WrapsDriver {
    private static Logger getLogger() {
        return Logger.getLogger(TestBenchDriver.class.getName());
    }

    private final WebDriver actualDriver;

    /**
     * Constructs a TestBenchDriver using the provided web driver for the actual
     * driving.
     * 
     * @param webDriver
     */
    protected TestBenchDriver(WebDriver webDriver) {
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
        return TestBench.createElement(actualDriver.findElement(arg0), this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#findElements(org.openqa.selenium.By)
     */
    @Override
    public List<WebElement> findElements(By arg0) {
        List<WebElement> elements = actualDriver.findElements(arg0);
        List<WebElement> tbElements = new ArrayList<WebElement>(elements.size());
        for (WebElement e : elements) {
            tbElements.add(TestBench.createElement(e, this));
        }
        return tbElements;
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

}
