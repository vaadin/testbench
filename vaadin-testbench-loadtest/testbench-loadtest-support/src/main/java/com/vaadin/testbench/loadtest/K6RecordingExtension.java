/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import com.vaadin.testbench.HasDriver;

/**
 * JUnit 5 extension for k6 recording support.
 * <p>
 * This extension provides two features:
 * <ul>
 * <li>Skips test methods annotated with {@link Destructive} when the k6
 * recording proxy is active (detected via the {@code k6.proxy.host} system
 * property).</li>
 * <li>Ensures WebDriver cleanup after each test to prevent orphaned Chrome
 * processes.</li>
 * </ul>
 * <p>
 * This extension uses JUnit 5's ServiceLoader auto-detection mechanism. Enable
 * it by setting:
 * 
 * <pre>
 * -Djunit.jupiter.extensions.autodetection.enabled=true
 * </pre>
 */
public class K6RecordingExtension
        implements ExecutionCondition, AfterEachCallback {

    private static final ConditionEvaluationResult ENABLED = ConditionEvaluationResult
            .enabled("Not in k6 recording mode or test is not @Destructive");

    private static final ConditionEvaluationResult DISABLED = ConditionEvaluationResult
            .disabled("@Destructive test skipped during k6 recording");

    /**
     * Public no-arg constructor required for ServiceLoader.
     */
    public K6RecordingExtension() {
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(
            ExtensionContext context) {
        // Only skip if we're in k6 recording mode
        if (!isK6RecordingActive()) {
            return ENABLED;
        }

        // Check if the test method is annotated with @Destructive
        return context.getTestMethod()
                .filter(method -> method.isAnnotationPresent(Destructive.class))
                .map(method -> DISABLED).orElse(ENABLED);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Object testInstance = context.getRequiredTestInstance();
        if (!(testInstance instanceof HasDriver)) {
            return;
        }

        HasDriver testBase = (HasDriver) testInstance;
        WebDriver driver = testBase.getDriver();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                // Log but don't fail - driver may already be closed
                System.err.println(
                        "Warning: Failed to quit WebDriver: " + e.getMessage());
            }
        }
    }

    /**
     * Returns true if k6 recording proxy is active.
     */
    private static boolean isK6RecordingActive() {
        String proxyHost = System.getProperty("k6.proxy.host");
        return proxyHost != null && !proxyHost.isEmpty();
    }
}
