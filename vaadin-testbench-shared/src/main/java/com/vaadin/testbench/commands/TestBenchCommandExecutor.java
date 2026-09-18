/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.ScriptTimeoutException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.HasDriver;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

/**
 * Provides actual implementation of TestBenchCommands
 */
public class TestBenchCommandExecutor implements TestBenchCommands, HasDriver {

    private static Logger getLogger() {
        return LoggerFactory.getLogger(TestBenchCommandExecutor.class);
    }

    private TestBenchDriverProxy driver;
    private final ImageComparison imageComparison;
    private final ReferenceNameGenerator referenceNameGenerator;

    private boolean enableWaitForVaadin = true;
    private boolean autoScrollIntoView = true;

    /**
     * Returned by {@link #WAIT_FOR_VAADIN_SCRIPT} when Vaadin is not idle but
     * the page exposes {@code window.Vaadin.Flow.ready()}, so that the waiting
     * itself can be delegated to the browser.
     */
    private static final String AWAIT_FLOW_READY = "await-flow-ready";

    /**
     * Synchronous probe for the current readiness state. Returns {@code true}
     * when Vaadin is idle, {@link #AWAIT_FLOW_READY} when Flow can be asked to
     * report readiness asynchronously, and {@code false} when the state has to
     * be probed again later.
     * <p>
     * The {@code document.readyState} and {@code devServerIsNotLoaded} checks
     * are kept here on purpose. This probe also runs on pages that have no Flow
     * at all, and while the dev server is still starting there is no
     * {@code window.Vaadin.Flow.ready()} to defer to yet.
     */
    // @formatter:off
    private static final String WAIT_FOR_VAADIN_SCRIPT =
            "if (document.readyState != 'complete') {"
            + "  return false;"
            + "}"
            + "if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.devServerIsNotLoaded) {"
            + "  return false;"
            + "} else if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.clients) {"
            + "  var clients = window.Vaadin.Flow.clients;"
            + "  for (var client in clients) {"
            + "    if (clients[client].isActive && clients[client].isActive()) {"
            + "      return typeof window.Vaadin.Flow.ready === 'function'"
            + "        ? '" + AWAIT_FLOW_READY + "' : false;"
            + "    }"
            + "  }"
            + "  return true;"
            + "} else {"
            + "  return true;"
            + "}";

    private static final String AWAIT_FLOW_READY_SCRIPT =
            "var callback = arguments[arguments.length - 1];"
            + "window.Vaadin.Flow.ready({ timeout: arguments[0] })"
            + "  .then(function() { callback(null); })"
            + "  .catch(function(error) {"
            + "    callback(error && error.message ? error.message : 'rejected');"
            + "  });";
    // @formatter:on

    private static final long WAIT_FOR_VAADIN_TIMEOUT_MS = 40000;

    /**
     * How long a single browser-side wait may last. Kept below the WebDriver
     * default script timeout of 30s so the promise can settle and invoke the
     * callback before the driver aborts the script, and short enough that a
     * page load cancelling the pending script only costs one chunk before the
     * state is probed again.
     */
    private static final long AWAIT_FLOW_READY_CHUNK_MS = 10000;

    private static final long POLL_INTERVAL_MS = 100;

    public TestBenchCommandExecutor(ImageComparison imageComparison,
            ReferenceNameGenerator referenceNameGenerator) {
        this.imageComparison = imageComparison;
        this.referenceNameGenerator = referenceNameGenerator;
    }

    public void setDriver(TestBenchDriverProxy driver) {
        this.driver = driver;
    }

