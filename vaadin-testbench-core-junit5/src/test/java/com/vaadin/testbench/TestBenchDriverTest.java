/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.vaadin.testbench.backend.selenium.SeleniumElementBackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;

/** The test-level driver, on a Selenium driver. */
public class TestBenchDriverTest {

    private final FirefoxDriver selenium = Mockito.mock(FirefoxDriver.class);
    private final BrowserDriver driver = TestBench.createDriver(selenium);

    {
        Mockito.when(selenium.executeScript(contains("isActive()")))
                .thenReturn(true);
    }

    @Test
    public void pageCallsGoToTheSeleniumDriver() {
        Mockito.when(selenium.getCurrentUrl()).thenReturn("foo");
        Mockito.when(selenium.getPageSource()).thenReturn("<html></html>");
        Mockito.when(selenium.getTitle()).thenReturn("bar");
        Mockito.when(selenium.getWindowHandle()).thenReturn("baz");
        Mockito.when(selenium.getWindowHandles()).thenReturn(Set.of("baz"));
        Navigation navigation = Mockito.mock(Navigation.class);
        Mockito.when(selenium.navigate()).thenReturn(navigation);
        Options options = Mockito.mock(Options.class);
        Window window = Mockito.mock(Window.class);
        Mockito.when(selenium.manage()).thenReturn(options);
        Mockito.when(options.window()).thenReturn(window);
        TargetLocator targetLocator = Mockito.mock(TargetLocator.class);
        Mockito.when(selenium.switchTo()).thenReturn(targetLocator);

        driver.get("foo");
        Mockito.verify(selenium).get("foo");
        assertEquals("foo", driver.getCurrentUrl());
        assertEquals("<html></html>", driver.getPageSource());
        assertEquals("bar", driver.getTitle());
        assertEquals("baz", driver.getWindowHandle());
        assertEquals(Set.of("baz"), driver.getWindowHandles());
        driver.navigate().refresh();
        Mockito.verify(navigation).refresh();
        driver.manage().window().setSize(new Dimension(800, 600));
        Mockito.verify(window)
                .setSize(new org.openqa.selenium.Dimension(800, 600));
        assertSame(driver, driver.switchTo().window("baz"));
        Mockito.verify(targetLocator).window("baz");
        driver.close();
        Mockito.verify(selenium).close();
        driver.quit();
        Mockito.verify(selenium).quit();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void findElementGivesATestBenchElement() {
        WebElement found = Mockito.mock(WebElement.class);
        Mockito.when(selenium.executeScript(contains("querySelectorAll"),
                any(Object[].class))).thenReturn(List.of(found));

        TestBenchElement element = driver.findElement(By.id("foo"));
        assertEquals(found,
                ((SeleniumElementBackend) element.getElementBackend())
                        .getWebElement());
        assertEquals(1, driver.findElements(By.id("foo")).size());
    }

    @Test
    public void noWaitForVaadinWhenDisabled() {
        driver.getCommandExecutor().disableWaitForVaadin();
        driver.getTitle();
        Mockito.verify(selenium, Mockito.never())
                .executeScript(contains("isActive()"));

        driver.getCommandExecutor().enableWaitForVaadin();
        driver.getTitle();
        Mockito.verify(selenium).executeScript(contains("isActive()"));
    }
}
