/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
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
        return Logger.getLogger(TestBenchCommandExecutor.class.getName());
    }

    private TestBenchDriverProxy driver;
    private final ImageComparison imageComparison;
    private final ReferenceNameGenerator referenceNameGenerator;

    private boolean enableWaitForVaadin = true;
    private boolean autoScrollIntoView = true;
    // @formatter:off
    String WAIT_FOR_VAADIN_SCRIPT =
            "if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.clients) {"
            + "  var clients = window.Vaadin.Flow.clients;"
            + "  for (var client in clients) {"
            + "    if (clients[client].isActive()) {"
            + "      return false;"
            + "    }"
            + "  }"
            + "  return true;"
            + "} else if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.devServerIsNotLoaded) {"
            + "  return false;"
            + "} else {"
            + "  return true;"
            + "}";
    // @formatter:on

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

    /**
     * Block until Vaadin reports it has finished processing server messages.
     */
    public void waitForVaadin() {
        if (!enableWaitForVaadin) {
            // wait for vaadin is disabled, just return.
            return;
        }

        long timeoutTime = System.currentTimeMillis() + 20000;
        Boolean finished = false;
        while (System.currentTimeMillis() < timeoutTime && !finished) {
            // Must use the wrapped driver here to avoid calling waitForVaadin
            // again
            finished = (Boolean) ((JavascriptExecutor) getDriver()
                    .getWrappedDriver()).executeScript(WAIT_FOR_VAADIN_SCRIPT);
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
                    + "  window.Vaadin.Flow.clients[client].poll();\n" + "}");
        }

        return (List<Long>) executeScript("" //
                + "if (!window.Vaadin || !window.Vaadin.Flow || !window.Vaadin.Flow.clients) {"
                + "  throw 'Performance data is only available when using Vaadin Flow';"
                + "}" //
                + "var pd = [0,0,0,0];\n" //
                + "for (client in window.Vaadin.Flow.clients) {\n"
                + "  if (!window.Vaadin.Flow.clients[client].getProfilingData) {"
                + "    throw 'Performance data is not available in production mode';"
                + "  }" //
                + "  var p = window.Vaadin.Flow.clients[client].getProfilingData();\n"
                + "  pd[0] += p[0];\n" //
                + "  pd[1] += p[1];\n"//
                + "  pd[2] += p[2];\n" //
                + "  pd[3] += p[3];\n" //
                + "}\n" + "return pd;\n");
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
        try {
            getDriver().manage().window().setPosition(new Point(0, 0));

            // first try with mac FF, these will change from plat to plat and
            // browser setup to another
            int extrah = 106;
            int extraw = 0;
            getDriver().manage().window().setSize(new Dimension(
                    desiredWidth + extraw, desiredHeight + extrah));

            int actualWidth = detectViewportWidth();
            int actualHeight = detectViewportHeight();

            int diffW = desiredWidth - actualWidth;
            int diffH = desiredHeight - actualHeight;

            if (diffH != 0 || diffW != 0) {
                driver.manage().window()
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
