package com.vaadin.testbench.screenshot;

import com.vaadin.frp.model.Result;
import com.vaadin.testbench.screenshot.ImageFileUtil.ReferenceInfo;
import com.vaadin.testbench.screenshot.ImageUtil.ImageProperties;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator.TestcaseInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.vaadin.testbench.screenshot.ImageFileUtil.createScreenshotDirectoriesIfNeeded;
import static com.vaadin.testbench.screenshot.ImageFileUtil.getErrorScreenshotFile;
import static com.vaadin.testbench.screenshot.ImageFileUtil.getReferenceImageFileNames;
import static com.vaadin.testbench.screenshot.ImageFileUtil.readReferenceImage;
import static com.vaadin.testbench.screenshot.ImageUtil.cloneImage;
import static com.vaadin.testbench.screenshot.ImageUtil.createSampleBuffer;
import static com.vaadin.testbench.screenshot.ImageUtil.cropToBeSameSize;
import static com.vaadin.testbench.screenshot.ImageUtil.extractImageProperties;
import static com.vaadin.testbench.screenshot.ImageUtil.getBlock;
import static com.vaadin.testbench.screenshot.ImageUtil.imagesSameSize;
import static com.vaadin.testbench.screenshot.ImageUtil.rgbToLuminance;
import static com.vaadin.testbench.screenshot.ReferenceNameGenerator.generateName;
import static com.vaadin.testbench.screenshot.ScreenshotProperties.IMAGE_FILE_NAME_ENDING;
import static java.lang.Math.abs;

/**
 * Class with features for comparing 2 images.
 */
public class ImageComparison {

    /**
     * Extracted for clarity. No guarantee that it can be changed without other
     * code changes!
     */
    private static final int BLOCK_SIZE = 16;
    private static final int MAX_CURSOR_Y_BLOCKS = 3; // 3 to cover cursor up to

    // 33px high
    //
    // NOTE: All functions in the screenshot comparison package process images
    // in 16x16 blocks. This behavior is hard-coded in several places in this
    // class.
    //

    /**
     * Create a parameter descriptor object containing all relevant information
     * and temporary data buffers for a given pair of reference and screenshot
     * images. The resulting data structure is used to avoid unnecessary
     * allocations, function calls and the like in internal processing (and to
     * keep the method signatures manageable and the entire system more readily
     * maintainable).
     *
     * @param reference  a BufferedImage
     * @param screenshot a BufferedImage
     * @param tolerance  error tolerance value
     * @return a ComparisonParameters descriptor object
     */
    private static ComparisonParameters createParameters(
            final BufferedImage reference,
            final BufferedImage screenshot,
            final double tolerance) {

        ComparisonParameters p = new ComparisonParameters();
        p.refImage = reference;
        p.ssImage = screenshot;

        p.refBlock = new int[BLOCK_SIZE * BLOCK_SIZE];
        p.ssBlock = new int[BLOCK_SIZE * BLOCK_SIZE];
        p.sampleBuffer = createSampleBuffer();
        p.errorTolerance = tolerance;

        //
        // Internal testing requires image sizes to be exact - if they're not,
        // we crop the inputs and make a not of it for further use.
        //

        p.sizesDiffer = !imagesSameSize(reference, screenshot);
        if (p.sizesDiffer) {
            List<BufferedImage> images = cropToBeSameSize(reference,
                    screenshot);
            p.refImage = images.get(0);
            p.ssImage = images.get(1);
        }

        p.width = p.refImage.getWidth();
        p.height = p.refImage.getHeight();
        p.xBlocks = ImageComparisonUtil.getNrBlocks(p.width);
        p.yBlocks = ImageComparisonUtil.getNrBlocks(p.height);

        p.falseBlocks = new boolean[p.xBlocks][p.yBlocks];

        final Function<BufferedImage, ImageProperties> imgProperties = extractImageProperties();
        p.refProperties = imgProperties.apply(p.refImage);
        p.ssProperties = imgProperties.apply(p.ssImage);

        return p;
    }

