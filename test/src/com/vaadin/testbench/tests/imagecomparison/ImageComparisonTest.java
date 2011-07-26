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
                "false");
    }

    @Test
    public void testPartialBlockComparisonRC() throws IOException {
        testRCCompareImages("purple-border.png", "purple-border-top-left.png",
                false);
        testRCCompareImages("purple-border.png", "purple-border.png", true);
    }

    @Test
    public void testPartialBlockComparisonFull() throws IOException {
        testFullCompareImages("purple-border.png",
                "purple-border-top-left.png", false, 0.0);
        testFullCompareImages("purple-border.png", "purple-border.png", true,
                0.0);
    }

    @Test
    public void testColorChangesRC() throws IOException {
        testRCCompareImages("text-red.png", "text-blue.png", false);
    }

    @Test
    public void testColorChangesFull() throws IOException {
        testFullCompareImages("text-red.png", "text-blue.png", false, 0);
    }

    @Test
    public void compareSimilarImagesFull() throws IOException {
        // #7297
        testFullCompareImages("11.png", "111.png", false, 0.0);
        testFullCompareImages("17x17-similar-26.png", "17x17-similar-31.png",
                false, 0.0);

        // Differ in outline should be detected using tolerance 0.0 but ignored
        // using 0.02
        testFullCompareImages("cursor2-on-outline-on.png",
                "cursor2-on-outline-off.png", false, 0.0);
        testFullCompareImages("cursor2-on-outline-on.png",
                "cursor2-on-outline-off.png", true, 0.02);

        testFullCompareImages("cursor2-off-outline-on.png",
                "cursor2-off-outline-off.png", false, 0.0);
        testFullCompareImages("cursor2-off-outline-on.png",
                "cursor2-off-outline-off.png", true, 0.02);

    }

    @Test
    public void compareSimilarImagesRC() throws IOException {
        // #7297
        testRCCompareImages("11.png", "111.png", false);
        testRCCompareImages("17x17-similar-26.png", "17x17-similar-31.png",
                false);
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
                false);
    }

    @Test
    public void compareCursorImagesRC() throws IOException {
        testRCCompareImages("cursor-on.png", "cursor-on.png", true);
        testRCCompareImages("cursor-on.png",
                "cursor-on-with-minor-difference.png", true);
        testRCCompareImages("cursor-off.png", "cursor-on.png", false);
    }

    @Test
    public void compareCursorImagesFullWithoutCursorDetection()
            throws IOException {
        testFullCompareImages("cursor-off.png", "cursor-on.png", false, 0.0);
        testFullCompareImages("cursor2-off-outline-on.png",
                "cursor2-on-outline-off.png", false, 0.0);
    }

    @Test
    public void compareCursorImagesFullWithCursorDetection() throws IOException {
        System.setProperty(Parameters.SCREENSHOT_COMPARISON_CURSOR_DETECTION,
                "true");

        testFullCompareImages("cursor-off.png", "cursor-on.png", true, 0.0);

        // Cursor and additional outline problem
        testFullCompareImages("cursor2-off-outline-on.png",
                "cursor2-on-outline-off.png", false, 0.0);
        testFullCompareImages("cursor2-off-outline-on.png",
                "cursor2-on-outline-off.png", true, 0.05);

        // Cursor but no outline problem
        testFullCompareImages("cursor2-off-outline-on.png",
                "cursor2-on-outline-on.png", true, 0.0);

    }

    @Test
    public void incorrectlyDetectedCursor() throws IOException {
        // Difference between cursor3-ref and cursor3-new is NOT a cursor
        System.setProperty(Parameters.SCREENSHOT_COMPARISON_CURSOR_DETECTION,
                "false");
        testFullCompareImages("cursor3-ref.png", "cursor3-new.png", false,
                0.025);
        System.setProperty(Parameters.SCREENSHOT_COMPARISON_CURSOR_DETECTION,
                "true");
        testFullCompareImages("cursor3-ref.png", "cursor3-new.png", false,
                0.025);

    }

    @Test
    public void comparisonTolerance() throws IOException {
        testFullCompareImages("black.png", "white.png", true, 1);
        testFullCompareImages("white.png", "black.png", true, 1);
        testFullCompareImages("black.png", "white.png", false, 0.99999999);
        testFullCompareImages("white.png", "black.png", false, 0.99999999);

        // Differs in one channel -> 33% error
        testFullCompareImages("red.png", "black.png", true, 0.334);
        testFullCompareImages("red.png", "black.png", false, 0.333);

        // Differs in two channels -> 66% error
        testFullCompareImages("red.png", "white.png", true, 0.667);
        testFullCompareImages("red.png", "white.png", false, 0.666);

    }

    @Test
    public void cursorAtEdge() throws IOException {
        System.setProperty(Parameters.SCREENSHOT_COMPARISON_CURSOR_DETECTION,
                "true");

        testFullCompareImages("cursor-bottom-edge-off.png",
                "cursor-bottom-edge-on.png", true, 0.0);

        testFullCompareImages("cursor-right-edge-off.png",
                "cursor-right-edge-on.png", true, 0.0);

        testFullCompareImages("cursor-bottom-right-off.png",
                "cursor-bottom-right-on.png", true, 0.0);

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
                + (shouldBeEqual ? "" : "not")
                + " be considered equal using tolerance " + errorTolerance;
        assertTrue(expected, shouldBeEqual ? blocksEqual : !blocksEqual);
    }

    private void testRCCompareImages(String referenceFilename,
            String screenshotFilename, boolean shouldBeEqual)
            throws IOException {
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

        String referenceHash = ImageComparisonUtil
                .generateImageHash(referenceImage);
        String screenshotHash = ImageComparisonUtil
                .generateImageHash(screenshotImage);

        boolean blocksEqual = (referenceHash.equals(screenshotHash));

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
