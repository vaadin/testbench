/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.screenshot;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.vaadin.testbench.testutils.ImageLoader;

public class ScreenshotMaskingTest {
    private static final String FOLDER = ScreenshotMaskingTest.class
            .getPackage().getName().replace('.', '/') + "/masking";

    @Test
    public void testEqualScreenshotsPass() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        Assertions.assertTrue(ic.imageEqualToReference(screenshotImage,
                referenceImage, "google1.png", 0));
    }

    @Test
    public void testDifferentScreenshotsFail() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google2.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        Assertions.assertFalse(ic.imageEqualToReference(screenshotImage,
                referenceImage, "google1.png", 0));
    }

    public void testEqualScreenshotsBlankMaskPass() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "blank-mask.png");
        Assertions.assertTrue(ic.imageEqualToReference(screenshotImage,
                referenceImage, "google1.png", 0));
    }

    @Test
    public void testEqualScreenshotsMaskedPass() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1-masked.png");
        Assertions.assertTrue(ic.imageEqualToReference(screenshotImage,
                referenceImage, "google1-masked.png", 0));
    }

    @Test
    public void testDifferentScreenshotsMaskedPass() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google2.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1-masked.png");
        Assertions.assertTrue(ic.imageEqualToReference(screenshotImage,
                referenceImage, "google1-masked.png", 0));
    }

    @Test
    public void testDifferentScreenshotsMaskedPass2() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google3.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1-masked2.png");
        // Just enough tolerance to allow the focused text field and darker font
        // on the buttons.
        Assertions.assertTrue(ic.imageEqualToReference(screenshotImage,
                referenceImage, "google1-masked2.png", 0.16));
    }

    @Test
    public void testDifferentScreenshotsChangesInUnmaskedAreasFail()
            throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google3.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1-masked.png");
        Assertions.assertFalse(ic.imageEqualToReference(screenshotImage,
                referenceImage, "google1-masked.png", 0));
    }

}
