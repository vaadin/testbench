package com.vaadin.testbench;

import com.vaadin.testbench.commands.TestBenchCommands;
import org.easymock.EasyMock;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ByVaadinTest {
    interface TestBenchCommandsAndSearchContext extends TestBenchCommands, SearchContext {
    }

    @Test
    public void testFindByElements_findByElementReturnsNull_shouldReturnEmptyList() throws Exception {
        TestBenchCommandsAndSearchContext context = EasyMock.createNiceMock(TestBenchCommandsAndSearchContext.class);
        EasyMock.expect(context.findElementByVaadinSelector("foo")).andReturn(null);
        EasyMock.replay(context);

        List<WebElement> elements = By.vaadin("foo").findElements(context);
        assertNotNull(elements);
        assertTrue(elements.isEmpty());

        EasyMock.verify(context);
    }

    @Test
    public void testFindByElements_findByElementThrowsNoSuchElementException_shouldReturnEmptyList() throws Exception {
        TestBenchCommandsAndSearchContext context = EasyMock.createNiceMock(TestBenchCommandsAndSearchContext.class);
        EasyMock.expect(context.findElementByVaadinSelector("foo")).andThrow(new NoSuchElementException("foo"));
        EasyMock.replay(context);

        List<WebElement> elements = By.vaadin("foo").findElements(context);
        assertNotNull(elements);
        assertTrue(elements.isEmpty());

        EasyMock.verify(context);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFindByElement_searchContextNotTBC_shouldThrow() throws Exception {
        SearchContext context = EasyMock.createNiceMock(SearchContext.class);
        By.vaadin("foo").findElement(context);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testFindByElement_calledThroughElement_shouldThrow() throws Exception {
        RemoteWebElement rwe = new RemoteWebElement();
        rwe.findElement(By.vaadin("foo"));
    }
}