    /**
     * Compare image [name] to image under /reference/. Images may differ in RGB
     * hues 0.1% (default) per macroblock of 16x16
     *
     * @param screenshotImage Image of canvas (must have proper dimensions)
     * @param errorTolerance  Allowed RGB error for a macroblock (value range 0-1 default
     *                        0.025 == 2.5%)
     * @return true if images are the same
     * @throws IOException
     */
    public boolean imageEqualToReference(BufferedImage screenshotImage,
                                         TestcaseInfo info,
                                         double errorTolerance) throws IOException {
        createScreenshotDirectoriesIfNeeded()
                .apply(null);
//        .ifPresentOrElse(aVoid -> logger().info("Screenshot Directories are OK.") ,
//                         failed -> logger().warning(failed));

        final String referenceImageFileName = generateName()
                .apply(new TestcaseInfo(
                        info.referenceId(),
                        info.browserName(),
                        info.platformName(),
                        info.version()
                )) + "." + IMAGE_FILE_NAME_ENDING;


        final List<String> referenceFileNames = getReferenceImageFileNames()
                .apply(new ReferenceInfo(
                        referenceImageFileName,
                        info.browserName(),
                        info.version()
                ));

        if (referenceFileNames.isEmpty()) {
            // We require a reference image to continue
            // Save the screenshot in the error directory.

            final File errorScreenshotFile = getErrorScreenshotFile()
                    .apply(referenceImageFileName);

            ImageIO.write(screenshotImage,
                    IMAGE_FILE_NAME_ENDING,
                    errorScreenshotFile);
//      logger().warning("No reference found for " + info.referenceId() + " in "
//                       + getScreenshotReferenceDirectory());
            return false;
        } else {
            // This is used to make the final error HTML page based on main
            // reference file only.

            final long count = referenceFileNames
                    .stream()
                    .map(referenceFileName -> readReferenceImage().apply(referenceFileName))
                    .filter(img -> img
//              .ifFailed((failed) -> logger().warning(failed))
                            .isPresent())
                    .map(Result::get)
                    .map(referenceImage -> createParameters(referenceImage,
                            screenshotImage,
                            errorTolerance))
                    .map(this::compareImages)
                    .filter(Objects::nonNull) // all OK if null
                    .peek(reporter -> reporter.createErrorImageAndHTML(info.referenceId() + "." + IMAGE_FILE_NAME_ENDING,
                            screenshotImage))
                    .count();

//      logger().info("found " + count + " differences for referenceFileId " + count);

            return count == 0;
        }
    }

    public boolean imageEqualToReference(BufferedImage screenshotImage,
                                         BufferedImage referenceImage,
                                         String referenceFileName,
                                         double errorTolerance) {
        createScreenshotDirectoriesIfNeeded()
                .apply(null);
//        .ifPresentOrElse(aVoid -> logger().info("Screenshot Directories are OK.") ,
//                         failed -> logger().warning(failed));

        ComparisonParameters param = createParameters(referenceImage,
                screenshotImage, errorTolerance);
        ScreenShotFailureReporter failureReporter = compareImages(param);

        if (failureReporter != null) {
            failureReporter.createErrorImageAndHTML(referenceFileName,
                    screenshotImage);
            return false;
        }

        return true;
    }

    /**
     * @param param a ComparisonParameters object.
     * @return
     */
    private ScreenShotFailureReporter compareImages(
            final ComparisonParameters param) {
        boolean imagesEqual = compareImage(param);
        if (param.sizesDiffer) {
            // The command has failed because the dimensions of the captured
            // image do not match the reference image.
            if (imagesEqual) {
                // The images are equal but of different size.
//        logger().info("Images are of different size.");
            } else {
                // Neither size nor contents match
//        logger().info(
//            "Images differ and are of different size.");
            }

            // TODO: Add info about which RC it was run on
            return makeFailureReporter(param);
        }

        if (imagesEqual) {
//      logger().info("Screenshot matched reference");

            // Images match. Nothing else to do.
            return null;
        }

        if (ScreenshotProperties.isScreenshotComparisonCursorDetection()) {
            // Images are not equal, still check if the only difference
            // is a blinking cursor.
            Point possibleCursorPosition = getPossibleCursorPosition(param);
            if (possibleCursorPosition != null) {
                if (isCursorTheOnlyError(possibleCursorPosition, param)) {
//          logger().info(
//              "Screenshot matched reference after removing cursor");
                    // Cursor is the only difference so we are done.
                    return null;
                } else {
//          logger().info(
//              "Screenshot did not match reference after removing cursor");
                }
            }
        }

//    logger().info("Screenshot did not match reference");
        // Make a failure reporter that is used upstream
        return makeFailureReporter(param);
    }

