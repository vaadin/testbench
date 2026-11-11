/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;

/**
 * What an element waits for, written as the lambda Selenium's
 * {@code ExpectedCondition} took:
 *
 * <pre>{@code
 * waitUntil(driver -> !getPropertyBoolean("loading"));
 * }</pre>
 *
 * It holds once it returns neither {@code null} nor {@code false}; see
 * {@link Wait}.
 */
@FunctionalInterface
public interface WaitCondition<T> {

    /**
     * @param browser
     *            the browser the element lives in; most conditions ignore it
     */
    @Nullable
    T apply(TestBenchCommandExecutor browser);
}
