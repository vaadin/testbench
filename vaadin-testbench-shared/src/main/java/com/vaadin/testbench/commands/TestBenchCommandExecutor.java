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
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
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
    // @formatter:off
    private static final String WHEN_READY_CHECK_SCRIPT =
            "return typeof window.Vaadin !== 'undefined'"
            + " && typeof window.Vaadin.Flow !== 'undefined'"
            + " && typeof window.Vaadin.Flow.whenReady === 'function'";

    private static final String WAIT_FOR_VAADIN_ASYNC_SCRIPT =
            "var callback = arguments[arguments.length - 1];"
            + "window.Vaadin.Flow.whenReady(callback);";
    // @formatter:on
    private static final long WAIT_FOR_VAADIN_TIMEOUT_MS = 40000;

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
     * First waits for the dev server to start (if needed), then makes a single
     * async call to {@code Flow.whenReady} which handles all remaining
     * readiness checks.
     */
    public void waitForVaadin() {
        if (!enableWaitForVaadin) {
            return;
        }

        // Must use the wrapped driver here to avoid calling waitForVaadin
        // again
        WebDriver wrappedDriver = getDriver().getWrappedDriver();
        long deadline = System.currentTimeMillis() + WAIT_FOR_VAADIN_TIMEOUT_MS;

        if (!waitForDevServer(wrappedDriver, deadline)) {
            return;
        }

        // Single async call — whenReady handles all readiness checks
        Duration originalTimeout = wrappedDriver.manage().timeouts()
                .getScriptTimeout();
        try {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                return;
            }
            wrappedDriver.manage().timeouts()
                    .scriptTimeout(Duration.ofMillis(remaining));
            ((JavascriptExecutor) wrappedDriver)
                    .executeAsyncScript(WAIT_FOR_VAADIN_ASYNC_SCRIPT);
        } catch (ScriptTimeoutException e) {
            // Silent timeout
        } finally {
            wrappedDriver.manage().timeouts().scriptTimeout(originalTimeout);
        }
    }

    /**
     * Polls until {@code Vaadin.Flow.whenReady} is a function, indicating the
     * dev server has started and Flow is loaded. Uses Java-side polling so it
     * survives page reloads during dev server startup.
     *
     * @return {@code true} if whenReady became available, {@code false} if the
     *         deadline was reached
     */
    private boolean waitForDevServer(WebDriver wrappedDriver, long deadline) {
        while (System.currentTimeMillis() < deadline) {
            try {
                Boolean ready = (Boolean) ((JavascriptExecutor) wrappedDriver)
                        .executeScript(WHEN_READY_CHECK_SCRIPT);
                if (Boolean.TRUE.equals(ready)) {
                    return true;
                }
            } catch (Exception e) {
                // Page may be reloading, continue polling
            }
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                return false;
            }
            try {
                Thread.sleep(Math.min(500, remaining));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
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
