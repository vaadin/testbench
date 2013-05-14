package com.vaadin.testbench.screenshot;

import com.vaadin.testbench.testutils.ImageLoader;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScreenshotMaskingTest {
    private static final String FOLDER = ScreenshotMaskingTest.class
            .getPackage().getName().replace('.', '/')
            + "/masking";

    @Test
    public void testEqualScreenshotsPass() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        assertTrue(ic.imageEqualToReference(screenshotImage, referenceImage,
                "google1.png", 0));
    }

    @Test
    public void testDifferentScreenshotsFail() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google2.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        assertFalse(ic.imageEqualToReference(screenshotImage, referenceImage,
                "google1.png", 0));
    }

    public void testEqualScreenshotsBlankMaskPass() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "blank-mask.png");
        assertTrue(ic.imageEqualToReference(screenshotImage, referenceImage,
                "google1.png", 0));
    }

    @Test
    public void testEqualScreenshotsMaskedPass() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google1.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1-masked.png");
        assertTrue(ic.imageEqualToReference(screenshotImage, referenceImage,
                "google1-masked.png", 0));
    }

    @Test
    public void testDifferentScreenshotsMaskedPass() throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google2.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1-masked.png");
        assertTrue(ic.imageEqualToReference(screenshotImage, referenceImage,
                "google1-masked.png", 0));
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
        assertTrue(ic.imageEqualToReference(screenshotImage, referenceImage,
                "google1-masked2.png", 0.16));
    }

    @Test
    public void testDifferentScreenshotsChangesInUnmaskedAreasFail()
            throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "google3.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "google1-masked.png");
        assertFalse(ic.imageEqualToReference(screenshotImage, referenceImage,
                "google1-masked.png", 0));
    }

}
