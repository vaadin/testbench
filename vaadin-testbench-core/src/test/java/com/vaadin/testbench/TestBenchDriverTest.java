/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.isA;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestBenchDriverTest {

    @Test
    public void testTestBenchDriverIsAWebDriver() {
        WebDriver driver = TestBench
                .createDriver(Mockito.mock(WebDriver.class));
        assertTrue(driver instanceof WebDriver);
    }

    @Test
    public void testTestBenchDriverActsAsProxy() {
        FirefoxDriver mockDriver = Mockito.mock(FirefoxDriver.class);
        mockDriver.close();
        WebElement mockElement = Mockito.mock(WebElement.class);
        Mockito.when(mockDriver.findElement(isA(By.class)))
                .thenReturn(mockElement);
        List<WebElement> elements = Arrays.asList(mockElement);
        Mockito.when(mockDriver.findElements(isA(By.class)))
                .thenReturn(elements);
        mockDriver.get("foo");

        Mockito.when(mockDriver.getCurrentUrl()).thenReturn("foo");
        Mockito.when(mockDriver.getPageSource()).thenReturn("<html></html>");
        Mockito.when(mockDriver.getTitle()).thenReturn("bar");
        Mockito.when(mockDriver.getWindowHandle()).thenReturn("baz");
        Set<String> handles = new HashSet<>();
        Mockito.when(mockDriver.getWindowHandles()).thenReturn(handles);
        Options mockOptions = Mockito.mock(Options.class);
        Mockito.when(mockDriver.manage()).thenReturn(mockOptions);
        Navigation mockNavigation = Mockito.mock(Navigation.class);
        Mockito.when(mockDriver.navigate()).thenReturn(mockNavigation);
        mockDriver.quit();

        Mockito.when(((JavascriptExecutor) mockDriver)
                .executeScript(Mockito.anyString())).thenReturn(true);
        TargetLocator mockTargetLocator = Mockito.mock(TargetLocator.class);
        Mockito.when(mockDriver.switchTo()).thenReturn(mockTargetLocator);

        // TestBenchDriverProxy driver = new TestBenchDriverProxy(mockDriver);
        WebDriver driver = TestBench.createDriver(mockDriver);
        driver.close();
        By mockBy = Mockito.mock(By.class);
        assertTrue(driver.findElement(mockBy) instanceof TestBenchElement);
        assertTrue(
                driver.findElements(mockBy).get(0) instanceof TestBenchElement);
        driver.get("foo");
        assertEquals("foo", driver.getCurrentUrl());
        assertEquals("<html></html>", driver.getPageSource());
        assertEquals("bar", driver.getTitle());
        assertEquals("baz", driver.getWindowHandle());
        assertEquals(handles, driver.getWindowHandles());
        assertEquals(mockOptions, driver.manage());
        assertEquals(mockNavigation, driver.navigate());
        driver.quit();
        assertEquals(mockTargetLocator, driver.switchTo());
    }

    @Test
    public void getWrappedDriver_returnsParent() {
        WebDriver driverMock = Mockito.mock(WebDriver.class);
        WebDriver driver = TestBench.createDriver(driverMock);
        WebDriver wrappedDriver = ((WrapsDriver) driver).getWrappedDriver();
        assertEquals(driverMock, wrappedDriver);
    }

    @Test
    public void testDisableWaitForVaadin() {
        Capabilities mockCapabilities = Mockito.mock(Capabilities.class);
        Mockito.when(mockCapabilities.getBrowserName()).thenReturn("firefox");

        FirefoxDriver mockFF = Mockito.mock(FirefoxDriver.class);
        Mockito.when(mockFF.getCapabilities()).thenReturn(mockCapabilities);
        Mockito.when(
                mockFF.executeScript(contains("clients[client].isActive()")))
                .thenReturn(true);
        WebElement mockElement = Mockito.mock(WebElement.class);
        Mockito.when(mockFF.findElement(isA(By.class))).thenReturn(mockElement);

        TestBenchDriverProxy tb = (TestBenchDriverProxy) TestBench
                .createDriver(mockFF);
        tb.getCommandExecutor().disableWaitForVaadin();
        tb.findElement(By.id("foo"));

        tb.getCommandExecutor().enableWaitForVaadin();
        tb.findElement(By.id("foo"));

    }

}
