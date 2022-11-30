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
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.testutils.ImageLoader;

public class ImageComparisonTest {

    private static final String FOLDER = ImageComparisonTest.class.getPackage()
            .getName().replace('.', '/');

    private String previousScreenshotErrorDirectory;
    private String previousScreenshotReferenceDirectory;
    private boolean previousScreenshotComparisonCursorDetection;

    @BeforeEach
    public void setup() {
        previousScreenshotErrorDirectory = Parameters
                .getScreenshotErrorDirectory();
        previousScreenshotReferenceDirectory = Parameters
                .getScreenshotReferenceDirectory();
        previousScreenshotComparisonCursorDetection = Parameters
                .isScreenshotComparisonCursorDetection();

        URL screenshotUrl = getClass().getClassLoader().getResource(FOLDER);
        Parameters.setScreenshotErrorDirectory(
                screenshotUrl.getPath() + "/errors");
        Parameters.setScreenshotReferenceDirectory(
                screenshotUrl.getPath() + "/reference");
        Parameters.setScreenshotComparisonCursorDetection(false);

    }

    @AfterEach
    public void teardown() {
        Parameters
                .setScreenshotErrorDirectory(previousScreenshotErrorDirectory);
        Parameters.setScreenshotReferenceDirectory(
                previousScreenshotReferenceDirectory);
        Parameters.setScreenshotComparisonCursorDetection(
                previousScreenshotComparisonCursorDetection);
    }

    @Test
    public void testBigImage() throws IOException {
        Parameters.setScreenshotComparisonCursorDetection(true);
        testFullCompareImages("big-image.png", "big-image-ss.png", false, 0.05);
    }

    @Test
    public void testPartialBlockComparisonRC() throws IOException {
        testRCCompareImages("purple-border.png", "purple-border-top-left.png",
                false);
        testRCCompareImages("purple-border.png", "purple-border.png", true);
    }

    @Test
    public void testPartialBlockComparisonFull() throws IOException {
        testFullCompareImages("purple-border.png", "purple-border-top-left.png",
                false, 0.0);
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
    public void compareSimilarImagesWithCursorAndMinorDifferences()
            throws IOException {
        Parameters.setScreenshotComparisonCursorDetection(false);
        testFullCompareImages("no-outline-cursor.png", "outline-no-cursor.png",
                false, 0.025);
        Parameters.setScreenshotComparisonCursorDetection(true);
        testFullCompareImages("no-outline-cursor.png", "outline-no-cursor.png",
                true, 0.025);
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
    public void compareCursorImagesFullWithCursorDetection()
            throws IOException {
        Parameters.setScreenshotComparisonCursorDetection(true);

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
        Parameters.setScreenshotComparisonCursorDetection(false);
        testFullCompareImages("cursor3-ref.png", "cursor3-new.png", false,
                0.025);
        Parameters.setScreenshotComparisonCursorDetection(true);
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
        Parameters.setScreenshotComparisonCursorDetection(true);

        testFullCompareImages("cursor-bottom-edge-off.png",
                "cursor-bottom-edge-on.png", true, 0.0);

        testFullCompareImages("cursor-right-edge-off.png",
                "cursor-right-edge-on.png", true, 0.0);

        // A test where cursor is so close to the right edge that old way
        // results in four failing blocks instead of two.
        testFullCompareImages("cursor-right-edge-overlap-off.png",
                "cursor-right-edge-overlap-on.png", true, 0.0);

        testFullCompareImages("cursor-bottom-right-off.png",
                "cursor-bottom-right-on.png", true, 0.0);

    }

    @Test
    public void cursorAt15x1() throws IOException {
        Parameters.setScreenshotComparisonCursorDetection(true);

        testFullCompareImages("white-33x33-cursor-15x1-cursoron.png",
                "white-33x33-cursor-15x1-cursoroff.png", true, 0.0);

    }

    @Test
    public void cursorAt15x17() throws IOException {
        Parameters.setScreenshotComparisonCursorDetection(true);

        testFullCompareImages("white-33x33-cursor-15x17-cursoron.png",
                "white-33x33-cursor-15x17-cursoroff.png", true, 0.0);

    }

    @Test
    public void cursorAt15x16() throws IOException {
        Parameters.setScreenshotComparisonCursorDetection(true);

        testFullCompareImages("white-33x33-cursor-15x16-cursoron.png",
                "white-33x33-cursor-15x16-cursoroff.png", true, 0.0);

    }

    @Test
    public void canCompareReferenceSmallerThanScreenshot() throws IOException {
        ImageComparison ic = new ImageComparison();
        Assertions.assertFalse(ic.imageEqualToReference(
                ImageLoader.loadImage(FOLDER, "screenshot1008x767.png"),
                "reference738x624", 1, null));
    }

    private void testFullCompareImages(String referenceFilename,
            String screenshotFilename, boolean shouldBeEqual,
            double errorTolerance) throws IOException {
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                referenceFilename);
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                screenshotFilename);

        boolean blocksEqual = new ImageComparison()
                .compareImages(referenceImage, screenshotImage, errorTolerance);

        String expected = "Images " + referenceFilename + " and "
                + screenshotFilename + " should " + (shouldBeEqual ? "" : "not")
                + " be considered equal using tolerance " + errorTolerance;
        Assertions.assertTrue(shouldBeEqual ? blocksEqual : !blocksEqual,
                expected);
    }