    private ScreenShotFailureReporter makeFailureReporter(
            final ComparisonParameters param) {
        return new ScreenShotFailureReporter(param.refImage, param.falseBlocks);
    }

    public boolean compareImages(BufferedImage referenceImage,
                                 BufferedImage screenshotImage, double errorTolerance) {
        ComparisonParameters params = createParameters(referenceImage,
                screenshotImage, errorTolerance);

        boolean imagesEqual = compareImage(params);

        // Check for cursor.
        if (!imagesEqual
                && ScreenshotProperties.isScreenshotComparisonCursorDetection()) {
            Point possibleCursorPosition = getPossibleCursorPosition(params);
            if (possibleCursorPosition != null) {
                if (isCursorTheOnlyError(possibleCursorPosition, params)) {
                    return true;
                }
            }
        }

        return imagesEqual;
    }

    private boolean compareImage(final ComparisonParameters params) {
        boolean result = true;
        final int imageWidth = params.width;
        final int imageHeight = params.height;

        // Iterate through image in 16x16 blocks
        for (int y = 0; y < imageHeight; y += BLOCK_SIZE) {
            for (int x = 0; x < imageWidth; x += BLOCK_SIZE) {
                if (blocksDiffer(x, y, params)) {
                    params.falseBlocks[x >>> 4][y >>> 4] = true;
                    result = false;
                }
            }
        }

        return result;
    }

    private boolean blocksDiffer(int x, int y,
                                 final ComparisonParameters params) {
        final int[] refBlock = getBlock(params.refProperties, x, y,
                params.refBlock, params.sampleBuffer);
        final int[] ssBlock = getBlock(params.ssProperties, x, y,
                params.ssBlock, params.sampleBuffer);

        for (int i = 0; i < (BLOCK_SIZE * BLOCK_SIZE); ++i) {
            if (refBlock[i] != ssBlock[i]) {
                return rgbCompare(refBlock, ssBlock) > params.errorTolerance;
            }
        }

        return false;
    }

    /**
     * Calculates the difference between pixels in the block.
     *
     * @param referenceBlock
     * @param screenshotBlock
     * @return Difference %
     */
    private double rgbCompare(final int[] referenceBlock,
                              final int[] screenshotBlock) {
        int sum = 0;
        assert (referenceBlock.length == screenshotBlock.length);

        // Build sums from all available colors Red, Green and Blue
        for (int i = 0, l = referenceBlock.length; i < l; i++) {
            final int targetPixel = referenceBlock[i];

            if ((targetPixel >>> 24) < 255) {

                // Only completely opaque pixels are considered. Pixels with
                // alpha values below 255 (== fully opaque) are considered
                // masked and differences in these pixels won't be reported.
                continue;
            }

            final int testPixel = screenshotBlock[i];

            sum += abs(((targetPixel & 0xff0000) >> 16)
                    - ((testPixel & 0xff0000) >> 16));

            sum += abs(((targetPixel & 0xff00) >> 8)
                    - ((testPixel & 0xff00) >> 8));

            sum += abs((targetPixel & 0xff) - (testPixel & 0xff));
        }

        return sum / ((double) referenceBlock.length * 255 * 3);
    }

