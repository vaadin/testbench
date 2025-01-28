/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

/**
 * Provides actual implementation of TestBenchCommands
 */
public class TestBenchCommandExecutor
        implements TestBenchCommands, JavascriptExecutor {

    private static Logger logger = Logger
            .getLogger(TestBenchCommandExecutor.class.getName());

    private static Logger getLogger() {
        return logger;
    }

    private final WebDriver actualDriver;
    private final ImageComparison imageComparison;
    private ReferenceNameGenerator referenceNameGenerator;
    private boolean enableWaitForVaadin = true;
    private boolean autoScrollIntoView = true;

    public TestBenchCommandExecutor(WebDriver actualDriver,
            ImageComparison imageComparison,
            ReferenceNameGenerator referenceNameGenerator) {
        this.actualDriver = actualDriver;
        this.imageComparison = imageComparison;
        this.referenceNameGenerator = referenceNameGenerator;
    }

    @Override
    public String getRemoteControlName() {
        InetAddress ia = null;
        try {
            if (actualDriver instanceof RemoteWebDriver) {
                RemoteWebDriver rwd = (RemoteWebDriver) actualDriver;
                if (rwd.getCommandExecutor() instanceof HttpCommandExecutor) {
                    ia = InetAddress.getByName(
                            ((HttpCommandExecutor) rwd.getCommandExecutor())
                                    .getAddressOfRemoteServer().getHost());
                }
            } else {
                ia = InetAddress.getLocalHost();
            }
        } catch (UnknownHostException e) {
            getLogger().log(Level.WARNING,
                    "Could not find name of remote control", e);
            return "unknown";
        }

        if (ia != null) {
            return String.format("%s (%s)", ia.getCanonicalHostName(),
                    ia.getHostAddress());
        }
        return null;
    }

    @Override
    public void waitForVaadin() {
        if (!enableWaitForVaadin
                || !(actualDriver instanceof JavascriptExecutor)) {
            // wait for vaadin is disabled, just return.
            return;
        }

        // @formatter:off
        String isVaadinFinished = "if (document.readyState != 'complete') {"
                + "  return false;"
                + "}"
                + "if (window.vaadin == null) {"
                + "  return true;"
                + "}"
                + "var clients = window.vaadin.clients;"
                + "if (clients) {"
                + "  for (var client in clients) {"
                + "    if (clients[client].isActive()) {"
                + "      return false;"
                + "    }"
                + "  }"
                + "  return true;"
                + "} else {"
                // A Vaadin connector was found so this is most likely a Vaadin
                // application. Keep waiting.
                + "  return false;"
                + "}";
        // @formatter:on
        JavascriptExecutor js = (JavascriptExecutor) actualDriver;
        long timeoutTime = System.currentTimeMillis() + 20000;
        Boolean finished = false;
        while (System.currentTimeMillis() < timeoutTime && !finished) {
            finished = (Boolean) js.executeScript(isVaadinFinished);
            if (finished == null) {
                // This should never happen but according to
                // https://dev.vaadin.com/ticket/19703, it happens
                getLogger().fine(
                        "waitForVaadin returned null, this should never happen");
                finished = false;
            }
        }
    }

    @Override
    public boolean compareScreen(String referenceId) throws IOException {
        WebDriver driver = getDriver();
        return ScreenshotComparator.compareScreen(referenceId,
                referenceNameGenerator, imageComparison,
                (TakesScreenshot) driver, (HasCapabilities) driver);
    }

    @Override
    public boolean compareScreen(File reference) throws IOException {
        WebDriver driver = getWrappedDriver();
        return ScreenshotComparator.compareScreen(reference, imageComparison,
                (TakesScreenshot) driver, (HasCapabilities) driver);

    }

    @Override
    public boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException {
        WebDriver driver = getWrappedDriver();
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
    private List<Long> getTimingValues(boolean forceSync) {
        // @formatter:off
        String getProfilingData = "var pd = [0,0,0,0];\n"
                + "for (client in window.vaadin.clients) {\n"
                + "  var p = window.vaadin.clients[client].getProfilingData();\n"
                + "  pd[0] += p[0];\n"
                + "  pd[1] += p[1];\n"
                + "  pd[2] += p[2];\n"
                + "  pd[3] += p[3];\n"
                + "}\n"
                + "return pd;\n";
        // @formatter:on
        if (actualDriver instanceof JavascriptExecutor) {
            try {
                JavascriptExecutor jse = (JavascriptExecutor) actualDriver;
                if (forceSync) {
                    // Force sync to get the latest server-side timing data. The
                    // server-side timing data is always one request behind.
                    jse.executeScript("window.vaadin.forceSync()");
                }
                return (List<Long>) jse.executeScript(getProfilingData);
            } catch (Exception e) {
                // Could not retrieve profiling data, just return null.
            }
        }
        return null;
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

    @Override
    public Object executeScript(String script, Object... args) {
        if (actualDriver instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) actualDriver).executeScript(script,
                    args);
        }
        throw new RuntimeException("The driver is not a JavascriptExecutor");
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        if (actualDriver instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) actualDriver)
                    .executeAsyncScript(script, args);
        }
        throw new RuntimeException("The driver is not a JavascriptExecutor");
    }

    /**
     * Return a reference to the {@link WebDriver} instance associated with this
     * {@link TestBenchCommandExecutor}
     *
     * @return a WebDriver instance
     */
    public WebDriver getWrappedDriver() {
        return actualDriver;
    }

    /**
     * Return a reference to the {@link WebDriver} instance associated with this
     * {@link TestBenchCommandExecutor}
     *
     * @return a WebDriver instance
     */
    public WebDriver getDriver() {
        // This is actually never called. The overridden version in
        // TestBenchDriverProy is. This class hierarchy is wrong in several
        // ways.
        return actualDriver;
    }

    @Override
    public void resizeViewPortTo(final int desiredWidth,
            final int desiredHeight) throws UnsupportedOperationException {
        try {
            actualDriver.manage().window().setPosition(new Point(0, 0));

            // first try with mac FF, these will change from plat to plat and
            // browser setup to another
            int extrah = 106;
            int extraw = 0;
            actualDriver.manage().window().setSize(new Dimension(
                    desiredWidth + extraw, desiredHeight + extrah));

            int actualWidth = detectViewportWidth();
            int actualHeight = detectViewportHeight();

            int diffW = desiredWidth - actualWidth;
            int diffH = desiredHeight - actualHeight;

            if (diffH != 0 || diffW != 0) {
                actualDriver.manage().window()
                        .setSize(new Dimension(desiredWidth + extraw + diffW,
                                desiredHeight + extrah + diffH));
            }
            actualWidth = detectViewportWidth();
            actualHeight = detectViewportHeight();
            if (desiredWidth != actualWidth || desiredHeight != actualHeight) {
                throw new Exception(
                        "Viewport size couldn't be set to desired.");
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    "Viewport couldn't be adjusted.", e);
        }
    }

    private int detectViewportHeight() {
        // also check in IE combat mode etc + detect IE9 for extra borders in
        // combat mode (although vaadin always in std mode, function may be
        // needed earlier)
        int height = ((Number) ((JavascriptExecutor) actualDriver)
                .executeScript(
                        "function f() { if(typeof window.innerHeight != 'undefined') { return window.innerHeight; } if(document.documentElement && document.documentElement.offsetHeight) { return document.documentElement.offsetHeight; } w = document.body.clientHeight; if(navigator.userAgent.indexOf('Trident/5') != -1 && document.documentMode < 9) { w += 4; } return w;} return f();"))
                                .intValue();
        return height;
    }

    private int detectViewportWidth() {
        // also check in IE combat mode etc + detect IE9 for extra borders in
        // combat mode (although vaadin always in std mode, function may be
        // needed earlier)
        int width = ((Number) ((JavascriptExecutor) actualDriver).executeScript(
                "function f() { if(typeof window.innerWidth != 'undefined') { return window.innerWidth; } if(document.documentElement && document.documentElement.offsetWidth) { return document.documentElement.offsetWidth; } w = document.body.clientWidth; if(navigator.userAgent.indexOf('Trident/5') != -1 && document.documentMode < 9) { w += 4; } return w;} return f();"))
                        .intValue();
        return width;
    }

    public void focusElement(TestBenchElement testBenchElement) {
        // The actual driver is _always_ a JavaScriptExecutor - if it is not,
        // something is terribly wrong.
        JavascriptExecutor jse = (JavascriptExecutor) actualDriver;

        Object ret = jse.executeScript(
                "try { arguments[0].focus() } catch(e) {}; return null;",
                testBenchElement);
        assert (ret == null);
    }

    public ReferenceNameGenerator getReferenceNameGenerator() {
        return referenceNameGenerator;
    }

    public void setReferenceNameGenerator(
            ReferenceNameGenerator nameGenerator) {
        Objects.requireNonNull(nameGenerator,
                "ReferenceNameGenerator can not be null");
        referenceNameGenerator = nameGenerator;
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
