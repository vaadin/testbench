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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Dimension;
import com.vaadin.testbench.Point;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;
import com.vaadin.testbench.spi.BrowserBackend;
import com.vaadin.testbench.spi.ElementBackend;

/**
 * Provides actual implementation of TestBenchCommands
 */
public class TestBenchCommandExecutor implements TestBenchCommands {

    private static Logger getLogger() {
        return LoggerFactory.getLogger(TestBenchCommandExecutor.class);
    }

    private BrowserBackend backend;
    private final ImageComparison imageComparison;
    private final ReferenceNameGenerator referenceNameGenerator;

    private boolean enableWaitForVaadin = true;
    private boolean autoScrollIntoView = true;
    // @formatter:off
    String WAIT_FOR_VAADIN_SCRIPT =
            "if (document.readyState != 'complete') {"
            + "  return false;"
            + "}"
            + "if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.devServerIsNotLoaded) {"
            + "  return false;"
            + "} else if (window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.clients) {"
            + "  var clients = window.Vaadin.Flow.clients;"
            + "  for (var client in clients) {"
            + "    if (clients[client].isActive()) {"
            + "      return false;"
            + "    }"
            + "  }"
            + "  return true;"
            + "} else {"
            + "  return true;"
            + "}";
    // @formatter:on

    // A hook for testing purposes
    private Runnable waitForVaadinLoopHook;

    public TestBenchCommandExecutor(ImageComparison imageComparison,
            ReferenceNameGenerator referenceNameGenerator) {
        this.imageComparison = imageComparison;
        this.referenceNameGenerator = referenceNameGenerator;
    }

    /**
     * @param backend
     *            the browser to drive, as {@code TestBench.createDriver} sets
     *            it
     */
    public void setBackend(BrowserBackend backend) {
        this.backend = backend;
    }

    /**
     * The browser this executor drives, below TestBench: nothing called on it
     * waits for Vaadin. Cast it to reach the browser library itself, as an
     * escape hatch:
     *
     * <pre>{@code
     * WebDriver driver = ((SeleniumBackend) getCommandExecutor().getBackend())
     *         .getDriver();
     * }</pre>
     */
    public BrowserBackend getBackend() {
        return backend;
    }

    @Override
    public String getRemoteControlName() {
        InetAddress ia = null;
        try {
            ia = getBackend().getRemoteAddress();
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
     */
    public void waitForVaadin() {
        if (!enableWaitForVaadin) {
            // wait for vaadin is disabled, just return.
            return;
        }

        long timeoutTime = System.currentTimeMillis() + 40000;
        Boolean finished = false;
        while (System.currentTimeMillis() < timeoutTime && !finished) {
            if (waitForVaadinLoopHook != null) {
                waitForVaadinLoopHook.run();
            }
            finished = (Boolean) getBackend()
                    .executeScript(WAIT_FOR_VAADIN_SCRIPT);
            if (finished == null) {
                // This should never happen but according to
                // https://dev.vaadin.com/ticket/19703, it happens
                getLogger().debug(
                        "waitForVaadin returned null, this should never happen");
                finished = false;
            }
        }
    }

    @Override
    public boolean compareScreen(String referenceId) throws IOException {
        waitForVaadin();
        return ScreenshotComparator.compareScreen(referenceId,
                referenceNameGenerator, imageComparison, getBackend(), null);
    }

    @Override
    public boolean compareScreen(File reference) throws IOException {
        waitForVaadin();
        return ScreenshotComparator.compareScreen(reference, imageComparison,
                getBackend(), null);

    }

    @Override
    public boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException {
        waitForVaadin();
        return ScreenshotComparator.compareScreen(reference, referenceName,
                imageComparison, getBackend(), null);

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

    /**
     * Waits for Vaadin, then runs {@code script} as the body of a function.
     *
     * @param args
     *            available as {@code arguments[0]...arguments[N]}; may hold
     *            {@link TestBenchElement}s, also inside lists and maps
     * @return the script's result, DOM nodes wrapped as
     *         {@link TestBenchElement}s, also inside lists
     */
    public Object executeScript(String script, Object... args) {
        waitForVaadin();
        return fromBackend(
                getBackend().executeScript(script, toBackendArgs(args)));
    }

    /**
     * Waits for Vaadin, then runs {@code script} until it calls back, Selenium
     * style:
     *
     * <pre>{@code
     * executeAsyncScript(
     *         "var done = arguments[arguments.length - 1];"
     *                 + "setTimeout(() => done(arguments[0].value), 100);",
     *         field);
     * }</pre>
     *
     * @return what the callback is called with, converted as
     *         {@link #executeScript(String, Object...)} converts a result
     */
    public Object executeAsyncScript(String script, Object... args) {
        waitForVaadin();
        return fromBackend(
                getBackend().executeAsyncScript(script, toBackendArgs(args)));
    }

    private static Object[] toBackendArgs(Object[] args) {
        Object[] backendArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            backendArgs[i] = toBackend(args[i]);
        }
        return backendArgs;
    }

    private static Object toBackend(Object value) {
        if (value instanceof TestBenchElement element) {
            return element.getElementBackend();
        }
        if (value instanceof Collection<?> collection) {
            List<Object> list = new ArrayList<>(collection.size());
            for (Object item : collection) {
                list.add(toBackend(item));
            }
            return list;
        }
        // An array of elements, as GridTRElement.getCells passes; a script
        // sees a list as the same array
        if (value instanceof Object[] array) {
            return toBackend(Arrays.asList(array));
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            map.forEach((key, item) -> copy.put(key, toBackend(item)));
            return copy;
        }
        return value;
    }

    private Object fromBackend(Object value) {
        if (value instanceof ElementBackend element) {
            return TestBench.createElement(TestBenchElement.class, element,
                    this);
        }
        if (value instanceof List<?> list) {
            List<Object> converted = new ArrayList<>(list.size());
            for (Object item : list) {
                converted.add(fromBackend(item));
            }
            return converted;
        }
        return value;
    }

    @Override
    public void resizeViewPortTo(final int desiredWidth,
            final int desiredHeight) throws UnsupportedOperationException {
        final int MAX_RESIZE_ATTEMPTS = 5;
        try {
            getBackend().setWindowPosition(new Point(0, 0));
            // Start with the desired dimensions; the loop will adjust for
            // browser chrome (title bar, borders, etc.)
            getBackend()
                    .setWindowSize(new Dimension(desiredWidth, desiredHeight));

            for (int attempt = 0; attempt < MAX_RESIZE_ATTEMPTS; attempt++) {
                int actualWidth = detectViewportWidth();
                int actualHeight = detectViewportHeight();

                if (actualWidth == desiredWidth
                        && actualHeight == desiredHeight) {
                    return;
                }

                int diffW = desiredWidth - actualWidth;
                int diffH = desiredHeight - actualHeight;
                Dimension currentSize = getBackend().getWindowSize();
                getLogger().debug(
                        "resizeViewPortTo: attempt {}, desired={}x{}, actual={}x{}, adjusting by {}x{}",
                        attempt + 1, desiredWidth, desiredHeight, actualWidth,
                        actualHeight, diffW, diffH);
                getBackend().setWindowSize(
                        new Dimension(currentSize.getWidth() + diffW,
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
