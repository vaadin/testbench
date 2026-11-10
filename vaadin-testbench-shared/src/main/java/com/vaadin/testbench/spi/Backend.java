/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.spi;

import org.jspecify.annotations.Nullable;

/**
 * The browser TestBench drives, whatever library drives it. Element classes
 * reach the browser through this interface and {@link ElementBackend}, never
 * through Selenium or Playwright directly:
 *
 * <pre>{@code
 * Object visible = backend.executeScript(
 *         "return arguments[0].opened", dialog.getElementBackend());
 * }</pre>
 *
 * A backend only talks to the browser. Waiting for Vaadin, and turning a found
 * {@link ElementBackend} into a {@code TestBenchElement}, happen above it, the
 * same for every backend.
 * <p>
 * The Selenium backend is the reference: what a method does on Selenium today
 * is its contract, quirks included, and so are Selenium's exceptions until
 * TestBench has its own.
 */
public interface Backend {

    /**
     * Runs {@code script} in the page as the body of a function, Selenium
     * style: {@code arguments[n]} is the n-th argument, {@code return} gives
     * the result.
     *
     * @param args
     *            strings, numbers, booleans, {@code null}, this backend's
     *            {@link ElementBackend}s, and lists and maps of these
     * @return {@code null}, a {@code Boolean}, {@code Long}, {@code Double},
     *         {@code String}, an {@link ElementBackend} for a DOM node, a
     *         {@code List} of these (nested lists too), or a {@code Map}, whose
     *         values come back as the browser driver returns them
     */
    @Nullable
    Object executeScript(String script, @Nullable Object... args);
}
