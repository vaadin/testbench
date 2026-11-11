/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.backend.selenium;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.StaleElementReferenceException;
import com.vaadin.testbench.spi.ElementBackend;

public class SeleniumBackendTest {

    public interface FullDriver
            extends WebDriver, JavascriptExecutor, HasCapabilities {
    }

    private final FullDriver driver = Mockito.mock(FullDriver.class);
    private final SeleniumBackend backend = new SeleniumBackend(driver);

    @Test
    public void executeAsyncScriptConvertsArgumentsAndResult() {
        WebElement argument = Mockito.mock(WebElement.class);
        WebElement result = Mockito.mock(WebElement.class);
        Mockito.when(driver.executeAsyncScript("script", argument, "text"))
                .thenReturn(List.of(result));

        Object returned = backend.executeAsyncScript("script",
                new SeleniumElementBackend(backend, argument), "text");

        Assert.assertEquals(
                List.of(new SeleniumElementBackend(backend, result)), returned);
    }

    @Test
    public void executeAsyncScriptThrowsTestBenchsStaleElement() {
        Mockito.when(driver.executeAsyncScript("script")).thenThrow(
                new org.openqa.selenium.StaleElementReferenceException("gone"));
        try {
            backend.executeAsyncScript("script");
            Assert.fail("Expected a stale element");
        } catch (StaleElementReferenceException e) {
            Assert.assertEquals("gone", e.getMessage());
        }
    }

    @Test
    public void executeAsyncScriptLeavesOtherExceptionsAlone() {
        WebDriverException other = new WebDriverException("other");
        Mockito.when(driver.executeAsyncScript("script")).thenThrow(other);
        try {
            backend.executeAsyncScript("script");
            Assert.fail("Expected the driver's exception");
        } catch (WebDriverException e) {
            Assert.assertSame(other, e);
        }
    }

    @Test
    public void browserNameComesFromTheCapabilities() {
        Mockito.when(driver.getCapabilities()).thenReturn(
                new ImmutableCapabilities("browserName", "firefox"));
        Assert.assertEquals("firefox", backend.getBrowserName());
        Assert.assertNull(new SeleniumBackend(Mockito.mock(WebDriver.class))
                .getBrowserName());
    }

    @Test
    public void findElementsSearchesByCss() {
        WebElement parent = Mockito.mock(WebElement.class);
        WebElement child = Mockito.mock(WebElement.class);
        Mockito.when(parent.findElements(By.cssSelector("#save")))
                .thenReturn(List.of(child));

        List<ElementBackend> found = new SeleniumElementBackend(backend, parent)
                .findElements("#save");

        Assert.assertEquals(List.of(new SeleniumElementBackend(backend, child)),
                found);
    }

    @Test(expected = StaleElementReferenceException.class)
    public void findElementsInAGoneElementThrowsTestBenchsStaleElement() {
        WebElement parent = Mockito.mock(WebElement.class);
        Mockito.when(parent.findElements(Mockito.any())).thenThrow(
                new org.openqa.selenium.StaleElementReferenceException("gone"));
        new SeleniumElementBackend(backend, parent).findElements("div");
    }
}