    private void testRCCompareImages(String referenceFilename,
            String screenshotFilename, boolean shouldBeEqual)
            throws IOException {
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                referenceFilename);
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                screenshotFilename);

        String referenceHash = ImageComparisonUtil
                .generateImageHash(referenceImage);
        String screenshotHash = ImageComparisonUtil
                .generateImageHash(screenshotImage);

        boolean blocksEqual = (referenceHash.equals(screenshotHash));

        String expected = "Images " + referenceFilename + " and "
                + screenshotFilename + " should " + (shouldBeEqual ? "" : "not")
                + " be considered equal";
        Assertions.assertTrue(shouldBeEqual ? blocksEqual : !blocksEqual,
                expected);

    }

    @Test
    public void blocksCalculation() {
        Assertions.assertEquals(0, ImageComparisonUtil.getNrBlocks(0));
        Assertions.assertEquals(1, ImageComparisonUtil.getNrBlocks(1));
        Assertions.assertEquals(1, ImageComparisonUtil.getNrBlocks(15));
        Assertions.assertEquals(1, ImageComparisonUtil.getNrBlocks(16));
        Assertions.assertEquals(2, ImageComparisonUtil.getNrBlocks(17));
        Assertions.assertEquals(2, ImageComparisonUtil.getNrBlocks(31));
        Assertions.assertEquals(2, ImageComparisonUtil.getNrBlocks(32));
    }

    @Test
    public void testImageEqualToReference_equalImages_returnsNull()
            throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "16x16-reference.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "16x16-reference.png");
        Assertions.assertTrue(ic.imageEqualToReference(screenshotImage,
                referenceImage, "16x16-reference.png", 0));
    }

    @Test
    public void testImageEqualToReference_differingImages_returnsNotNull()
            throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "16x16-screenshot.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "16x16-reference.png");
        Assertions.assertFalse(ic.imageEqualToReference(screenshotImage,
                referenceImage, "16x16-reference.png", 0));
    }

    @Test
    public void testImageEqualToReference_equalImagesDifferentSize_false()
            throws IOException {
        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "17x17-similar-26.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "16x16-reference.png");
        Assertions.assertFalse(ic.imageEqualToReference(screenshotImage,
                referenceImage, "16x16-reference.png", 0));
    }

    @Test
    public void testImageEqualToReference_cursorError_true()
            throws IOException {
        Parameters.setScreenshotComparisonCursorDetection(true);

        ImageComparison ic = new ImageComparison();
        BufferedImage screenshotImage = ImageLoader.loadImage(FOLDER,
                "cursor-on.png");
        BufferedImage referenceImage = ImageLoader.loadImage(FOLDER,
                "cursor-off.png");
        Assertions.assertTrue(ic.imageEqualToReference(screenshotImage,
                referenceImage, "cursor-off.png", 0));
    }

    @Test
    public void testCursorComparisonAt0x15() throws IOException {
        Parameters.setScreenshotComparisonCursorDetection(true);
        testFullCompareImages("cursor-like-diff-at-0x15-ref.png",
                "cursor-like-diff-at-0x15-new.png", false, 0.01);
    }
}
