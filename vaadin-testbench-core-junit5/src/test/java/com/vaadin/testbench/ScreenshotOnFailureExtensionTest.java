/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for ScreenshotOnFailureExtension to ensure it handles closed drivers
 * gracefully without masking the original test failure.
 */
public class ScreenshotOnFailureExtensionTest {

    /**
     * Tests that when the driver is closed (sessionId is null), the extension
     * does not throw an exception that would mask the original test failure.
     * Before the fix, this would throw a RuntimeException masking the original
     * failure. After the fix, it gracefully handles the situation and logs a
     * warning.
     */
    @Test
    public void testScreenshotOnFailure_whenDriverClosed_doesNotThrowException() {
        // Create a mock driver that simulates a closed session
        RemoteWebDriver mockDriver = mock(RemoteWebDriver.class);
        when(mockDriver.getSessionId()).thenReturn(null);

        // Create a test holder
        TestDriverHolder driverHolder = new TestDriverHolder(mockDriver);

        // Create the extension
        ScreenshotOnFailureExtension extension = new ScreenshotOnFailureExtension(
                driverHolder, false);

        // Create a mock extension context
        ExtensionContext mockContext = mock(ExtensionContext.class);
        when(mockContext.getDisplayName()).thenReturn("testMethod");
        when(mockContext.getParent()).thenReturn(java.util.Optional.empty());

        // Create an original test failure
        AssertionError originalFailure = new AssertionError(
                "This is the original test failure");

        // Call testFailed - this should NOT throw an exception
        // Before the fix, this would throw RuntimeException("There was a
        // problem grabbing and writing a screen shot of a test failure.")
        // After the fix, it logs a warning and returns gracefully
        Assertions.assertDoesNotThrow(() -> {
            extension.testFailed(mockContext, originalFailure);
        }, "Extension should not throw exception when driver is closed");
    }

    /**
     * Tests that when the driver throws WebDriverException during screenshot
     * capture, the extension does not throw an exception that would mask the
     * original test failure. Before the fix, only IOException was caught, so
     * WebDriverException would propagate and mask the original failure. After
     * the fix, all exceptions are caught and logged as warnings.
     */
    @Test
    public void testScreenshotOnFailure_whenDriverThrowsWebDriverException_doesNotThrowException() {
        // Create a mock driver that throws WebDriverException
        RemoteWebDriver mockDriver = mock(RemoteWebDriver.class);
        when(mockDriver.getSessionId())
                .thenReturn(new SessionId("test-session"));
        when(((TakesScreenshot) mockDriver).getScreenshotAs(any()))
                .thenThrow(new WebDriverException("Session not found"));

        // Create a test holder
        TestDriverHolder driverHolder = new TestDriverHolder(mockDriver);

        // Create the extension
        ScreenshotOnFailureExtension extension = new ScreenshotOnFailureExtension(
                driverHolder, false);

        // Create a mock extension context
        ExtensionContext mockContext = mock(ExtensionContext.class);
        when(mockContext.getDisplayName()).thenReturn("testMethod");
        when(mockContext.getParent()).thenReturn(java.util.Optional.empty());

        // Create an original test failure
        AssertionError originalFailure = new AssertionError(
                "This is the original test failure");

        // Call testFailed - this should NOT throw an exception
        // Before the fix, WebDriverException would propagate (only IOException
        // was caught)
        // After the fix, all exceptions are caught and logged as warnings
        Assertions.assertDoesNotThrow(() -> {
            extension.testFailed(mockContext, originalFailure);
        }, "Extension should not throw exception when screenshot capture fails");
    }

    /**
     * Simple test driver holder for testing purposes.
     */
    private static class TestDriverHolder implements HasDriver {
        private final WebDriver driver;

        TestDriverHolder(WebDriver driver) {
            this.driver = driver;
        }

        @Override
        public WebDriver getDriver() {
            return driver;
        }
    }
}
