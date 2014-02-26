package com.vaadin.testbench;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestBenchDriverProxyTest {

    @Test
    public void testFindElementsByVaadin_shouldNotCallActualDriver()
            throws Exception {
        WebDriver mockDriver = EasyMock.createMock(WebDriver.class);
        TestBenchDriverProxy tbdp = new TestBenchDriverProxy(mockDriver);
        EasyMock.replay(mockDriver);
        try {
            tbdp.findElements(By.vaadin("foo"));
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("is not a JavascriptExecutor"));
        }
        EasyMock.verify(mockDriver);
    }

    @Test
    public void testFindElementsById_shouldCallActualDriver() throws Exception {
        WebDriver mockDriver = EasyMock.createMock(WebDriver.class);
        mockDriver.findElements(By.id("foo"));
        EasyMock.expectLastCall().andReturn(Collections.emptyList());
        TestBenchDriverProxy tbdp = new TestBenchDriverProxy(mockDriver);
        EasyMock.replay(mockDriver);
        tbdp.findElements(By.id("foo"));
        EasyMock.verify(mockDriver);
    }

    @Test
    public void testFindElementsByVaadin_shouldCallByVaadin() throws Exception {
        FirefoxDriver mockDriver = EasyMock.createMock(FirefoxDriver.class);
        expect(
                mockDriver.executeScript(contains("getElementByPath"),
                        contains("foo"))).andReturn(
                EasyMock.createMock(WebElement.class));
        TestBenchDriverProxy tbdp = new TestBenchDriverProxy(mockDriver);
        EasyMock.replay(mockDriver);
        // Beware, this is ugly...
        final boolean[] findElementsCalled = { false };
        By.ByVaadin by = new By.ByVaadin("foo") {
            @Override
            public List<WebElement> findElements(SearchContext context) {
                findElementsCalled[0] = true;
                return super.findElements(context);
            }
        };
        tbdp.findElements(by);
        EasyMock.verify(mockDriver);
        assertTrue(findElementsCalled[0]);
    }
}
