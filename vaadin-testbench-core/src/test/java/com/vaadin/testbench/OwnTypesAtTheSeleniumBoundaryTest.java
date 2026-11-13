/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.spi.ElementBackend;
import com.vaadin.testbench.testutils.SeleniumElements;

/**
 * An element speaks TestBench's own types; the Selenium backend turns them into
 * Selenium's.
 */
public class OwnTypesAtTheSeleniumBoundaryTest {

    private final ElementBackend node = Mockito.mock(ElementBackend.class);
    private final TestBenchElement element = TestBench
            .createElement(TestBenchElement.class, node, null);

    @Test
    public void findElementSearchesTheLightDomByCss() {
        ElementBackend child = Mockito.mock(ElementBackend.class);
        Mockito.when(node.findElements("#save")).thenReturn(List.of(child));

        Assert.assertSame(child,
                element.findElement(By.id("save")).getElementBackend());
        Assert.assertEquals(1, element.findElements(By.id("save")).size());
    }

    @Test(expected = NoSuchElementException.class)
    public void findElementThrowsTestBenchsNotFound() {
        element.findElement(By.id("missing"));
    }

    public interface JavascriptDriver extends WebDriver, JavascriptExecutor {
    }

    @Test
    public void driverScriptsGetElementsAsSeleniumElements() {
        List<Object[]> scriptArgs = new ArrayList<>();
        JavascriptDriver driver = Mockito.mock(JavascriptDriver.class,
                invocation -> {
                    if (!invocation.getMethod().getName()
                            .equals("executeScript")) {
                        return null;
                    }
                    String script = invocation.getArgument(0);
                    if (script.contains("isActive()")) {
                        return true;
                    }
                    Object[] args = invocation.getRawArguments();
                    scriptArgs.add((Object[]) args[1]);
                    return null;
                });
        BrowserDriver browser = TestBench.createDriver(driver);
        WebElement raw = Mockito.mock(WebElement.class);
        TestBenchElement element = SeleniumElements.wrap(raw,
                browser.getCommandExecutor());

        browser.executeScript("script", element,
                new TestBenchElement[] { element }, List.of(element));

        Object[] args = scriptArgs.get(0);
        Assert.assertSame(raw, args[0]);
        Assert.assertSame(raw, ((List<?>) args[1]).get(0));
        Assert.assertSame(raw, ((List<?>) args[2]).get(0));
    }

    @Test
    public void geometryIsTestBenchs() {
        Mockito.when(node.getRect())
                .thenReturn(new ElementBackend.Rect(1, 2, 30, 40));

        Assert.assertEquals(new Rectangle(1, 2, 40, 30), element.getRect());
        Assert.assertEquals(new Dimension(30, 40), element.getSize());
        Assert.assertEquals(new Point(1, 2), element.getLocation());
    }
}
