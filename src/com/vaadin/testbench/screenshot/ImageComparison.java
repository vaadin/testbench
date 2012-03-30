package com.vaadin.testbench.screenshot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import com.vaadin.testbench.Parameters;

/**
 * Class with features for comparing 2 images.
 */
public class ImageComparison {

    /**
     * Compare image [name] to image under /reference/. Images may differ in RGB
     * hues 0.1% (default) per macroblock of 16x16
     * 
     * @param screenshotImage
     *            Image of canvas (must have proper dimensions)
     * @param errorTolerance
     *            Allowed RGB error for a macroblock (value range 0-1 default
     *            0.025 == 2.5%)
     * @param referenceFileId
     *            File id for this image without .png extension
     * @param writeScreenshots
     *            true if error images and diff files should be written to disk,
     *            false otherwise
     * @return true if images are the same
     */
    public boolean imageEqualToReference(BufferedImage screenshotImage,
            String referenceFileId, double errorTolerance,
            boolean writeScreenshots) {
        ImageFileUtil.createScreenshotDirectoriesIfNeeded();

        List<String> referenceFileNames = ImageFileUtil
                .getReferenceImageFileNames(referenceFileId + ".png");

        if (referenceFileNames.isEmpty()) {
            // We require a reference image to continue
            Assert.fail("No reference found for " + referenceFileId + " in "
                    + ImageFileUtil.getScreenshotReferenceDirectory());
        }

        // these are used to make the final error HTML page based on main
        // reference file only
        BufferedImage primaryReferenceImage = null;
        boolean[][] primaryReferenceFalseBlocks = null;

        for (String referenceFileName : referenceFileNames) {
            BufferedImage referenceImage;
            try {
                referenceImage = ImageFileUtil
                        .readReferenceImage(referenceFileName);
            } catch (IOException e) {
                Assert.fail("Could not read reference image file "
                        + referenceFileName);

                // needed to keep the compiler happy
                return false;
            }

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
                if (imagesEqual) {
                    /*
                     * The images are equal but of different size
                     */
                    if (writeScreenshots) {
                        createErrorImageAndHTML(referenceFileId,
                                screenshotImage, referenceImage, falseBlocks,
                                xBlocks, yBlocks);
                    }

                    // TODO: Add info about which RC it was run on
                    Assert.fail("Images are of different size ("
                            + referenceFileName + ").");
                } else {
                    // Neither size nor contents match

                    // FIXME: Why is nothing written here?

                    // TODO: Add info about which RC it was run on
                    Assert.fail("Images differ and are of different size ("
                            + referenceFileName + ").");
                }
            }

            if (imagesEqual) {
                if (Parameters.isDebug()) {
                    System.out.println("Screenshot matched reference "
                            + referenceFileName);
                }
                // Images match. Nothing else to do.
                return true;
            }

            if (Parameters.isScreenshotComparisonCursorDetection()) {
                // Images are not equal, still check if the only difference
                // is a blinking cursor
                Point possibleCursorPosition = getPossibleCursorPosition(
                        xBlocks, yBlocks, falseBlocks);
                if (possibleCursorPosition != null) {
                    if (isCursorTheOnlyError(possibleCursorPosition,
                            referenceImage, screenshotImage, errorTolerance)) {
                        if (Parameters.isDebug()) {
                            System.out.println("Screenshot matched reference "
                                    + referenceFileName
                                    + " after removing cursor");
                        }
                        // Cursor is the only difference so we are done.
                        return true;
                    } else if (Parameters.isDebug()) {
                        System.out
                                .println("Screenshot did not match reference "
                                        + referenceFileName
                                        + " after removing cursor");
                    }
                }
            }

            if (Parameters.isDebug()) {
                System.out.println("Screenshot did not match reference "
                        + referenceFileName);
            }

            // store information about the primary reference image for creating
            // the error image if no match is found
            if (null == primaryReferenceImage) {
                primaryReferenceImage = referenceImage;
                primaryReferenceFalseBlocks = falseBlocks;
            }

        }

