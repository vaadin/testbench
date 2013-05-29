/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.testbench.screenshot;

import com.vaadin.testbench.Parameters;
import org.junit.Assert;
import org.openqa.selenium.Capabilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Class with features for comparing 2 images.
 */
public class ImageComparison {

    /**
     * Compare image [name] to image under /reference/. Images may differ in RGB
     * hues 0.1% (default) per macroblock of 16x16
     *
     * @param screenshotImage Image of canvas (must have proper dimensions)
     * @param referenceFileId File id for this image without .png extension
     * @param errorTolerance  Allowed RGB error for a macroblock (value range 0-1 default
     *                        0.025 == 2.5%)
     * @param capabilities    browser capabilities
     * @return true if images are the same
     * @throws IOException
     */
    public boolean imageEqualToReference(BufferedImage screenshotImage,
                                         String referenceFileId, double errorTolerance,
                                         Capabilities capabilities)
            throws IOException {
        ImageFileUtil.createScreenshotDirectoriesIfNeeded();

        List<String> referenceFileNames = ImageFileUtil
                .getReferenceImageFileNames(referenceFileId + ".png",
                        capabilities);

        if (referenceFileNames.isEmpty()) {
            // We require a reference image to continue
            // Save the screenshot in the error directory.
            ImageIO.write(
                    screenshotImage,
                    "png",
                    ImageFileUtil.getErrorScreenshotFile(referenceFileId
                            + ".png"));
            Assert.fail("No reference found for " + referenceFileId + " in "
                    + ImageFileUtil.getScreenshotReferenceDirectory());
        }

        // this is used to make the final error HTML page based on main
        // reference file only
        ScreenShotFailureReporter failureReporter = null;

        for (String referenceFileName : referenceFileNames) {
            BufferedImage referenceImage;
            referenceImage = ImageFileUtil
                    .readReferenceImage(referenceFileName);

            failureReporter = compareToReference(screenshotImage,
                    referenceImage, errorTolerance);
            if (failureReporter == null) {
                return true;
            }
        }

        /*
         * The command has failed because the captured image differs from the
         * reference image
         */
        if (failureReporter != null) {
            failureReporter.createErrorImageAndHTML(referenceFileId + ".png",
                    screenshotImage);
        }

        // The images differ
        return false;
    }

    public boolean imageEqualToReference(BufferedImage screenshotImage,
                                         BufferedImage referenceImage, String referenceFileName,
                                         double errorTolerance) {
        ImageFileUtil.createScreenshotDirectoriesIfNeeded();

        ScreenShotFailureReporter failureReporter = compareToReference(
                screenshotImage, referenceImage, errorTolerance);
        if (failureReporter != null) {
            failureReporter.createErrorImageAndHTML(referenceFileName,
                    screenshotImage);
            return false;
        }
        return true;
    }

    /**
     * @param screenshotImage
     * @param referenceImage
     * @param errorTolerance
     * @return
     */
    private ScreenShotFailureReporter compareToReference(
            BufferedImage screenshotImage, BufferedImage referenceImage,
            double errorTolerance) {

        // If images are of different size crop both images to same size
        // before checking for differences
        boolean sizesDiffer = !ImageUtil.imagesSameSize(referenceImage,
                screenshotImage);
        if (sizesDiffer) {
            List<BufferedImage> images = ImageUtil.cropToBeSameSize(
                    referenceImage, screenshotImage);
            referenceImage = images.get(0);
            screenshotImage = images.get(1);
        }

        int imageWidth = referenceImage.getWidth();
        int imageHeight = referenceImage.getHeight();

        int xBlocks = ImageComparisonUtil.getNrBlocks(imageWidth);
        int yBlocks = ImageComparisonUtil.getNrBlocks(imageHeight);
        boolean[][] falseBlocks = new boolean[xBlocks][yBlocks];
        boolean imagesEqual = compareImage(falseBlocks, referenceImage,
                screenshotImage, errorTolerance);

        if (sizesDiffer) {
            /*
             * The command has failed because the dimensions of the captured
             * image do not match the reference image
             */
            if (Parameters.isDebug()) {
                if (imagesEqual) {
                    // The images are equal but of different size
                    System.out.println("Images are of different size.");
                } else {
                    // Neither size nor contents match
                    System.out
                            .println("Images differ and are of different size.");
                }
            }
            // TODO: Add info about which RC it was run on
            return makeFailureReporter(referenceImage, falseBlocks);
        }

        if (imagesEqual) {
            if (Parameters.isDebug()) {
                System.out.println("Screenshot matched reference");
            }
            // Images match. Nothing else to do.
            return null;
        }

        if (Parameters.isScreenshotComparisonCursorDetection()) {
            // Images are not equal, still check if the only difference
            // is a blinking cursor
            Point possibleCursorPosition = getPossibleCursorPosition(xBlocks,
                    yBlocks, falseBlocks);
            if (possibleCursorPosition != null) {
                if (isCursorTheOnlyError(possibleCursorPosition,
                        referenceImage, screenshotImage, errorTolerance)) {
                    if (Parameters.isDebug()) {
                        System.out
                                .println("Screenshot matched reference after removing cursor");
                    }
                    // Cursor is the only difference so we are done.
                    return null;
                } else if (Parameters.isDebug()) {
                    System.out
                            .println("Screenshot did not match reference after removing cursor");
                }
            }
        }

        if (Parameters.isDebug()) {
            System.out.println("Screenshot did not match reference");
        }

        // Make a failure reporter that is used upstream
        return makeFailureReporter(referenceImage, falseBlocks);
    }

