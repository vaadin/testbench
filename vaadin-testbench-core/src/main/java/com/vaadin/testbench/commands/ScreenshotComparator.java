package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ImageFileUtil;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

public class ScreenshotComparator {

    private static Boolean supportsElementScreenshots = null;

    public static boolean compareScreen(String referenceId,
            ReferenceNameGenerator referenceNameGenerator,
            ImageComparison imageComparison, TakesScreenshot takesScreenshot,
            HasCapabilities driver) throws IOException {
        Capabilities capabilities = driver.getCapabilities();
        String referenceName = referenceNameGenerator.generateName(referenceId,
                capabilities);

        for (int times = 0; times < Parameters
                .getMaxScreenshotRetries(); times++) {
            boolean equal = imageComparison.imageEqualToReference(
                    getScreenshot((TakesScreenshot) driver, takesScreenshot,
                            capabilities),
                    referenceName,
                    Parameters.getScreenshotComparisonTolerance(),
                    capabilities);
            if (equal) {
                return true;
            }
            pause(Parameters.getScreenshotRetryDelay());
        }
        return false;
    }

    /**
     * Captures a screenshot of the given screen (if parameter is a driver) or
     * the given element (if the parameter is a WebElement).
     *
     * @param screenshotContext
     * @param capabilities
     * @param isIE8
     *            <code>true</code> if this is IE8, <code>false</code> otherwise
     * @return
     * @throws IOException
     */
    private static BufferedImage getScreenshot(TakesScreenshot driver,
            TakesScreenshot screenshotContext, Capabilities capabilities)
            throws IOException {
        boolean elementScreenshot = (screenshotContext instanceof WebElement);

        if (elementScreenshot && supportsElementScreenshots == null) {
            if (BrowserUtil.isPhantomJS(capabilities)) {
                // PhantomJS will die if you try to detect this...
                supportsElementScreenshots = false;
            } else {
                // Detect if the driver supports element screenshots or not
                try {
                    byte[] screenshotBytes = screenshotContext
                            .getScreenshotAs(OutputType.BYTES);
                    supportsElementScreenshots = true;
                    return ImageIO
                            .read(new ByteArrayInputStream(screenshotBytes));
                } catch (UnsupportedCommandException e) {
                    supportsElementScreenshots = false;
                } catch (WebDriverException e) {
                    if (e.getCause() instanceof UnsupportedCommandException) {
                        supportsElementScreenshots = false;
                    } else {
                        throw e;
                    }
                }
            }
        }

        if (elementScreenshot && !supportsElementScreenshots) {
            // Driver does not support element screenshots, get whole screen
            // and crop
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(
                    driver.getScreenshotAs(OutputType.BYTES)));
            return cropToElement((WebElement) screenshotContext, image,
                    BrowserUtil.isIE8(capabilities));
        } else {
            // Element or full screen image
            return ImageIO.read(new ByteArrayInputStream(
                    screenshotContext.getScreenshotAs(OutputType.BYTES)));
        }
    }

    /**
     * Crops the image to show only the element. If the element is partly off
     * screen, crops to show the part of the element which is in the screenshot
     *
     * @param element
     *            the element to retain in the screenshot
     * @param fullScreen
     *            the full screen image
     * @param isIE8
     *            true if the browser is IE8
     * @return
     * @throws IOException
     */
    public static BufferedImage cropToElement(WebElement element,
            BufferedImage fullScreen, boolean isIE8) throws IOException {
        Point loc = element.getLocation();
        Dimension size = element.getSize();
        int x = loc.x, y = loc.y;
        int w = size.width;
        int h = size.height;

        if (isIE8) {
            // IE8 border...
            x += 2;
            y += 2;
        }
        if (x >= 0 && x < fullScreen.getWidth()) {
            // X loc on screen
            // Get the part of the element which is on screen
            w = Math.min(fullScreen.getWidth() - x, w);
        } else {
            throw new IOException("Element x is outside the screenshot (x: " + x
                    + ", y: " + y + ")");
        }

        if (y >= 0 && y < fullScreen.getHeight()) {
            // Y loc on screen
            // Get the part of the element which is on screen
            h = Math.min(fullScreen.getHeight() - y, h);
        } else {
            throw new IOException("Element y is outside the screenshot (x: " + x
                    + ", y: " + y + ")");
        }

        return fullScreen.getSubimage(x, y, w, h);
    }

    public static boolean compareScreen(File reference,
            ImageComparison imageComparison, TakesScreenshot takesScreenshot,
            HasCapabilities driver) throws IOException {
        BufferedImage image = null;
        try {
            image = ImageIO.read(reference);
        } catch (IIOException e) {
            // Don't worry, an error screen shot will be generated that later
            // can be used as the reference
        }
        return compareScreen(image, reference.getName(), imageComparison,
                takesScreenshot, driver);
    }

    public static boolean compareScreen(BufferedImage reference,
            String referenceName, ImageComparison imageComparison,
            TakesScreenshot takesScreenshot, HasCapabilities driver)
            throws IOException {
        for (int times = 0; times < Parameters
                .getMaxScreenshotRetries(); times++) {
            BufferedImage screenshotImage = getScreenshot(
                    (TakesScreenshot) driver, takesScreenshot,
                    driver.getCapabilities());
            if (reference == null) {
                // Store the screenshot in the errors directory and fail the
                // test
                ImageFileUtil.createScreenshotDirectoriesIfNeeded();
                ImageIO.write(screenshotImage, "png",
                        ImageFileUtil.getErrorScreenshotFile(referenceName));
                getLogger().severe("No reference found for " + referenceName
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

    private static Logger getLogger() {
        return Logger.getLogger(ScreenshotComparator.class.getName());
    }

    private static void pause(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

}
