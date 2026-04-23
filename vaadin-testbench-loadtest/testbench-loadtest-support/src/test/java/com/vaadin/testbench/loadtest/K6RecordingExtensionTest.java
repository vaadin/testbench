/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.HasDriver;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class K6RecordingExtensionTest {

    private String originalProxyHost;

    @BeforeEach
    void captureSystemProperties() {
        originalProxyHost = System.getProperty("k6.proxy.host");
        System.clearProperty("k6.proxy.host");
    }

    @AfterEach
    void restoreSystemProperties() {
        if (originalProxyHost == null) {
            System.clearProperty("k6.proxy.host");
        } else {
            System.setProperty("k6.proxy.host", originalProxyHost);
        }
    }

    /** Test fixture: plain test method (not destructive). */
    void plainMethod() {
    }

    /** Test fixture: destructive test method. */
    @Destructive
    void destructiveMethod() {
    }

    private Method method(String name) throws NoSuchMethodException {
        return K6RecordingExtensionTest.class.getDeclaredMethod(name);
    }

    @Test
    void evaluateExecutionCondition_recordingInactive_enabled()
            throws Exception {
        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Mockito.when(context.getTestMethod())
                .thenReturn(Optional.of(method("destructiveMethod")));

        K6RecordingExtension extension = new K6RecordingExtension();
        ConditionEvaluationResult result = extension
                .evaluateExecutionCondition(context);

        assertFalse(result.isDisabled());
    }

    @Test
    void evaluateExecutionCondition_recordingActive_destructiveDisabled()
            throws Exception {
        System.setProperty("k6.proxy.host", "localhost:6000");

        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Mockito.when(context.getTestMethod())
                .thenReturn(Optional.of(method("destructiveMethod")));

        K6RecordingExtension extension = new K6RecordingExtension();
        ConditionEvaluationResult result = extension
                .evaluateExecutionCondition(context);

        assertTrue(result.isDisabled());
    }

    @Test
    void evaluateExecutionCondition_recordingActive_nonDestructiveEnabled()
            throws Exception {
        System.setProperty("k6.proxy.host", "localhost:6000");

        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Mockito.when(context.getTestMethod())
                .thenReturn(Optional.of(method("plainMethod")));

        K6RecordingExtension extension = new K6RecordingExtension();
        ConditionEvaluationResult result = extension
                .evaluateExecutionCondition(context);

        assertFalse(result.isDisabled());
    }

    @Test
    void evaluateExecutionCondition_recordingActive_missingTestMethodEnabled() {
        System.setProperty("k6.proxy.host", "localhost:6000");

        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Mockito.when(context.getTestMethod()).thenReturn(Optional.empty());

        K6RecordingExtension extension = new K6RecordingExtension();
        ConditionEvaluationResult result = extension
                .evaluateExecutionCondition(context);

        assertFalse(result.isDisabled());
    }

    @Test
    void afterEach_instanceNotHasDriver_noop() throws Exception {
        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Mockito.when(context.getRequiredTestInstance())
                .thenReturn("notADriver");

        K6RecordingExtension extension = new K6RecordingExtension();
        assertDoesNotThrow(() -> extension.afterEach(context));
    }

    @Test
    void afterEach_hasDriverWithNullDriver_noop() throws Exception {
        HasDriverFake fake = new HasDriverFake(null);
        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Mockito.when(context.getRequiredTestInstance()).thenReturn(fake);

        K6RecordingExtension extension = new K6RecordingExtension();
        assertDoesNotThrow(() -> extension.afterEach(context));
    }

    @Test
    void afterEach_hasDriver_quitsDriver() throws Exception {
        WebDriver driver = Mockito.mock(WebDriver.class);
        HasDriverFake fake = new HasDriverFake(driver);
        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Mockito.when(context.getRequiredTestInstance()).thenReturn(fake);

        K6RecordingExtension extension = new K6RecordingExtension();
        extension.afterEach(context);

        Mockito.verify(driver).quit();
    }

    @Test
    void afterEach_quitThrows_swallowed() throws Exception {
        WebDriver driver = Mockito.mock(WebDriver.class);
        Mockito.doThrow(new RuntimeException("already closed")).when(driver)
                .quit();
        HasDriverFake fake = new HasDriverFake(driver);
        ExtensionContext context = Mockito.mock(ExtensionContext.class);
        Mockito.when(context.getRequiredTestInstance()).thenReturn(fake);

        K6RecordingExtension extension = new K6RecordingExtension();
        assertDoesNotThrow(() -> extension.afterEach(context));
        Mockito.verify(driver).quit();
    }

    /** Test double that exposes a WebDriver. */
    private static class HasDriverFake implements HasDriver {
        private final WebDriver driver;

        HasDriverFake(WebDriver driver) {
            this.driver = driver;
        }

        @Override
        public WebDriver getDriver() {
            return driver;
        }
    }
}
