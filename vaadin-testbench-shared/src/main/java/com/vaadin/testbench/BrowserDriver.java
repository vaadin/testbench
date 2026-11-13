/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench;

import java.net.URL;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

/**
 * The browser a test drives, as {@code getDriver()} returns it, on every
 * backend. Shaped like Selenium's {@code WebDriver}, so the common calls read
 * as before:
 *
 * <pre>{@code
 * getDriver().get(getRootURL() + "/crud");
 * getDriver().manage().window().setSize(new Dimension(1024, 768));
 * getDriver().navigate().refresh();
 * }</pre>
 *
 * Every call waits for Vaadin first, except navigation, the window size,
 * switching windows and closing. Elements come back as
 * {@link TestBenchElement}s; prefer {@code $} to {@link #findElement(By)}. The
 * library underneath is reached through the backend's escape hatch:
 *
 * <pre>{@code
 * WebDriver driver = ((SeleniumBackend) getCommandExecutor().getBackend())
 *         .getDriver();
 * }</pre>
 */
public interface BrowserDriver extends HasTestBenchCommandExecutor {

    /** Loads {@code url} in the current window. */
    void get(String url);

    String getCurrentUrl();

    String getTitle();

    /** The DOM as HTML, as it is now. */
    String getPageSource();

    Navigation navigate();

    Options manage();

    /**
     * Runs {@code script} as the body of a function: {@code arguments[n]} is
     * the n-th argument, {@code return} gives the result, a DOM node comes back
     * as a {@link TestBenchElement}.
     */
    @Nullable
    Object executeScript(String script, @Nullable Object... args);

    /**
     * Runs {@code script} and waits for it to call back: the callback is the
     * last of the {@code arguments}, and what it is called with is the result.
     */
    @Nullable
    Object executeAsyncScript(String script, @Nullable Object... args);

    /**
     * The page's elements {@code by} matches, in the light DOM only, as
     * Selenium finds them.
     *
     * @return empty when nothing matches
     */
    List<TestBenchElement> findElements(By by);

    /**
     * The first of {@link #findElements(By)}.
     *
     * @throws NoSuchElementException
     *             if nothing matches
     */
    TestBenchElement findElement(By by);

    String getWindowHandle();

    /** Every open window's handle, in the order the windows opened. */
    Set<String> getWindowHandles();

    TargetLocator switchTo();

    /** Closes the current window; switch to another before the next call. */
    void close();

    /** Closes the browser. */
    void quit();

    interface Navigation {

        void back();

        void forward();

        void to(String url);

        void to(URL url);

        void refresh();
    }

    interface Options {

        Window window();
    }

    /**
     * The browser window. On Playwright, which has no window around the page,
     * its size is the viewport's.
     */
    interface Window {

        Dimension getSize();

        void setSize(Dimension size);
    }

    interface TargetLocator {

        /**
         * Makes the window with this handle the current one.
         *
         * @throws NoSuchElementException
         *             if no open window has the handle
         */
        BrowserDriver window(String handle);

        /**
         * The element with the focus, the page's {@code body} if none; a
         * focused element inside a shadow root gives its host.
         */
        TestBenchElement activeElement();
    }
}