    @Override
    public String getRemoteControlName() {
        InetAddress ia = null;
        try {
            WebDriver realDriver = driver.getWrappedDriver();
            if (realDriver instanceof RemoteWebDriver) {
                RemoteWebDriver rwd = (RemoteWebDriver) realDriver;
                if (rwd.getCommandExecutor() instanceof HttpCommandExecutor) {
                    ia = InetAddress.getByName(
                            ((HttpCommandExecutor) rwd.getCommandExecutor())
                                    .getAddressOfRemoteServer().getHost());
                }
            } else {
                ia = InetAddress.getLocalHost();
            }
        } catch (UnknownHostException e) {
            getLogger().warn("Could not find name of remote control", e);
            return "unknown";
        }

        if (ia != null) {
            return String.format("%s (%s)", ia.getCanonicalHostName(),
                    ia.getHostAddress());
        }
        return null;
    }

    /**
     * Block until Vaadin reports it has finished processing server messages.
     * <p>
     * A single synchronous probe decides whether there is anything to wait for.
     * When there is, and the page exposes {@code window.Vaadin.Flow.ready()},
     * the waiting is delegated to the browser in one blocking call instead of
     * being polled from the test JVM.
     */
    public void waitForVaadin() {
        if (!enableWaitForVaadin) {
            // wait for vaadin is disabled, just return.
            return;
        }

        // Must use the wrapped driver here to avoid calling waitForVaadin
        // again
        JavascriptExecutor executor = (JavascriptExecutor) getDriver()
                .getWrappedDriver();
        long deadline = System.currentTimeMillis() + WAIT_FOR_VAADIN_TIMEOUT_MS;

        while (System.currentTimeMillis() < deadline) {
            Object state = executor.executeScript(WAIT_FOR_VAADIN_SCRIPT);
            if (Boolean.TRUE.equals(state)) {
                return;
            }
            if (AWAIT_FLOW_READY.equals(state)
                    && awaitFlowReady(executor, deadline)) {
                return;
            }
            if (state == null) {
                // This should never happen but according to
                // https://dev.vaadin.com/ticket/19703, it happens
                getLogger().debug(
                        "waitForVaadin returned null, this should never happen");
            }
            if (!pauseBeforeNextProbe(deadline)) {
                return;
            }
        }
        getLogger().debug("Vaadin was still not idle after {}ms, continuing",
                WAIT_FOR_VAADIN_TIMEOUT_MS);
    }

    /**
     * Lets Flow report readiness by awaiting {@code window.Vaadin.Flow.ready()}
     * in the browser, so that waiting costs a single WebDriver call instead of
     * one call per poll.
     *
     * @param executor
     *            the executor to run the script with
     * @param deadline
     *            the time after which waiting must stop
     * @return {@code true} if Flow reported it is idle, {@code false} if the
     *         state has to be probed again
     */
    private boolean awaitFlowReady(JavascriptExecutor executor, long deadline) {
        long chunk = Math.min(AWAIT_FLOW_READY_CHUNK_MS,
                deadline - System.currentTimeMillis());
        if (chunk <= 0) {
            return false;
        }
        try {
            Object rejection = executor
                    .executeAsyncScript(AWAIT_FLOW_READY_SCRIPT, chunk);
            if (rejection == null) {
                return true;
            }
            getLogger().debug("window.Vaadin.Flow.ready() was rejected: {}",
                    rejection);
        } catch (ScriptTimeoutException e) {
            // The pending script was aborted, e.g. by a page load or by a
            // session script timeout shorter than the chunk
            getLogger().debug(
                    "Awaiting window.Vaadin.Flow.ready() was interrupted", e);
        } catch (JavascriptException e) {
            // e.g. navigated to a page without Flow while the call was being
            // set up
            getLogger().debug("Could not call window.Vaadin.Flow.ready()", e);
        }
        return false;
    }

