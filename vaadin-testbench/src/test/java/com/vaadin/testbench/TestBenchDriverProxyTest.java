package com.vaadin.testbench;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import org.easymock.EasyMock;
import org.junit.Test;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestBenchDriverProxyTest {

    @Test
    public void testFindElementsByVaadin_shouldNotCallActualDriver() throws Exception {
        WebDriver mockDriver = EasyMock.createMock(WebDriver.class);
        TestBenchDriverProxy tbdp = new TestBenchDriverProxy(mockDriver);
        EasyMock.replay(mockDriver);
        tbdp.findElements(By.vaadin("foo"));
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
        WebDriver mockDriver = EasyMock.createMock(WebDriver.class);
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
