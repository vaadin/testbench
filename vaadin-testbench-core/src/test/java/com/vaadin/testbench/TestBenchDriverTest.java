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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WrapsDriver;

public class TestBenchDriverTest {

    @Test
    public void testTestBenchDriverIsAWebDriver() {
        WebDriver driver = TestBench
                .createDriver(createNiceMock(WebDriver.class));
        assertTrue(driver instanceof WebDriver);
    }

    @Test
    public void testTestBenchDriverActsAsProxy() {
        FirefoxDriver mockDriver = createMock(FirefoxDriver.class);
        mockDriver.close();
        expectLastCall().once();
        WebElement mockElement = createNiceMock(WebElement.class);
        expect(mockDriver.findElement(isA(By.class))).andReturn(mockElement);
        List<WebElement> elements = Arrays.asList(mockElement);
        expect(mockDriver.findElements(isA(By.class))).andReturn(elements);
        mockDriver.get("foo");
        expectLastCall().once();
        expect(mockDriver.getCurrentUrl()).andReturn("foo");
        expect(mockDriver.getPageSource()).andReturn("<html></html>");
        expect(mockDriver.getTitle()).andReturn("bar");
        expect(mockDriver.getWindowHandle()).andReturn("baz");
        Set<String> handles = new HashSet<>();
        expect(mockDriver.getWindowHandles()).andReturn(handles);
        Options mockOptions = createNiceMock(Options.class);
        expect(mockDriver.manage()).andReturn(mockOptions);
        Navigation mockNavigation = createNiceMock(Navigation.class);
        expect(mockDriver.navigate()).andReturn(mockNavigation);
        mockDriver.quit();
        expectLastCall().once();
        expect(((JavascriptExecutor) mockDriver)
                .executeScript(anyObject(String.class))).andStubReturn(true);
        TargetLocator mockTargetLocator = createNiceMock(TargetLocator.class);
        expect(mockDriver.switchTo()).andReturn(mockTargetLocator);
        replay(mockDriver);

        // TestBenchDriverProxy driver = new TestBenchDriverProxy(mockDriver);
        WebDriver driver = TestBench.createDriver(mockDriver);
        driver.close();
        By mockBy = createNiceMock(By.class);
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

        verify(mockDriver);
    }

    @Test
    public void getWrappedDriver_returnsParent() {
        WebDriver driverMock = createNiceMock(WebDriver.class);
        WebDriver driver = TestBench.createDriver(driverMock);
        WebDriver wrappedDriver = ((WrapsDriver) driver).getWrappedDriver();
        assertEquals(driverMock, wrappedDriver);
    }

    @Test
    public void testDisableWaitForVaadin() {
        Capabilities mockCapabilities = createNiceMock(Capabilities.class);
        expect(mockCapabilities.getBrowserName()).andReturn("firefox")
                .anyTimes();

        FirefoxDriver mockFF = createMock(FirefoxDriver.class);
        expect(mockFF.getCapabilities()).andReturn(mockCapabilities).anyTimes();
        expect(mockFF.executeScript(contains("clients[client].isActive()")))
                .andReturn(true).once();
        WebElement mockElement = createNiceMock(WebElement.class);
        expect(mockFF.findElement(isA(By.class))).andReturn(mockElement)
                .times(2);
        replay(mockFF, mockElement, mockCapabilities);

        TestBenchDriverProxy tb = (TestBenchDriverProxy) TestBench
                .createDriver(mockFF);
        tb.getCommandExecutor().disableWaitForVaadin();
        tb.findElement(By.id("foo"));

        tb.getCommandExecutor().enableWaitForVaadin();
        tb.findElement(By.id("foo"));

        verify(mockFF, mockElement);
    }

}
