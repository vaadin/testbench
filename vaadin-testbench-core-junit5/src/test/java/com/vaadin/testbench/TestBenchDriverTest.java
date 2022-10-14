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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.isA;

public class TestBenchDriverTest {

    @Test
    public void testTestBenchDriverIsAWebDriver() {
        WebDriver driver = TestBench
                .createDriver(Mockito.mock(WebDriver.class));
        Assertions.assertTrue(driver instanceof WebDriver);
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
        Assertions.assertTrue(
                driver.findElement(mockBy) instanceof TestBenchElement);
        Assertions.assertTrue(
                driver.findElements(mockBy).get(0) instanceof TestBenchElement);
        driver.get("foo");
        Assertions.assertEquals("foo", driver.getCurrentUrl());
        Assertions.assertEquals("<html></html>", driver.getPageSource());
        Assertions.assertEquals("bar", driver.getTitle());
        Assertions.assertEquals("baz", driver.getWindowHandle());
        Assertions.assertEquals(handles, driver.getWindowHandles());
        Assertions.assertEquals(mockOptions, driver.manage());
        Assertions.assertEquals(mockNavigation, driver.navigate());
        driver.quit();
        Assertions.assertEquals(mockTargetLocator, driver.switchTo());
    }

    @Test
    public void getWrappedDriver_returnsParent() {
        WebDriver driverMock = Mockito.mock(WebDriver.class);
        WebDriver driver = TestBench.createDriver(driverMock);
        WebDriver wrappedDriver = ((WrapsDriver) driver).getWrappedDriver();
        Assertions.assertEquals(driverMock, wrappedDriver);
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
