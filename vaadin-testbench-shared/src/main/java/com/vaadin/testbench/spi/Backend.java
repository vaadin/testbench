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
 * Object visible = backend.executeScript("return arguments[0].opened",
 *         dialog.getElementBackend());
 * }</pre>
 *
 * A backend only talks to the browser. Waiting for Vaadin, and turning a found
 * {@link ElementBackend} into a {@code TestBenchElement}, happen above it, the
 * same for every backend.
 * <p>
 * The Selenium backend is the reference: what a method does on Selenium today
 * is its contract, quirks included. A gone node throws TestBench's own
 * {@code StaleElementReferenceException}, and a search finding nothing its
 * {@code NoSuchElementException}; until the element classes catch those instead
 * of Selenium's, the older methods still throw Selenium's.
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

    /**
     * Runs {@code script} as {@link #executeScript(String, Object...)} does,
     * then waits for it to call back, Selenium style: the callback is the last
     * of the {@code arguments}, and what it is called with is the result.
     *
     * <pre>{@code
     * backend.executeAsyncScript(
     *         "var done = arguments[arguments.length - 1];"
     *                 + "setTimeout(() => done(arguments[0].value), 100);",
     *         field.getElementBackend());
     * }</pre>
     *
     * @return as {@link #executeScript(String, Object...)}
     */
    @Nullable
    Object executeAsyncScript(String script, @Nullable Object... args);

    /**
     * The browser's name as WebDriver reports it: {@code "chrome"} (or
     * {@code "chrome-headless-shell"}), {@code "firefox"},
     * {@code "MicrosoftEdge"}, {@code "safari"}. Known without asking the
     * browser, so cheap enough to call before every property read.
     *
     * @return {@code null} if the browser does not say
     */
    @Nullable
    String getBrowserName();
}