    private ScreenShotFailureReporter makeFailureReporter(
            BufferedImage referenceImage, boolean[][] falseBlocks) {
        return new ScreenShotFailureReporter(referenceImage, falseBlocks);
    }

    public boolean compareImages(BufferedImage referenceImage,
                                 BufferedImage screenshotImage, double errorTolerance) {
        int xBlocks = ImageComparisonUtil
                .getNrBlocks(referenceImage.getWidth());
        int yBlocks = ImageComparisonUtil.getNrBlocks(referenceImage
                .getHeight());
        boolean[][] falseBlocks = new boolean[xBlocks][yBlocks];

        boolean imagesEqual = compareImage(falseBlocks, referenceImage,
                screenshotImage, errorTolerance);

        // Check for cursor.
        if (!imagesEqual && Parameters.isScreenshotComparisonCursorDetection()) {
            Point possibleCursorPosition = getPossibleCursorPosition(xBlocks,
                    yBlocks, falseBlocks);
            if (possibleCursorPosition != null) {
                if (isCursorTheOnlyError(possibleCursorPosition,
                        referenceImage, screenshotImage, errorTolerance)) {
                    return true;
                }
            }
        }
        return imagesEqual;
    }

    private boolean compareImage(boolean[][] falseBlocks,
                                 BufferedImage referenceImage, BufferedImage screenshotImage,
                                 double errorTolerance) {
        boolean result = true;

        int imageWidth = referenceImage.getWidth();
        int imageHeight = referenceImage.getHeight();

        int xBlocks = ImageComparisonUtil.getNrBlocks(imageWidth);
        int yBlocks = ImageComparisonUtil.getNrBlocks(imageHeight);

        // iterate picture in macroblocks of 16x16 (x,y) (0-> m-16, 0->
        // n-16)
        for (int y = 0; y < imageHeight - 15; y += 16) {
            for (int x = 0; x < imageWidth - 15; x += 16) {
                if (blocksDiffer(x, y, referenceImage, screenshotImage,
                        errorTolerance)) {
                    if (falseBlocks != null) {
                        falseBlocks[x / 16][y / 16] = true;
                    }
                    result = false;
                }
            }
        }

        // Check image bottom
        if (imageHeight % 16 != 0) {
            for (int x = 0; x < imageWidth - 15; x += 16) {
                if (blocksDiffer(x, imageHeight - 16, referenceImage,
                        screenshotImage, errorTolerance)) {
                    if (falseBlocks != null) {
                        falseBlocks[x / 16][yBlocks - 1] = true;
                    }
                    result = false;
                }
            }
        }

        // Check right side of image
        if (imageWidth % 16 != 0) {
            for (int y = 0; y < imageHeight - 15; y += 16) {
                if (blocksDiffer(imageWidth - 16, y, referenceImage,
                        screenshotImage, errorTolerance)) {
                    if (falseBlocks != null) {
                        falseBlocks[xBlocks - 1][y / 16] = true;
                    }
                    result = false;
                }
            }
        }

        // Check lower right corner if necessary
        if (imageWidth % 16 != 0 && imageHeight % 16 != 0) {
            if (blocksDiffer(imageWidth - 16, imageHeight - 16, referenceImage,
                    screenshotImage, errorTolerance)) {
                if (falseBlocks != null) {
                    falseBlocks[xBlocks - 1][yBlocks - 1] = true;
                }
                result = false;
            }
        }

        return result;
    }

