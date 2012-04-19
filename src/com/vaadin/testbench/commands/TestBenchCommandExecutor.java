package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import junit.framework.AssertionFailedError;

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

import com.google.common.collect.ImmutableMap;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

/**
 * @author jonatan
 * 
 */
public class TestBenchCommandExecutor implements TestBenchCommands {
    private static Logger getLogger() {
        return Logger.getLogger(TestBenchCommandExecutor.class.getName());
    }

    private final WebDriver actualDriver;
    private final ImageComparison imageComparison;
    private final ReferenceNameGenerator referenceNameGenerator;

    public TestBenchCommandExecutor(WebDriver actualDriver,
            ImageComparison imageComparison,
            ReferenceNameGenerator referenceNameGenerator) {
        this.actualDriver = actualDriver;
        this.imageComparison = imageComparison;
        this.referenceNameGenerator = referenceNameGenerator;
    }

    protected Response execute(String driverCommand, Map<String, ?> parameters) {
        try {
            Method exec = RemoteWebDriver.class.getMethod("execute",
                    String.class, Map.class);
            exec.setAccessible(true);
            return (Response) exec.invoke(actualDriver, driverCommand,
                    parameters);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#setTestName(java.lang
     * .String)
     */
    @Override
    public void setTestName(String testName) {
        if (actualDriver instanceof RemoteWebDriver) {
            execute(TestBenchCommands.SET_TEST_NAME,
                    ImmutableMap.of("name", testName));
        } else {
            getLogger().info(
                    String.format("Currently running \"%s\"", testName));
        }
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
     * @see
     * com.vaadin.testbench.commands.TestBenchElementCommands#expectDialog()
     */
    @Override
    public void expectDialog(WebElement element, Keys... modifierKeysPressed) {
        Actions actions = new Actions(actualDriver);
        // Press modifier key(s)
        for (Keys key : modifierKeysPressed) {
            actions = actions.keyDown(key);
        }
        actions = actions.click(element);
        // Release modifier key(s)
        for (Keys key : modifierKeysPressed) {
            actions = actions.keyUp(key);
        }
        actions.perform();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#closeNotification(org
     * .openqa.selenium.WebElement)
     */
    @Override
    public boolean closeNotification(WebElement element) {
        element.click();
        // Wait for 5000 ms or until the element is no longer visible.
        int times = 0;
        while (element.isDisplayed() || times > 25) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            times++;
        }
        return element.isDisplayed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchElementCommands#showTooltip()
     */
    @Override
    public void showTooltip(WebElement element) {
        new Actions(actualDriver).moveToElement(element).perform();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#scroll(org.openqa.selenium
     * .WebElement, int)
     */
    @Override
    public void scroll(WebElement element, int scrollTop) {
        JavascriptExecutor js = (JavascriptExecutor) actualDriver;
        js.executeScript("arguments[0].setScrollTop(arguments[1])", element,
                scrollTop);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.testbench.commands.TestBenchCommands#scrollLeft(org.openqa
     * .selenium.WebElement, int)
     */
    @Override
    public void scrollLeft(WebElement element, int scrollLeft) {
        JavascriptExecutor js = (JavascriptExecutor) actualDriver;
        js.executeScript("arguments[0].setScrollLeft(arguments[1])", element,
                scrollLeft);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.testbench.commands.TestBenchCommands#waitForVaadin()
     */
    @Override
    public void waitForVaadin() {
        // @formatter:off
        String isVaadinFinished =
                "if (window.vaadin == null) {" + 
                "  return true;" +
                "}" +
                "var clients = window.vaadin.clients;" + 
                "if (clients) {" +
                "  for (var client in clients) {" + 
                "    if (clients[client].isActive()) {" + 
                "      return false;" +
                "    }" +
                "  }" + 
                "  return true;" +
                "} else {" + 
                   // A Vaadin connector was found so this is most likely a Vaadin
                   // application. Keep waiting.
                "  return false;" +
                "}";
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
    public boolean compareScreen(String referenceId) throws IOException,
            AssertionFailedError {
        String referenceName = referenceNameGenerator.generateName(referenceId,
                ((HasCapabilities) actualDriver).getCapabilities());

        for (int times = 0; times < Parameters.getMaxRetries(); times++) {
            BufferedImage screenshotImage = ImageIO
                    .read(new ByteArrayInputStream(
                            ((TakesScreenshot) actualDriver)
                                    .getScreenshotAs(OutputType.BYTES)));
            boolean equal = imageComparison.imageEqualToReference(
                    screenshotImage, referenceName,
                    Parameters.getScreenshotComparisonTolerance(),
                    Parameters.isCaptureScreenshotOnFailure());
            if (equal) {
                return true;
            }
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
    public boolean compareScreen(File reference) throws IOException,
            AssertionFailedError {
        BufferedImage image = ImageIO.read(reference);
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
            throws IOException, AssertionFailedError {
        for (int times = 0; times < Parameters.getMaxRetries(); times++) {
            BufferedImage screenshotImage = ImageIO
                    .read(new ByteArrayInputStream(
                            ((TakesScreenshot) actualDriver)
                                    .getScreenshotAs(OutputType.BYTES)));
            if (imageComparison.imageEqualToReference(screenshotImage,
                    reference, referenceName,
                    Parameters.getScreenshotComparisonTolerance(),
                    Parameters.isCaptureScreenshotOnFailure())) {
                return true;
            }
        }
        return false;
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
        if (actualDriver instanceof JavascriptExecutor) {
            try {
                JavascriptExecutor jse = (JavascriptExecutor) actualDriver;
                if (forceSync) {
                    // Force sync to get the latest server-side timing data. The
                    // server-side timing data is always one request behind.
                    jse.executeScript("window.vaadin.forceSync()");
                }
                return (List<Long>) jse
                        .executeScript("return window.vaadin.clients."
                                + getAppId() + ".getProfilingData()");
            } catch (URISyntaxException e) {
                getLogger().log(Level.SEVERE,
                        "The application URL seems to be invalid", e);
            }
        }
        return null;
    }

    private String getAppId() throws URISyntaxException {
        String appUrl = new URI(actualDriver.getCurrentUrl()).getPath();
        if (appUrl.endsWith("/")) {
            appUrl = appUrl.substring(0, appUrl.length() - 1);
        }

        String appId = appUrl;
        if ("".equals(appUrl)) {
            appId = "ROOT";
        }
        return appId.replaceAll("[^a-zA-Z0-9]", "");
    }
}