    /**
     * Determine if an error is possibly caused by a blinking cursor and, in
     * that case, at what position the cursor might be. Uses only information
     * about the blocks that have failed to determine if the failure _possibly
     * can_ be caused by a cursor that is either missing from the reference or
     * the screenshot.
     *
     * @param params a ComparisonParameters object. .
     * @return A Point referring to the x and y coordinates in the image where
     * the cursor might be (actually might be inside a 16x32 block
     * starting from that point)
     */
    private Point getPossibleCursorPosition(
            final ComparisonParameters params) {
        int firstErrorBlockX = 0;
        int firstErrorBlockY = 0;
        boolean errorFound = false;

        final int xBlocks = params.xBlocks;
        final int yBlocks = params.yBlocks;
        final boolean[][] blocksWithErrors = params.falseBlocks;

        // Look for 1-2 blocks with errors. If and only if the blocks are
        // vertically adjacent to each other we might have a cursor problem.
        // This is the only case we are looking for.

        for (int y = 0; y < yBlocks; y++) {
            for (int x = 0; x < xBlocks; x++) {
                if (blocksWithErrors[x][y]) {
                    if (errorFound) {
                        // This is the second erroneous block we have found
                        if (x != firstErrorBlockX) {
                            // This error is not below the first
                            return null;
                        }
                        if ((y - firstErrorBlockY) > (MAX_CURSOR_Y_BLOCKS
                                - 1)) {
                            // Cursor is accepted for 1-3 blocks above each
                            // other (we are moving from top down).
                            return null;
                        }

                        // This is directly below the first so it is OK

                    } else {
                        // This is the first erroneous block we have found
                        firstErrorBlockX = x;
                        firstErrorBlockY = y;
                        errorFound = true;
                    }
                }
            }
        }

        Point value = null;

        if (errorFound) {
            // Return value is the pixel coordinates for the first block
            value = new Point(firstErrorBlockX << 4, firstErrorBlockY << 4);
        }

        return value;
    }

