/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.vaadin.testbench.screenshot.ImageFileUtil;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * This JUnit {@link org.junit.Rule} grabs a screenshot when a test fails.
 * Usage:
 *
 * <pre>
 * <code>
 * public class MyTestCase extends TestBenchTestCase {
 *
 *      \@Rule public ScreenshotOnFailureRule screenshotOnFailure = new ScreenshotOnFailureRule(this, true);
 *
 *      \@Test public void myTest() throws Exception {
 *          ...
 *      }
 * }
 * </code>
 * </pre>
 * <p>
 * <em>NOTE!</em> Do <b>NOT</b> call <code>driver.quit()</code> in your
 * <code>tearDown()</code> method (annotated with {@link org.junit.After}). The
 * tear down method will be run before this rule is run and if the driver is
 * closed it is no longer possible to grab a screen shot of the situation.
 * </p>
 */
public class ScreenshotOnFailureRule extends TestWatcher {

    private static final Logger logger = Logger
            .getLogger(ScreenshotOnFailureRule.class.getName());

    private HasDriver driverHolder;
    private boolean quitDriverOnFinish = false;

    /**
     * Creates a new ScreenshotOnFailureRule in the provided test case.
     *
     * @param driverHolder
     *            The {@link HasDriver} instance that holds the active WebDriver
     *            instance. Commonly this is the {@link TestBenchTestCase}.
     */
    public ScreenshotOnFailureRule(HasDriver driverHolder) {
        this.driverHolder = driverHolder;
    }

    /**
     * Creates a new ScreenshotOnFailureRule in the provided test case.
     *
     * @param driverHolder
     *            The {@link HasDriver} instance that holds the active WebDriver
     *            instance. Commonly this is the {@link TestBenchTestCase}.
     * @param quitDriverOnFinish
     *            Tells the rule whether to quit the driver when a single test
     *            has finished or not.
     */
    public ScreenshotOnFailureRule(HasDriver driverHolder,
            boolean quitDriverOnFinish) {
        this.driverHolder = driverHolder;
        this.quitDriverOnFinish = quitDriverOnFinish;
    }

    /**
     * Tells the rule whether to quit the driver when the test has finished
     * executing or to allow the user to specify this.
     *
     * @param quitDriverOnFinish
     *            true if the driver should be quit when a test has finished
     *            running.
     */
    public void setQuitDriverOnFinish(boolean quitDriverOnFinish) {
        this.quitDriverOnFinish = quitDriverOnFinish;
    }

    @Override
    protected void failed(Throwable throwable, Description description) {
        super.failed(throwable, description);

        if (driverHolder.getDriver() == null) {
            return;
        }

        WebDriver realDriver = driverHolder.getDriver();
        while (realDriver instanceof WrapsDriver) {
            realDriver = ((WrapsDriver)realDriver).getWrappedDriver();
        }
        if (realDriver instanceof RemoteWebDriver &&  ((RemoteWebDriver)realDriver).getSessionId() == null) {
            logger.warning("Unable capture failure screenshot: web driver is no longer available");
            return;
        }

        // Grab a screenshot when a test fails
        try {
            BufferedImage screenshotImage = ImageIO
                    .read(new ByteArrayInputStream(
                            ((TakesScreenshot) driverHolder.getDriver())
                                    .getScreenshotAs(OutputType.BYTES)));
            // Store the screenshot in the errors directory
            ImageFileUtil.createScreenshotDirectoriesIfNeeded();
            final File errorScreenshotFile = getErrorScreenshotFile(description);
            ImageIO.write(screenshotImage, "png",
                    errorScreenshotFile);
            logger.info("Error screenshot written to: " + errorScreenshotFile.getAbsolutePath());
        } catch (IOException e1) {
            throw new RuntimeException(
                    "There was a problem grabbing and writing a screen shot of a test failure.",
                    e1);
        }
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);
        if (quitDriverOnFinish && driverHolder != null
                && driverHolder.getDriver() != null) {
            driverHolder.getDriver().quit();
        }
    }

    /**
     * @param description
     *            test {@link Description}
     * @return Failure screenshot file.
     */
    protected File getErrorScreenshotFile(Description description) {
        return ImageFileUtil
                .getErrorScreenshotFile(description.getDisplayName() + ".png");
    }
}
