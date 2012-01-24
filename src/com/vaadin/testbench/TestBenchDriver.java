package com.vaadin.testbench;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestBenchDriver implements WebDriver {

    private WebDriver actualDriver;

    /**
     * Constructs a TestBenchDriver using the provided web driver for the actual
     * driving.
     * 
     * @param webDriver
     */
    public TestBenchDriver(WebDriver webDriver) {
        actualDriver = webDriver;
    }

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
    public WebElement findElement(By by) {
        return actualDriver.findElement(by);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#findElements(org.openqa.selenium.By)
     */
    @Override
    public List<WebElement> findElements(By by) {
        return actualDriver.findElements(by);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openqa.selenium.WebDriver#get(java.lang.String)
     */
    @Override
    public void get(String url) {
        actualDriver.get(url);
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
