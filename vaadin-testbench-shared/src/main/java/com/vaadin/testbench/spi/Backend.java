/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.spi;

import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.vaadin.testbench.Dimension;
import com.vaadin.testbench.NoSuchElementException;
import com.vaadin.testbench.StaleElementReferenceException;

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
 * {@code StaleElementReferenceException}, a search finding nothing its
 * {@code NoSuchElementException}, a driver timeout its
 * {@code TimeoutException}; never the library's own.
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

    /** Loads {@code url} in the current window and waits for it to load. */
    void navigate(String url);

    void back();

    void forward();

    void refresh();

    String getCurrentUrl();

    String getTitle();

    /** The DOM as HTML, as it is now, not as the server sent it. */
    String getPageSource();

    /**
     * The window's outer size; on a backend without windows, such as
     * Playwright, the viewport's.
     */
    Dimension getWindowSize();

    void setWindowSize(Dimension size);

    /** @return a PNG of the viewport */
    byte[] getScreenshot();

    /** The current window's handle, opaque. */
    String getWindowHandle();

    /** Every open window's handle, in the order the windows opened. */
    Set<String> getWindowHandles();

    /**
     * Makes {@code handle}'s window the current one: the one navigation,
     * scripts and input go to.
     *
     * @throws NoSuchElementException
     *             if no open window has that handle
     */
    void switchToWindow(String handle);

    /**
     * Closes the current window. Until another is switched to, the next call
     * fails.
     */
    void close();

    /** Closes every window; nothing works after it. */
    void quit();

    /**
     * Performs the steps in order, as one WebDriver actions call. Keys and
     * buttons left down stay down for the next call; the mouse stays where the
     * steps left it.
     *
     * @throws StaleElementReferenceException
     *             if a step's element is gone
     */
    void perform(List<Input> steps);
}
