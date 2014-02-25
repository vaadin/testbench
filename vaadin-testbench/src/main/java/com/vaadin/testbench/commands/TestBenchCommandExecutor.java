/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.testbench.HasDriver;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ImageFileUtil;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

/**
 * @author Jonatan Kronqvist / Vaadin Ltd
 */
public class TestBenchCommandExecutor implements TestBenchCommands,
        JavascriptExecutor, HasDriver {

    private static Logger logger = Logger
            .getLogger(TestBenchCommandExecutor.class.getName());

    private static Logger getLogger() {
        return Logger.getLogger(TestBenchCommandExecutor.class.getName());
    }

    private final WebDriver actualDriver;
    private final ImageComparison imageComparison;
    private final ReferenceNameGenerator referenceNameGenerator;
    private boolean enableWaitForVaadin = true;

    public TestBenchCommandExecutor(WebDriver actualDriver,
            ImageComparison imageComparison,
            ReferenceNameGenerator referenceNameGenerator) {
        this.actualDriver = actualDriver;
        this.imageComparison = imageComparison;
        this.referenceNameGenerator = referenceNameGenerator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#getRemoteControlName()
     */
    @Override
    public String getRemoteControlName() {
        InetAddress ia = null;
        try {
            if (actualDriver instanceof RemoteWebDriver) {
                RemoteWebDriver rwd = (RemoteWebDriver) actualDriver;
                if (rwd.getCommandExecutor() instanceof HttpCommandExecutor) {
                    ia = InetAddress.getByName(((HttpCommandExecutor) rwd
                            .getCommandExecutor()).getAddressOfRemoteServer()
                            .getHost());
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchCommands#waitForVaadin()
     */
    @Override
    public void waitForVaadin() {
        if (!enableWaitForVaadin) {
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
        JavascriptExecutor js = (JavascriptExecutor) actualDriver;
        long timeoutTime = System.currentTimeMillis() + 20000;
        boolean finished = false;
        while (System.currentTimeMillis() < timeoutTime && !finished) {
            finished = (Boolean) js.executeScript(isVaadinFinished);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#screenshotEqualToReference
     * (java.lang.String)
     */
    @Override
    public boolean compareScreen(String referenceId) throws IOException {
        Capabilities capabilities = ((HasCapabilities) actualDriver)
                .getCapabilities();
        String referenceName = referenceNameGenerator.generateName(referenceId,
                capabilities);

        for (int times = 0; times < Parameters.getMaxScreenshotRetries(); times++) {
            BufferedImage screenshotImage = ImageIO
                    .read(new ByteArrayInputStream(
                            ((TakesScreenshot) actualDriver)
                                    .getScreenshotAs(OutputType.BYTES)));
            boolean equal = imageComparison
                    .imageEqualToReference(screenshotImage, referenceName,
                            Parameters.getScreenshotComparisonTolerance(),
                            capabilities);
            if (equal) {
                return true;
            }
            pause(Parameters.getScreenshotRetryDelay());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#compareScreen(java.io
     * .File)
     */
    @Override
    public boolean compareScreen(File reference) throws IOException {
        BufferedImage image = null;
        try {
            image = ImageIO.read(reference);
        } catch (IIOException e) {
            // Don't worry, an error screen shot will be generated that later
            // can be used as the reference
        }
        return compareScreen(image, reference.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#compareScreen(java.awt
     * .image.BufferedImage)
     */
    @Override
    public boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException {
        for (int times = 0; times < Parameters.getMaxScreenshotRetries(); times++) {
            BufferedImage screenshotImage = ImageIO
                    .read(new ByteArrayInputStream(
                            ((TakesScreenshot) actualDriver)
                                    .getScreenshotAs(OutputType.BYTES)));
            if (reference == null) {
                // Store the screenshot in the errors directory and fail the
                // test
                ImageFileUtil.createScreenshotDirectoriesIfNeeded();
                ImageIO.write(screenshotImage, "png",
                        ImageFileUtil.getErrorScreenshotFile(referenceName));
                logger.severe("No reference found for " + referenceName
                        + " in "
                        + ImageFileUtil.getScreenshotReferenceDirectory());
                return false;
            }
            if (imageComparison.imageEqualToReference(screenshotImage,
                    reference, referenceName,
                    Parameters.getScreenshotComparisonTolerance())) {
                return true;
            }
            pause(Parameters.getScreenshotRetryDelay());
        }
        return false;
    }

    private void pause(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#timeSpentRenderingLastRequest
     * ()
     */
    @Override
    public long timeSpentRenderingLastRequest() {
        List<Long> timingValues = getTimingValues(false);
        if (timingValues == null) {
            return -1;
        }
        return timingValues.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#totalTimeSpentRendering()
     */
    @Override
    public long totalTimeSpentRendering() {
        List<Long> timingValues = getTimingValues(false);
        if (timingValues == null) {
            return -1;
        }
        return timingValues.get(1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#timeSpentServicingLastRequest
     * ()
     */
    @Override
    public long timeSpentServicingLastRequest() {
        List<Long> timingValues = getTimingValues(true);
        if (timingValues == null) {
            return -1;
        }
        return timingValues.get(3);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchCommands#
     * totalTimeSpentServicingRequests()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#findElementByVaadinSelector
     * (java.lang.String)
     */
    @Override
    public WebElement findElementByVaadinSelector(String selector) {
        // @formatter:off
        String findByVaadinScript = "var clients = window.vaadin.clients;"
                + "for (client in clients) {"
                + "  var element = clients[client].getElementByPath(arguments[0]);"
                + "  if (element) {" + "    return element;" + "  }" + "}"
                + "return null;";
        // @formatter:on
        if (actualDriver instanceof JavascriptExecutor) {
            JavascriptExecutor jse = (JavascriptExecutor) actualDriver;
            WebElement element = null;
            if (selector.contains("::")) {
                String client = selector.substring(0, selector.indexOf("::"));
                String path = selector.substring(selector.indexOf("::") + 2);
                try {
                    element = (WebElement) jse
                            .executeScript("return window.vaadin.clients."
                                    + client + ".getElementByPath(\"" + path
                                    + "\");");
                } catch (Exception e) {
                    throw new NoSuchElementException(
                            "Vaadin could not find an element with the selector "
                                    + selector, e);
                }
            } else {
                element = (WebElement) jse.executeScript(findByVaadinScript,
                        selector);
            }
            if (element != null) {
                return TestBench.createElement(element, this);
            } else {
                throw new NoSuchElementException(
                        "Vaadin could not find an element with the selector "
                                + selector);
            }
        }
        throw new RuntimeException("WebDriver is not a JavascriptExecutor");
    }

    @Override
    public void disableWaitForVaadin() {
        enableWaitForVaadin = false;
    }

    @Override
    public void enableWaitForVaadin() {
        enableWaitForVaadin = true;
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
            return ((JavascriptExecutor) actualDriver).executeAsyncScript(
                    script, args);
        }
        throw new RuntimeException("The driver is not a JavascriptExecutor");
    }

    public WebDriver getWrappedDriver() {
        return null;
    }

    @Override
    public void resizeViewPortTo(final int desiredWidth, final int desiredHeight)
            throws UnsupportedOperationException {
        try {
            actualDriver.manage().window().setPosition(new Point(0, 0));

            // first try with mac FF, these will change from plat to plat and
            // browser setup to another
            int extrah = 106;
            int extraw = 0;
            actualDriver
                    .manage()
                    .window()
                    .setSize(
                            new Dimension(desiredWidth + extraw, desiredHeight
                                    + extrah));

            int actualWidth = detectViewportWidth();
            int actualHeight = detectViewportHeight();

            int diffW = desiredWidth - actualWidth;
            int diffH = desiredHeight - actualHeight;

            if (diffH != 0 || diffW != 0) {
                actualDriver
                        .manage()
                        .window()
                        .setSize(
                                new Dimension(desiredWidth + extraw + diffW,
                                        desiredHeight + extrah + diffH));
            }
            actualWidth = detectViewportWidth();
            actualHeight = detectViewportHeight();
            if (desiredWidth != actualWidth || desiredHeight != actualHeight) {
                throw new Exception("Viewport size couldn't be set to desired.");
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
                .executeScript("function f() { if(typeof window.innerHeight != 'undefined') { return window.innerHeight; } if(document.documentElement && document.documentElement.offsetHeight) { return document.documentElement.offsetHeight; } w = document.body.clientHeight; if(navigator.userAgent.indexOf('Trident/5') != -1 && document.documentMode < 9) { w += 4; } return w;} return f();"))
                .intValue();
        return height;
    }

    private int detectViewportWidth() {
        // also check in IE combat mode etc + detect IE9 for extra borders in
        // combat mode (although vaadin always in std mode, function may be
        // needed earlier)
        int width = ((Number) ((JavascriptExecutor) actualDriver)
                .executeScript("function f() { if(typeof window.innerWidth != 'undefined') { return window.innerWidth; } if(document.documentElement && document.documentElement.offsetWidth) { return document.documentElement.offsetWidth; } w = document.body.clientWidth; if(navigator.userAgent.indexOf('Trident/5') != -1 && document.documentMode < 9) { w += 4; } return w;} return f();"))
                .intValue();
        return width;
    }

    @Override
    public WebDriver getDriver() {
        return actualDriver;
    }
}
