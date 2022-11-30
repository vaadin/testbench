/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ImageFileUtil;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;

public class ScreenshotComparator {

    private static Set<String> browsersWithoutElementScreenshot = Collections
            .synchronizedSet(new HashSet<>());

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
     * @param driver
     *            The web driver, capable of taking screenshots
     * @param screenshotContext
     *            The context of the screenshot, either a driver for a full page
     *            screenshot or an element for a screenshot of only that element
     * @param capabilities
     *            Browser capabilities
     * @return a captured image
     * @throws IOException
     */
    private static BufferedImage getScreenshot(TakesScreenshot driver,
            TakesScreenshot screenshotContext, Capabilities capabilities)
            throws IOException {
        boolean elementScreenshot = (screenshotContext instanceof WebElement);
        String browserName = capabilities.getBrowserName();

        if (elementScreenshot
                && !browsersWithoutElementScreenshot.contains(browserName)) {
            // Detect if the driver supports element screenshots or not
            try {
                byte[] screenshotBytes = screenshotContext
                        .getScreenshotAs(OutputType.BYTES);
                return ImageIO.read(new ByteArrayInputStream(screenshotBytes));
            } catch (UnsupportedCommandException e) {
                browsersWithoutElementScreenshot.add(browserName);
            } catch (WebDriverException e) {
                if (e.getCause() instanceof UnsupportedCommandException) {
                    browsersWithoutElementScreenshot.add(browserName);
                } else {
                    throw e;
                }
            }
        }

        if (elementScreenshot
                && browsersWithoutElementScreenshot.contains(browserName)) {
            // Driver does not support element screenshots, get whole screen
            // and crop
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(
                    driver.getScreenshotAs(OutputType.BYTES)));
            return cropToElement((WebElement) screenshotContext, image);
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
     * @return the cropped image
     * @throws IOException
     *             if element outside of the screenshot
     */
    public static BufferedImage cropToElement(WebElement element,
            BufferedImage fullScreen) throws IOException {
        Point loc = element.getLocation();
        Dimension size = element.getSize();
        int x = loc.x, y = loc.y;
        int w = size.width;
        int h = size.height;

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
            BufferedImage screenshotImage = ImageIO
                    .read(new ByteArrayInputStream(
                            takesScreenshot.getScreenshotAs(OutputType.BYTES)));
            if (reference == null) {
                // Store the screenshot in the errors directory and fail the
                // test
                ImageFileUtil.createScreenshotDirectoriesIfNeeded();
                ImageIO.write(screenshotImage, "png",
                        ImageFileUtil.getErrorScreenshotFile(referenceName));
                getLogger().error("No reference found for " + referenceName
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
        return LoggerFactory.getLogger(ScreenshotComparator.class);
    }

    private static void pause(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

}