    /**
     * Waits for the poll interval without overrunning the deadline.
     *
     * @param deadline
     *            the time after which waiting must stop
     * @return {@code false} if waiting should stop
     */
    private boolean pauseBeforeNextProbe(long deadline) {
        long remaining = Math.min(POLL_INTERVAL_MS,
                deadline - System.currentTimeMillis());
        if (remaining <= 0) {
            return false;
        }
        try {
            Thread.sleep(remaining);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean compareScreen(String referenceId) throws IOException {
        return ScreenshotComparator.compareScreen(referenceId,
                referenceNameGenerator, imageComparison, driver, getDriver());
    }

    @Override
    public boolean compareScreen(File reference) throws IOException {
        WebDriver driver = getDriver();
        return ScreenshotComparator.compareScreen(reference, imageComparison,
                (TakesScreenshot) driver, (HasCapabilities) driver);

    }

    @Override
    public boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException {
        WebDriver driver = getDriver();
        return ScreenshotComparator.compareScreen(reference, referenceName,
                imageComparison, (TakesScreenshot) driver,
                (HasCapabilities) driver);

    }

    @Override
    public long timeSpentRenderingLastRequest() {
        List<Long> timingValues = getTimingValues(false);
        if (timingValues == null) {
            return -1;
        }
        return timingValues.get(0);
    }

    @Override
    public long totalTimeSpentRendering() {
        List<Long> timingValues = getTimingValues(false);
        if (timingValues == null) {
            return -1;
        }
        return timingValues.get(1);
    }

    @Override
    public long timeSpentServicingLastRequest() {
        List<Long> timingValues = getTimingValues(true);
        if (timingValues == null) {
            return -1;
        }
        return timingValues.get(3);
    }

    @Override
    public long totalTimeSpentServicingRequests() {
        List<Long> timingValues = getTimingValues(true);
        if (timingValues == null) {
            return -1;
        }
        return timingValues.get(2);
    }

    @SuppressWarnings("unchecked")
    private List<Long> getTimingValues(boolean poll) {
        if (poll) {
            // Get the latest server-side timing data.
            // The server-side timing data is always one request behind.
            executeScript("" //
                    + "if (!window.Vaadin || !window.Vaadin.Flow || !window.Vaadin.Flow.clients) {"
                    + "  throw 'Performance data is only available when using Vaadin Flow';"
                    + "}" //
                    + "for (client in window.Vaadin.Flow.clients) {\n" //
                    + "  if (typeof window.Vaadin.Flow.clients[client].poll === typeof Function) {\n"
                    + "    window.Vaadin.Flow.clients[client].poll();\n"
                    + "  }\n" + "}");
        }

        return (List<Long>) executeScript("" //
                + "if (!window.Vaadin || !window.Vaadin.Flow || !window.Vaadin.Flow.clients) {"
                + "  throw 'Performance data is only available when using Vaadin Flow';"
                + "}" //
                + "var pd = [0,0,0,0];\n" //
                + "var pdFound = false;\n" //
                + "for (client in window.Vaadin.Flow.clients) {\n"
                + "  if (typeof window.Vaadin.Flow.clients[client].getProfilingData === typeof Function) {\n"
                + "    var p = window.Vaadin.Flow.clients[client].getProfilingData();\n"
                + "    pd[0] += p[0];\n" //
                + "    pd[1] += p[1];\n"//
                + "    pd[2] += p[2];\n" //
                + "    pd[3] += p[3];\n" //
                + "    pdFound = true;\n" + "  }\n" + "}\n" + "if (pdFound) {\n"
                + "  return pd;\n" + "} else {\n"
                + "  throw 'Performance data is not available in production mode';\n"
                + "}");
    }

    @Override
    public void disableWaitForVaadin() {
        enableWaitForVaadin = false;
    }

    @Override
    public void enableWaitForVaadin() {
        enableWaitForVaadin = true;
    }

    /**
     * {@inheritDoc}. The default is {@code true}
     */
    @Override
    public boolean isAutoScrollIntoView() {
        return autoScrollIntoView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoScrollIntoView(boolean autoScrollIntoView) {
        this.autoScrollIntoView = autoScrollIntoView;
    }

    public Object executeScript(String script, Object... args) {
        return getDriver().executeScript(script, args);
    }

    protected Object executeAsyncScript(String script, Object... args) {
        return getDriver().executeAsyncScript(script, args);
    }

    /**
     * Return a reference to the {@link WebDriver} instance associated with this
     * {@link TestBenchCommandExecutor}
     *
     * @return a WebDriver instance
     */
    @Override
    public TestBenchDriverProxy getDriver() {
        return driver;
    }

    @Override
    public void resizeViewPortTo(final int desiredWidth,
            final int desiredHeight) throws UnsupportedOperationException {
        final int MAX_RESIZE_ATTEMPTS = 5;
        try {
            getDriver().manage().window().setPosition(new Point(0, 0));
            // Start with the desired dimensions; the loop will adjust for
            // browser chrome (title bar, borders, etc.)
            getDriver().manage().window()
                    .setSize(new Dimension(desiredWidth, desiredHeight));

            for (int attempt = 0; attempt < MAX_RESIZE_ATTEMPTS; attempt++) {
                int actualWidth = detectViewportWidth();
                int actualHeight = detectViewportHeight();

                if (actualWidth == desiredWidth
                        && actualHeight == desiredHeight) {
                    return;
                }

                int diffW = desiredWidth - actualWidth;
                int diffH = desiredHeight - actualHeight;
                Dimension currentSize = getDriver().manage().window().getSize();
                getLogger().debug(
                        "resizeViewPortTo: attempt {}, desired={}x{}, actual={}x{}, adjusting by {}x{}",
                        attempt + 1, desiredWidth, desiredHeight, actualWidth,
                        actualHeight, diffW, diffH);
                getDriver().manage().window()
                        .setSize(new Dimension(currentSize.getWidth() + diffW,
                                currentSize.getHeight() + diffH));
            }

            // Final check after all attempts
            int actualWidth = detectViewportWidth();
            int actualHeight = detectViewportHeight();
            if (actualWidth != desiredWidth || actualHeight != desiredHeight) {
                throw new UnsupportedOperationException(
                        "Viewport size couldn't be set to the desired '"
                                + desiredWidth + "," + desiredHeight + "' got '"
                                + actualWidth + "," + actualHeight + "' after "
                                + MAX_RESIZE_ATTEMPTS + " attempts.");
            }
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    "Viewport couldn't be adjusted.", e);
        }
    }

    private int detectViewportHeight() {
        // also check in IE combat mode etc + detect IE9 for extra borders in
        // combat mode (although vaadin always in std mode, function may be
        // needed earlier)
        int height = ((Number) executeScript(
                "function f() { if(typeof window.innerHeight != 'undefined') { return window.innerHeight; } if(document.documentElement && document.documentElement.offsetHeight) { return document.documentElement.offsetHeight; } w = document.body.clientHeight; if(navigator.userAgent.indexOf('Trident/5') != -1 && document.documentMode < 9) { w += 4; } return w;} return f();"))
                .intValue();
        return height;
    }

    private int detectViewportWidth() {
        // also check in IE combat mode etc + detect IE9 for extra borders in
        // combat mode (although vaadin always in std mode, function may be
        // needed earlier)
        int width = ((Number) executeScript(
                "function f() { if(typeof window.innerWidth != 'undefined') { return window.innerWidth; } if(document.documentElement && document.documentElement.offsetWidth) { return document.documentElement.offsetWidth; } w = document.body.clientWidth; if(navigator.userAgent.indexOf('Trident/5') != -1 && document.documentMode < 9) { w += 4; } return w;} return f();"))
                .intValue();
        return width;
    }

    public void focusElement(TestBenchElement testBenchElement) {
        Object ret = executeScript(
                "try { arguments[0].focus() } catch(e) {}; return null;",
                testBenchElement);
        assert (ret == null);
    }

    /**
     * Gets the name generator used for screenshot references.
     *
     * @return the name generator for screenshot references
     */
    public ReferenceNameGenerator getReferenceNameGenerator() {
        return referenceNameGenerator;
    }

    /**
     * Gets the image comparison implementation used for screenshots.
     *
     * @return the image comparison implementation
     */
    public ImageComparison getImageComparison() {
        return imageComparison;
    }

}