    /**
     * Check if failure is because of a blinking text cursor.
     *
     * @param possibleCursorPosition The position in the image where a cursor possibly can be found
     *                               (pixel coordinates of the top left corner of a block)
     * @param params                 a ComparisonParameters object.
     * @return true If cursor (vertical line of at least 5 pixels if not at the
     * top or bottom) is the only difference between the images.
     */
    private boolean isCursorTheOnlyError(Point possibleCursorPosition,
                                         final ComparisonParameters params) {
        int x = possibleCursorPosition.x;
        int y = possibleCursorPosition.y;

        final int width, height;
        if (params.width <= x + BLOCK_SIZE) {
            width = params.width - x;
        } else {
            width = BLOCK_SIZE;
        }

        if (params.height <= y + MAX_CURSOR_Y_BLOCKS * BLOCK_SIZE) {
            height = params.height - y;
        } else {
            height = MAX_CURSOR_Y_BLOCKS * BLOCK_SIZE;
        }

//    logger().info("Looking for cursor starting from " + x + "," + y
//                  + " using width=" + width + " and height=" + height);
        // getBlock writes the result into the int[] sample parameter, in
        // this case params.refBlock and params.ssBlock. params.sampleBuffer
        // is re-used between calls, and is used for temporary data storage.

        final int[] refBlock = params.refBlock;
        final int[] ssBlock = params.ssBlock;
        final int[] sampleBuffer = params.sampleBuffer;
        final ImageProperties refProperties = params.refProperties;
        final ImageProperties ssProperties = params.ssProperties;

        getBlock(refProperties, x, y, refBlock, sampleBuffer);
        getBlock(ssProperties, x, y, ssBlock, sampleBuffer);

        // Find first different pixel in the block of possibleCursorPosition.
        int cursorX = -1;
        int cursorStartY = -1;
        findCursor:
        for (int j = 0, l = (height > BLOCK_SIZE ? BLOCK_SIZE
                : height); j < l; j++) {
            for (int i = 0; i < width; i++) {

                // If found differing pixel
                if (isCursorPixel(params.refBlock[i + j * width],
                        params.ssBlock[i + j * width])) {

                    // Workaround to ignore vertical lines in certain tests.
                    if (j < l - 1
                            && !isCursorPixel(refBlock[i + (j + 1) * width],
                            ssBlock[i + (j + 1) * width])) {
                        continue;
                    }

                    cursorX = i;
                    cursorStartY = j;
//          logger().info("Cursor found at " + cursorX + ","
//                        + cursorStartY);
                    break findCursor;
                }
            }
        }

        if (-1 == cursorX) {
//      logger().info("Cursor not found");
            // No difference found with cursor detection threshold.
            return false;
        }

        // Find the end of the cursor
        int cursorEndY = cursorStartY;
        // Start from what we already know is a cursor pixel because that is
        // certainly inside the current block.
        int idx = cursorX + (cursorEndY) * width;
        int diff = 0;
        while (cursorEndY < height - 1
                && cursorEndY < MAX_CURSOR_Y_BLOCKS * BLOCK_SIZE
                && isCursorPixel(params.refBlock[idx], params.ssBlock[idx])) {

            if (++cursorEndY == BLOCK_SIZE) {
                // We need to get the next block and adjust our index by the
                // size of previous block
                params.refBlock = getBlock(refProperties, x, y + BLOCK_SIZE,
                        refBlock, sampleBuffer);
                params.ssBlock = getBlock(ssProperties, x, y + BLOCK_SIZE,
                        ssBlock, sampleBuffer);

                diff = width * BLOCK_SIZE;
            }

            idx = cursorX + (cursorEndY) * width - diff;
        }

        // Only accept as cursor if at least 5 pixels or at top or bottom of
        // image.
        if (cursorEndY - cursorStartY < 5 && cursorStartY > 0
                && cursorEndY < height - 1) {
//      logger().info("Cursor rejected at " + cursorX + ","
//                    + cursorStartY + "-" + cursorEndY);
            return false;
        }

//    logger().info("Cursor is at " + cursorX + "," + cursorStartY
//                  + "-" + cursorEndY);
        // Copy pixels from reference over the possible cursor, then
        // re-compare blocks. Pixels at cursor position are always copied
        // from the reference image regardless of which of the images has
        // the cursor.

        // Get width x height sub-images to compare
        final BufferedImage referenceCopy = params.refImage.getSubimage(x, y,
                width, height);

        // Clone the subImage of the screenshot to avoid accidentally
        // modifying the original screenshot.
        final BufferedImage screenshotCopy = cloneImage()
                .apply(params.ssImage.getSubimage(x, y, width, height));

        // Copy pixels for cursor position from reference to screenshot
        for (int j = cursorStartY; j <= cursorEndY; ++j) {
            int referenceRgb = referenceCopy.getRGB(cursorX, j);
            screenshotCopy.setRGB(cursorX, j, referenceRgb);
        }

        // Compare one or two blocks of reference with modified screenshot
        return compareImage(createParameters(referenceCopy, screenshotCopy,
                params.errorTolerance));

    }

    /**
     * Luminance based comparison of a pixel in two images for cursor detection.
     *
     * @param pixel1
     * @param pixel2
     * @return
     */
    private boolean isCursorPixel(int pixel1, int pixel2) {
        double lum1 = rgbToLuminance().apply(pixel1);
        double lum2 = rgbToLuminance().apply(pixel2);

        int blackMaxLuminance = 80;
        int whiteMinLuminance = 150;
        // Cursor must be dark and the other pixel bright enough for
        // contrast.
        return (lum1 < blackMaxLuminance && lum2 > whiteMinLuminance)
                || (lum1 > whiteMinLuminance && lum2 < blackMaxLuminance);
    }

    /**
     * Data collection type, used as input for image comparison functions. Saves
     * unnecessary buffer allocations.
     */
    private static class ComparisonParameters {

        private ImageProperties refProperties = null;
        private ImageProperties ssProperties = null;

        private BufferedImage refImage = null;
        private BufferedImage ssImage = null;

        private int[] refBlock = null;
        private int[] ssBlock = null;
        private int[] sampleBuffer = null;
        private boolean[][] falseBlocks = null;

        private int width = 0;
        private int height = 0;
        private int xBlocks = 0;
        private int yBlocks = 0;

        private double errorTolerance = 0.0;
        private boolean sizesDiffer = false;
    }
}