        /*
         * The command has failed because the captured image differs from the
         * reference image
         */
        if (writeScreenshots) {
            int xBlocks = ImageComparisonUtil.getNrBlocks(primaryReferenceImage
                    .getWidth());
            int yBlocks = ImageComparisonUtil.getNrBlocks(primaryReferenceImage
                    .getHeight());
            createErrorImageAndHTML(referenceFileId, screenshotImage,
                    primaryReferenceImage, primaryReferenceFalseBlocks,
                    xBlocks, yBlocks);
        }

        // TODO: Add info about which RC it was run on
        Assert.fail("Screenshot (" + referenceFileId
                + ") differs from reference image.");

        // Should never get here
        return false;
    }

    private void createErrorImageAndHTML(String fileId,
            BufferedImage screenshotImage, BufferedImage referenceImage,
            boolean[][] falseBlocks, int xBlocks, int yBlocks) {

        try {
            // Write the screenshot into the error directory
            ImageIO.write(screenshotImage, "png",
                    ImageFileUtil.getErrorScreenshotFile(fileId + ".png"));
        } catch (IOException e) {
            System.err.println("Error writing screenshot to "
                    + ImageFileUtil.getErrorScreenshotFile(fileId + ".png")
                            .getPath());
            e.printStackTrace();
        }

        // collect big error blocks of differences
        List<ErrorBlock> errorAreas = collectErrorsToList(xBlocks, yBlocks,
                falseBlocks);

        // Draw boxes around blocks that differ
        drawErrorsToImage(errorAreas, screenshotImage);

        createDiffHtml(errorAreas, fileId, screenshotImage, referenceImage);

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
            Color targetPixel = new Color(referenceBlock[i]);
            Color testPixel = new Color(screenshotBlock[i]);
            int redDiff = Math.abs(targetPixel.getRed() - testPixel.getRed());
            int greenDiff = Math.abs(targetPixel.getGreen()
                    - testPixel.getGreen());
            int blueDiff = Math
                    .abs(targetPixel.getBlue() - testPixel.getBlue());

            sum += redDiff;
            sum += greenDiff;
            sum += blueDiff;

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
     * @param xBlocks
     *            Number of blocks in x direction
     * @param yBlocks
     * @param blocksWithErrors
     *            Matrix with true marked for blocks where errors have been
     *            detected
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
     * @param possibleCursorPosition
     *            The position in the image where a cursor possibly can be found
     *            (pixel coordinates of the top left corner of a block)
     * @param referenceImage
     *            The reference image (with or without a cursor)
     * @param screenshotImage
     *            The captured image (with or without a cursor)
     * @param errorTolerance
     *            Allowed RGB error (value range 0-1)
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
        findCursor: for (int j = y; j < y + 16 && j < height; j++) {
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

    /**
     * Runs through the marked false macroblocks and collects them to bigger
     * blocks
     * 
     * @param xBlocks
     *            Amount of macroblocks in x direction
     * @param yBlocks
     *            Amount of macroblocks in y direction
     * @param falseBlocks
     *            Map of false blocks
     * @return List of ErrorBlocks
     */
    private List<ErrorBlock> collectErrorsToList(int xBlocks, int yBlocks,
            boolean[][] falseBlocks) {
        List<ErrorBlock> errorAreas = new LinkedList<ErrorBlock>();

        // run through blocks for marked errors for macroblocks.
        for (int y = 0; y < yBlocks; y++) {
            for (int x = 0; x < xBlocks; x++) {
                // if found error make new ErrorBlock and collect
                // connected error blocks and mark them false so
                // that they won't trigger new errors
                if (falseBlocks[x][y]) {
                    ErrorBlock newBlock = new ErrorBlock();
                    newBlock.setX(x * 16);
                    newBlock.setY(y * 16);
                    int x1 = x, xmin = x, y1 = y, maxSteps = xBlocks * yBlocks, steps = 0;
                    falseBlocks[x][y] = false;

                    // This'll confirm logic errors.
                    while (true) {
                        x1++;

                        // if x1 out of bounds set x1 to xmin where
                        // xmin == smallest error block found for
                        // this error
                        if (x1 >= xBlocks) {
                            x1 = xmin;
                        }

                        // if x1,y1 marked true add width to ErrorBlock
                        if (falseBlocks[x1][y1]) {
                            newBlock.addXBlock();
                            falseBlocks[x1][y1] = false;
                        } else if (y1 < yBlocks) {
                            x1 = xmin;

                            // If next row has a false block
                            // connected to our block
                            boolean foundConnectedBlock = false;
                            for (int foundX = x1; foundX < x1
                                    + newBlock.getXBlocks(); foundX++) {
                                if (foundX == xBlocks || y1 + 1 == yBlocks) {
                                    break;
                                }

                                if (falseBlocks[foundX][y1 + 1]) {
                                    foundConnectedBlock = true;
                                }
                            }

                            // If connected error to ErrorBlock add
                            // height to error block
                            if (foundConnectedBlock) {
                                y1++;
                                newBlock.addYBlock();

                                // while stepping back on this
                                // row is false change block x
                                // position
                                if (x1 - 1 >= 0) {
                                    while (falseBlocks[x1 - 1][y1]) {
                                        falseBlocks[x1 - 1][y1] = false;
                                        newBlock.addXBlock();
                                        x1 = x1 - 1;
                                        newBlock.setX(newBlock.getX() - 16);
                                        if (x1 == 0) {
                                            break;
                                        }
                                    }
                                    xmin = x1;
                                }

                                // Skip blocks inside main error
                                // block for this error
                                x1 = x1 + newBlock.getXBlocks() - 1;
                            } else {
                                x1 = newBlock.getX() / 16;
                                y1 = newBlock.getY() / 16;
                                // Set all blocks to false
                                // inside found box
                                for (int j = 0; j < newBlock.getYBlocks(); j++) {
                                    for (int i = 0; i < newBlock.getXBlocks(); i++) {
                                        if (x1 + i < xBlocks
                                                && y1 + j < yBlocks) {
                                            falseBlocks[x1 + i][y1 + j] = false;
                                        }
                                    }
                                }
                                break;

                            }
                        }
                        // In case something goes wrong we won't get stuck in
                        // the loop forever
                        if (++steps == maxSteps) {
                            break;
                        }
                    }
                    errorAreas.add(newBlock);
                }
            }
        }
        return errorAreas;
    }

    private void drawErrorsToImage(List<ErrorBlock> errorAreas,
            BufferedImage screenshotImage) {
        // Draw lines around false ErrorBlocks before saving _diff
        // file.
        Graphics2D drawToPicture = screenshotImage.createGraphics();
        drawToPicture.setColor(Color.MAGENTA);

        int width = screenshotImage.getWidth();
        int height = screenshotImage.getHeight();

        for (ErrorBlock error : errorAreas) {
            int offsetX = 0, offsetY = 0;
            if (error.getX() > 0) {
                offsetX = 1;
            }
            if (error.getY() > 0) {
                offsetY = 1;
            }
            int toX = error.getXBlocks() * 16 + offsetX;
            int toY = error.getYBlocks() * 16 + offsetY;
            // Draw lines inside canvas
            if ((error.getX() + (error.getXBlocks() * 16) + offsetX) > width) {
                toX = width - error.getX();
            }
            if ((error.getY() + (error.getYBlocks() * 16) + offsetY) > height) {
                toY = height - error.getY();
            }

            // draw error to image
            drawToPicture.drawRect(error.getX() - offsetX, error.getY()
                    - offsetY, toX, toY);

        }
        // release resources
        drawToPicture.dispose();
    }

    /**
     * Build a small html file that has mouse over picture change for fast
     * checking of errors and click on picture to switch between reference and
     * diff pictures.
     * 
     * @param blocks
     *            List of ErrorBlock
     * @param diff
     *            diff file
     * @param reference
     *            reference image file
     * @param fileId
     *            fileName for html file
     */
    private void createDiffHtml(List<ErrorBlock> blocks, String fileId,
            BufferedImage screenshotImage, BufferedImage referenceImage) {
        String image = ImageUtil.encodeImageToBase64(screenshotImage);
        String ref_image = ImageUtil.encodeImageToBase64(referenceImage);
        try {
            String directory = Parameters.getScreenshotDirectory();
            if (!File.separator
                    .equals(directory.charAt(directory.length() - 1))) {
                directory = directory + File.separator;
            }

            PrintWriter writer = new PrintWriter(
                    ImageFileUtil.getErrorScreenshotFile(fileId + ".html"));
            // Write head
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<script type=\"text/javascript\">var difference = true;function switchImage(){"
                    + "if(difference){difference = false;document.getElementById('reference').style.display='block';"
                    + "document.getElementById('diff').style.display='none';}else{difference = true;"
                    + "document.getElementById('reference').style.display='none';document.getElementById('diff').style.display='block';"
                    + "}}</script>");
            writer.println("</head>");
            writer.println("<body onclick=\"switchImage()\">");

            writer.println("<div id=\"diff\" style=\"display: block; position: absolute; top: 0px; left: 0px; \"><img src=\"data:image/png;base64,"
                    + image
                    + "\"/><span style=\"position: absolute; top: 0px; left: 0px; opacity:0.4; filter: alpha(opacity=40); font-weight: bold;\">Image for this run</span></div>");
            writer.println("<div id=\"reference\" style=\"display: none; position: absolute; top: 0px; left: 0px; z-index: 999;\"><img src=\"data:image/png;base64,"
                    + ref_image + "\"/></div>");

            int add = 0;
            for (ErrorBlock error : blocks) {
                int offsetX = 0, offsetY = 0;
                if (error.getX() > 0) {
                    offsetX = 1;
                }
                if (error.getY() > 0) {
                    offsetY = 1;
                }
                String id = "popUpDiv_" + (error.getX() + add) + "_"
                        + (error.getY() + add);
                // position stars so that it's not out of screen.
                writer.println("<div  onmouseover=\"document.getElementById('"
                        + id
                        + "').style.display='block'\"  style=\"z-index: 66;position: absolute; top: 0px; left: 0px; clip: rect("
                        + (error.getY() - offsetY) + "px,"
                        + (error.getX() + (error.getXBlocks() * 16) + 1)
                        + "px,"
                        + (error.getY() + (error.getYBlocks() * 16) + 1)
                        + "px," + (error.getX() - offsetX)
                        + "px);\"><img src=\"data:image/png;base64," + image
                        + "\"/></div>");
                // Start "popup" div
                writer.println("<div class=\"popUpDiv\" onclick=\"document.getElementById('reference').style.display='block'; document.getElementById('diff').style.display='none';\" onmouseout=\"this.style.display='none'\" id=\""
                        + id
                        + "\"  style=\"display: none; position: absolute; top: 0px; left: 0px; clip: rect("
                        + (error.getY() - offsetY)
                        + "px,"
                        + (error.getX() + (error.getXBlocks() * 16) + 1)
                        + "px,"
                        + (error.getY() + (error.getYBlocks() * 16) + 1)
                        + "px,"
                        + (error.getX() - offsetX)
                        + "px); z-index: "
                        + (99 + add) + ";\">");
                writer.println("<img src=\"data:image/png;base64," + ref_image
                        + "\" />");
                // End popup div
                writer.println("</div>");
                add++;
            }

            // End file
            writer.println("</body></html>");
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
