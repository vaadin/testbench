/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
    private final ReferenceNameGenerator referenceNameGenerator;
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
        String isVaadinFinished = "if (window.vaadin == null) {"
                + "  return true;" + "}"
                + "var clients = window.vaadin.clients;" + "if (clients) {"
                + "  for (var client in clients) {"
                + "    if (clients[client].isActive()) {"
                + "      return false;" + "    }" + "  }" + "  return true;"
                + "} else {" +
                // A Vaadin connector was found so this is most likely a Vaadin
                // application. Keep waiting.
                "  return false;" + "}";
        // @formatter:on
        long timeoutTime = System.currentTimeMillis() + 20000;
        Boolean finished = false;
        while (System.currentTimeMillis() < timeoutTime && !finished) {
            finished = (Boolean) executeScript(isVaadinFinished);
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
        WebDriver driver = getWrappedDriver();
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
                + "  pd[0] += p[0];\n" + "  pd[1] += p[1];\n"
                + "  pd[2] += p[2];\n" + "  pd[3] += p[3];\n" + "}\n"
                + "return pd;\n";
        // @formatter:on
        if (forceSync) {
            // Force sync to get the latest server-side timing data. The
            // server-side timing data is always one request behind.
            executeScript("window.vaadin.forceSync()");
        }
        return (List<Long>) executeScript(getProfilingData);
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

    /**
     * {@inheritDoc}
     * <p>
     * This method wraps any returned {@link WebElement} as
     * {@link TestBenchElement}.
     */
    @Override
    public Object executeScript(String script, Object... args) {
        if (actualDriver instanceof JavascriptExecutor) {
            return wrapElementOrElements(((JavascriptExecutor) actualDriver)
                    .executeScript(script, args), this);

        }
        throw new RuntimeException("The driver is not a JavascriptExecutor");
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method wraps any returned {@link WebElement} as
     * {@link TestBenchElement}.
     */
    @Override
    public Object executeAsyncScript(String script, Object... args) {
        if (actualDriver instanceof JavascriptExecutor) {
            return wrapElementOrElements(((JavascriptExecutor) actualDriver)
                    .executeAsyncScript(script, args), this);
        }
        throw new RuntimeException("The driver is not a JavascriptExecutor");
    }

    /**
     * Wraps any {@link WebElement} found inside the object inside a
     * {@link TestBenchElement}.
     * <p>
     * Traverses through any {@link List} found inside the object and wraps any
     * elements inside the list, recursively. The behavior is compatible with
     * what {@link #executeScript(String, Object...)} and
     * {@link #executeAsyncScript(String, Object...)} returns.
     * <p>
     * Does not modify the argument, instead creates a new object containing the
     * wrapped elements and other possible values.
     *
     * @param elementElementsOrValues
     *            an object containing a {@link WebElement}, a {@link List} of
     *            {@link WebElement WebElements} or something completely
     *            different.
     * @param tbCommandExecutor
     *            the {@link TestBenchCommandExecutor} related to the driver
     *            instance
     */
    private static Object wrapElementOrElements(Object elementElementsOrValues,
            TestBenchCommandExecutor tbCommandExecutor) {
        if (elementElementsOrValues instanceof List) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            List<Object> list = (List) elementElementsOrValues;
            List<Object> newList = new ArrayList<>();

            for (Object value : list) {
                newList.add(wrapElementOrElements(value, tbCommandExecutor));
            }
            return newList;
        } else if (elementElementsOrValues instanceof WebElement) {
            if (elementElementsOrValues instanceof TestBenchElement) {
                return elementElementsOrValues;
            } else {
                return wrapElement((WebElement) elementElementsOrValues,
                        tbCommandExecutor);
            }
        } else {
            return elementElementsOrValues;
        }
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