    private boolean blocksDiffer(int x, int y, BufferedImage referenceImage,
                                 BufferedImage screenshotImage, double errorTolerance) {
        boolean result = false;

        // Get 16x16 blocks from picture
        int[] referenceBlock = ImageUtil.getBlock(referenceImage, x, y);
        int[] screenshotBlock = ImageUtil.getBlock(screenshotImage, x, y);

        // If arrays aren't equal then
        if (!Arrays.equals(referenceBlock, screenshotBlock)) {

            double sums = rgbCompare(referenceBlock, screenshotBlock);

            // Check if total RGB error in a macroblock exceeds
            // allowed error % if true mark block with a rectangle,
            // append block info to imageErrors
            if (sums > errorTolerance) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Calculates the difference between pixels in the block.
     *
     * @param referenceBlock
     * @param screenshotBlock
     * @return Difference %
     */
    private double rgbCompare(int[] referenceBlock, int[] screenshotBlock) {
        int sum = 0;

        // build sums from all available colors Red, Green
        // and Blue
        for (int i = 0; i < referenceBlock.length; i++) {
            Color targetPixel = new Color(referenceBlock[i], /* hasAlpha */true);
            Color testPixel = new Color(screenshotBlock[i]);
            int redDiff = Math.abs(targetPixel.getRed() - testPixel.getRed());
            int greenDiff = Math.abs(targetPixel.getGreen()
                    - testPixel.getGreen());
            int blueDiff = Math
                    .abs(targetPixel.getBlue() - testPixel.getBlue());

            // Only completely opaque pixels are considered. Pixels with alpha
            // values below 255 (== fully opaque) are considered masked and
            // differences in these pixels won't be reported as differences.
            if (targetPixel.getAlpha() >= 255) {
                sum += redDiff;
                sum += greenDiff;
                sum += blueDiff;
            }
        }
        double fullSum = referenceBlock.length * 255 * 3;
        return (sum / fullSum);
    }

    /**
     * Determine if an error is possibly caused by a blinking cursor and, in
     * that case, at what position the cursor might be. Uses only information
     * about the blocks that have failed to determine if the failure _possibly
     * can_ be caused by a cursor that is either missing from the reference or
     * the screenshot.
     *
     * @param xBlocks          Number of blocks in x direction
     * @param yBlocks
     * @param blocksWithErrors Matrix with true marked for blocks where errors have been
     *                         detected
     * @return A Point referring to the x and y coordinates in the image where
     *         the cursor might be (actually might be inside a 16x32 block
     *         starting from that point)
     */
    private static Point getPossibleCursorPosition(int xBlocks, int yBlocks,
                                                   boolean[][] blocksWithErrors) {

        Point firstErrorBlock = null;

        /*
         * Look for 1-2 blocks with errors. Iff the blocks are vertically
         * adjacent to each other we might have a cursor problem. This is the
         * only case we are looking for.
         */

        for (int y = 0; y < yBlocks; y++) {
            for (int x = 0; x < xBlocks; x++) {
                if (blocksWithErrors[x][y]) {
                    if (firstErrorBlock == null) {
                        // This is the first erroneous block we have found
                        firstErrorBlock = new Point(x, y);
                    } else {
                        // This is the second erroneous block we have found
                        if (x == firstErrorBlock.x
                                && y == firstErrorBlock.y + 1) {
                            // This is directly below the first so it is OK
                        } else {
                            // This error is not below the first so there are
                            // other errors than 1-2 blocks above each other (we
                            // are moving from top down).
                            return null;
                        }
                    }
                }
            }
        }

        if (firstErrorBlock != null) {
            // Return value is the pixel coordinates for the first block
            return new Point(firstErrorBlock.x * 16, firstErrorBlock.y * 16);
        }

        return null;
    }

    /**
     * Check if failure is because of a blinking text cursor.
     *
     * @param possibleCursorPosition The position in the image where a cursor possibly can be found
     *                               (pixel coordinates of the top left corner of a block)
     * @param referenceImage         The reference image (with or without a cursor)
     * @param screenshotImage        The captured image (with or without a cursor)
     * @param errorTolerance         Allowed RGB error (value range 0-1)
     * @return true If cursor (vertical line of at least 5 pixels if not at the
     *         top or bottom) is the only difference between the images.
     */
    private boolean isCursorTheOnlyError(Point possibleCursorPosition,
                                         BufferedImage referenceImage, BufferedImage screenshotImage,
                                         double errorTolerance) {
        int x = possibleCursorPosition.x;
        int y = possibleCursorPosition.y;

        int width = referenceImage.getWidth();
        int height = referenceImage.getHeight();

        // find first different pixel in the block of possibleCursorPosition
        int cursorX = -1;
        int cursorStartY = -1;
        findCursor:
        for (int j = y; j < y + 16 && j < height; j++) {
            for (int i = x; i < x + 16 && i < width; i++) {
                // if found differing pixel
                if (isCursorPixel(referenceImage, screenshotImage, i, j)) {
                    // workaround to ignore vertical lines in certain tests
                    if (j < height - 1
                            && !isCursorPixel(referenceImage, screenshotImage,
                            i, j + 1)) {
                        continue;
                    }

                    cursorX = i;
                    cursorStartY = j;
                    break findCursor;
                }
            }
        }
        if (-1 == cursorX) {
            // no difference found with cursor detection threshold
            return false;
        }

        // find the end of the cursor
        int cursorEndY = cursorStartY;
        while (cursorEndY < height - 1
                && cursorEndY < y + 32
                && isCursorPixel(referenceImage, screenshotImage, cursorX,
                cursorEndY + 1)) {
            cursorEndY++;
        }

        // only accept as cursor if at least 5 pixels or at top or bottom of
        // image
        if (cursorEndY - cursorStartY < 5 && cursorStartY > 0
                && cursorEndY < height - 1) {
            return false;
        }

        // Copy pixels from reference over the possible cursor, then recompare
        // blocks. Pixels at cursor position are always copied from the
        // reference image regardless of which of the images has the cursor.

        // Get 16x32 sub-images, aligned on the 16x16 grid
        // this might compare some extra blocks when at the right or bottom edge
        int areaX = x;
        int areaY = y;
        int xSize = 16;
        int ySize = 32;
        // if close to the right edge, move to a 16px aligned block edge which
        // allows at least 16px wide blocks, then widen area to the edge
        if (areaX > width - 16) {
            areaX = (width - 16) & 0xFFFFF0;
            xSize = width - areaX;
        }
        // likewise for bottom edge (at least 32px high, aligned at 16px block
        // edge)
        if (areaY > height - 32) {
            areaY = (height - 32) & 0xFFFFF0;
            ySize = height - areaY;
        }
        BufferedImage referenceCopy = referenceImage.getSubimage(areaX, areaY,
                xSize, ySize);
        BufferedImage screenshotCopy = screenshotImage.getSubimage(areaX,
                areaY, xSize, ySize);
        // avoid modifying original image
        screenshotCopy = ImageUtil.cloneImage(screenshotCopy);

        // copy pixels for cursor position from reference to screenshot
        for (int j = cursorStartY - areaY; j <= cursorEndY - areaY; ++j) {
            int referenceRgb = referenceCopy.getRGB(cursorX - areaX, j);
            screenshotCopy.setRGB(cursorX - areaX, j, referenceRgb);
        }

        // compare one or two blocks of reference with modified screenshot
        boolean imagesEqual = compareImage(null, referenceCopy, screenshotCopy,
                errorTolerance);

        return imagesEqual;
    }

    /**
     * Luminance based comparison of a pixel in two images for cursor detection.
     *
     * @param image1
     * @param image2
     * @param x
     * @param y
     * @return
     */
    private boolean isCursorPixel(BufferedImage image1, BufferedImage image2,
                                  int x, int y) {
        double lum1 = ImageUtil.getLuminance(image1.getRGB(x, y));
        double lum2 = ImageUtil.getLuminance(image2.getRGB(x, y));

        // cursor must be dark and the other pixel bright enough for contrast
        return (lum1 < 50 && lum2 > 150) || (lum1 > 150 && lum2 < 50);
    }

}
