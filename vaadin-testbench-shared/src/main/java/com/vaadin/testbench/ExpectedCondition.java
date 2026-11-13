/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/**
 * Selenium's {@code ExpectedCondition} on TestBench's types: what a
 * {@code waitUntil} waits for, holding once it returns neither {@code null} nor
 * {@code false}. A test's {@code waitUntil} takes it as the {@link Function} it
 * is, an element's as the {@link WaitCondition} it is too.
 * {@link ExpectedConditions} has Selenium's common ones.
 */
@FunctionalInterface
public interface ExpectedCondition<T>
        extends Function<BrowserDriver, @Nullable T>, WaitCondition<T> {

    @Override
    default @Nullable T apply(TestBenchCommandExecutor browser) {
        return apply(new TestBenchDriver(browser));
    }
}
