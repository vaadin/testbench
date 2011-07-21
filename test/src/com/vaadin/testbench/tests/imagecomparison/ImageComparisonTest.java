package com.vaadin.testbench.tests.imagecomparison;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.util.ImageComparison;
import com.vaadin.testbench.util.ImageComparisonUtil;

public class ImageComparisonTest {

    private static final String FOLDER = ImageComparisonTest.class.getPackage()
            .getName().replace('.', '/');

    @Before
    public void setup() {
        System.setProperty(Parameters.SCREENSHOT_DIRECTORY, "test");
        System.setProperty(Parameters.SCREENSHOT_COMPARISON_CURSOR_DETECTION,
                "true");

    }

    @Test
    public void compareSimilarImagesFull() throws IOException {
        // #7297
        testFullCompareImages("11.png", "111.png", false, 0.0);
        testFullCompareImages("17x17-similar-26.png", "17x17-similar-31.png",
                false, 0.0);
    }

    @Test
    public void compareSimilarImagesRC() throws IOException {
        // #7297
        testRCCompareImages("11.png", "111.png", false, 0.0);
        testRCCompareImages("17x17-similar-26.png", "17x17-similar-31.png",
                false, 0.0);
    }

    @Test
    public void compareBlockSizedImagesFull() throws IOException {
        // #7300
        testFullCompareImages("16x16-reference.png", "16x16-screenshot.png",
                false, 0.0);
    }

    @Test
    public void compareBlockSizedImagesRC() throws IOException {
        // #7300
        testRCCompareImages("16x16-reference.png", "16x16-screenshot.png",
                false, 0.0);
    }

    @Test
    public void compareCursorImagesRC() throws IOException {
        testRCCompareImages("cursor-off.png", "cursor-on.png", true, 0.15);
        testRCCompareImages("cursor-off.png", "cursor-on.png", false, 0.0);
    }

    @Test
    public void compareCursorImagesFull() throws IOException {
        testFullCompareImages("cursor-off.png", "cursor-on.png", true, 0.0);
    }

    private void testFullCompareImages(String referenceFilename,
            String screenshotFilename, boolean shouldBeEqual,
            double errorTolerance) throws IOException {
        URL ref = getClass().getClassLoader().getResource(
                FOLDER + "/" + referenceFilename);
        URL scr = getClass().getClassLoader().getResource(
                FOLDER + "/" + screenshotFilename);
        assertNotNull("Missing reference " + referenceFilename, ref);
        assertNotNull("Missing screenshot" + screenshotFilename, ref);
        File reference = new File(ref.getPath());
        File screenshot = new File(scr.getPath());
        junit.framework.Assert.assertTrue(screenshot.exists());

        BufferedImage referenceImage = ImageIO.read(reference);
        BufferedImage screenshotImage = ImageIO.read(screenshot);

        boolean blocksEqual = new ImageComparison().compareImages(
                referenceImage, screenshotImage, errorTolerance);

        String expected = "Images " + referenceFilename + " and "
                + screenshotFilename + " should "
                + (shouldBeEqual ? "" : "not") + " be considered equal";
        assertTrue(expected, shouldBeEqual ? blocksEqual : !blocksEqual);

    }

    private void testRCCompareImages(String referenceFilename,
            String screenshotFilename, boolean shouldBeEqual,
            double errorTolerance) throws IOException {
        URL ref = getClass().getClassLoader().getResource(
                FOLDER + "/" + referenceFilename);
        URL scr = getClass().getClassLoader().getResource(
                FOLDER + "/" + screenshotFilename);
        assertNotNull("Missing reference " + referenceFilename, ref);
        assertNotNull("Missing screenshot" + screenshotFilename, ref);
        File reference = new File(ref.getPath());
        File screenshot = new File(scr.getPath());
        junit.framework.Assert.assertTrue(screenshot.exists());

        BufferedImage referenceImage = ImageIO.read(reference);
        BufferedImage screenshotImage = ImageIO.read(screenshot);

        int[] referenceBlocks = ImageComparisonUtil
                .generateImageBlocks(referenceImage);
        int[] screenshotBlocks = ImageComparisonUtil
                .generateImageBlocks(screenshotImage);

        boolean blocksEqual = ImageComparisonUtil.blocksEqual(referenceBlocks,
                screenshotBlocks, (float) errorTolerance);

        String expected = "Images " + referenceFilename + " and "
                + screenshotFilename + " should "
                + (shouldBeEqual ? "" : "not") + " be considered equal";
        assertTrue(expected, shouldBeEqual ? blocksEqual : !blocksEqual);

    }

    @Test
    public void blocksCalculation() {
        assertEquals(0, ImageComparisonUtil.getBlocks(0));
        assertEquals(1, ImageComparisonUtil.getBlocks(1));
        assertEquals(1, ImageComparisonUtil.getBlocks(15));
        assertEquals(1, ImageComparisonUtil.getBlocks(16));
        assertEquals(2, ImageComparisonUtil.getBlocks(17));
        assertEquals(2, ImageComparisonUtil.getBlocks(31));
        assertEquals(2, ImageComparisonUtil.getBlocks(32));

    }
}
