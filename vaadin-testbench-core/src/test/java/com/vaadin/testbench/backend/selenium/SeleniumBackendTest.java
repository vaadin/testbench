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
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
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

    @Test(expected = com.vaadin.testbench.TimeoutException.class)
    public void aPageLoadTimeoutIsTestBenchsTimeout() {
        Mockito.doThrow(new org.openqa.selenium.TimeoutException("slow"))
                .when(driver).get("http://slow");
        backend.navigate("http://slow");
    }

    @Test(expected = StaleElementReferenceException.class)
    public void executeScriptThrowsTestBenchsStaleElement() {
        Mockito.when(driver.executeScript("script")).thenThrow(
                new org.openqa.selenium.StaleElementReferenceException("gone"));
        backend.executeScript("script");
    }

    @Test(expected = StaleElementReferenceException.class)
    public void everyElementMethodThrowsTestBenchsStaleElement() {
        WebElement gone = Mockito.mock(WebElement.class);
        Mockito.when(gone.getText()).thenThrow(
                new org.openqa.selenium.StaleElementReferenceException("gone"));
        new SeleniumElementBackend(backend, gone).getText();
    }

    @Test
    public void anActionWaitsUntilItsElementCanTakeIt() {
        WebElement button = Mockito.mock(WebElement.class);
        Mockito.doThrow(new ElementNotInteractableException("not yet"))
                .doThrow(new ElementClickInterceptedException("covered"))
                .doNothing().when(button).click();

        new SeleniumElementBackend(backend, button).click();

        Mockito.verify(button, Mockito.times(3)).click();
    }

    @Test
    public void anActionFailsOnceTheActionTimeoutIsOver() {
        String property = "com.vaadin.testbench.Parameters.actionTimeout";
        System.setProperty(property, "300");
        try {
            WebElement hidden = Mockito.mock(WebElement.class);
            Mockito.doThrow(new ElementNotInteractableException("hidden"))
                    .when(hidden).sendKeys(Mockito.any());
            long start = System.nanoTime();
            Assert.assertThrows(ElementNotInteractableException.class,
                    () -> new SeleniumElementBackend(backend, hidden)
                            .sendKeys("x"));
            long waitedMs = (System.nanoTime() - start) / 1_000_000;
            Assert.assertTrue("waited " + waitedMs + " ms",
                    waitedMs >= 300 && waitedMs < 2000);
        } finally {
            System.clearProperty(property);
        }
    }

    @Test
    public void aReadDoesNotWait() {
        WebElement hidden = Mockito.mock(WebElement.class);
        Mockito.when(hidden.getText())
                .thenThrow(new ElementNotInteractableException("hidden"));
        long start = System.nanoTime();
        Assert.assertThrows(ElementNotInteractableException.class,
                () -> new SeleniumElementBackend(backend, hidden).getText());
        Assert.assertTrue((System.nanoTime() - start) / 1_000_000 < 200);
    }

    @Test(expected = StaleElementReferenceException.class)
    public void findElementsInAGoneElementThrowsTestBenchsStaleElement() {
        WebElement parent = Mockito.mock(WebElement.class);
        Mockito.when(parent.findElements(Mockito.any())).thenThrow(
                new org.openqa.selenium.StaleElementReferenceException("gone"));
        new SeleniumElementBackend(backend, parent).findElements("div");
    }
}
