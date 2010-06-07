package com.vaadin.testbench.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.Assert;

/**
 * Class with features for comparing 2 images.
 */
public class ImageComparison {

    private static final String TEST_SCREENS_DIRECTORY = "com.vaadin.testbench.screenshot.directory";
    private static final String CURSOR_DETECT = "com.vaadin.testbench.screenshot.cursor";
    private static final String BLOCK_ERROR = "com.vaadin.testbench.screenshot.block.error";
    private static final String DEBUG = "com.vaadin.testbench.debug";
    // referenceDirectory is the name of the directory with the reference
    // pictures of the same name as the one to be compared
    private static final String REFERENCE_DIRECTORY = "reference";
    private static final String ERROR_DIRECTORY = "errors";
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * Compare image [name] to image under /reference/. Images may differ in RGB
     * hues 0.1% (default) per macroblock of 16x16
     * 
     * @param image
     *            Image as BASE64 encoded String
     * @param d
     *            Allowed RGB error for a macroblock (value range 0-1 default
     *            0.025 == 2.5%)
     * @param fileId
     *            File name for this image
     * @param dimensions
     *            Browser window dimensions
     * @return true if images are the same
     */
    public boolean compareStringImage(String image, String fileId, double d,
            BrowserDimensions dimensions, boolean testEdges) {
        if (System.getProperty(BLOCK_ERROR) != null) {
            d = Double.parseDouble(System.getProperty(BLOCK_ERROR));
        }
        // Check that d value inside allowed range. if false set d to default
        // value.
        if (d < 0 || d > 1) {
            d = 0.025;
        }

        boolean result = false;

        String directory = System.getProperty(TEST_SCREENS_DIRECTORY);

        // Write error blocks to file && syserr only if debug is defined as true
        boolean debug = false;
        if ("true".equalsIgnoreCase(System.getProperty(DEBUG))) {
            debug = true;
        }

        if (directory == null || directory.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing reference directory definition. Use -D"
                            + TEST_SCREENS_DIRECTORY + "=c:\\screenshot\\. ");
        }

        if (!File.separator.equals(directory.charAt(directory.length() - 1))) {
            directory = directory + File.separator;
        }

        checkAndCreateDirectories(directory);

        // collect errors that are then written to a .log file
        StringBuilder imageErrors = new StringBuilder();

        BufferedImage test = (ImageUtil.stringToImage(image)).getSubimage(
                dimensions.getCanvasXPosition(), dimensions
                        .getCanvasYPosition(), dimensions.getCanvasWidth(),
                dimensions.getCanvasHeight());
        BufferedImage cleanTest = test;
        try {
            // Load images if reference not given
            BufferedImage target = ImageIO.read(new File(directory
                    + REFERENCE_DIRECTORY + File.separator + fileId + ".png"));
            if (testEdges) {
                target = ImageUtil.detectEdges(target);
                test = ImageUtil.detectEdges(test);
                BufferedImage referenceImage = new BufferedImage(dimensions
                        .getCanvasWidth(), dimensions.getCanvasHeight(),
                        BufferedImage.TYPE_INT_RGB);

                Graphics2D g = (Graphics2D) referenceImage.getGraphics();
                g.drawImage(test, 0, 0, dimensions.getCanvasWidth(), dimensions
                        .getCanvasHeight(), null);
                g.dispose();
                test = referenceImage;
            }

            // if images are of different size crop both images to same size
            // before checking for differences
            boolean sizesDiffer = false;
            if (target.getHeight() != test.getHeight()
                    || target.getWidth() != test.getWidth()) {
                sizesDiffer = true;
                // smallest height and width of images
                int minHeight, minWidth;
                if (target.getHeight() > test.getHeight()) {
                    minHeight = test.getHeight();
                    System.err
                            .println("Screenshot height less than reference image.");
                } else {
                    minHeight = target.getHeight();
                    if (target.getHeight() != test.getHeight()) {
                        System.err
                                .println("Reference image height less than screenshot.");
                    }
                }
                if (target.getWidth() > test.getWidth()) {
                    minWidth = test.getWidth();
                    System.err
                            .println("Screenshot width less than reference image.");
                } else {
                    minWidth = target.getWidth();
                    if (target.getWidth() != test.getWidth()) {
                        System.err
                                .println("Reference image width less than screenshot.");
                    }
                }

                // Crop both images to same size
                target = target.getSubimage(0, 0, minWidth, minHeight);
                test = test.getSubimage(0, 0, minWidth, minHeight);
            }

            // Flag result as true until proven false
            result = true;

            int xBlocks = (int) Math.floor(target.getWidth() / 16) + 1;
            int yBlocks = (int) Math.floor(target.getHeight() / 16) + 1;
            boolean[][] falseBlocks = new boolean[xBlocks][yBlocks];

            // iterate picture in macroblocks of 16x16 (x,y) (0-> m-16, 0->
            // n-16)
            for (int y = 0; y < target.getHeight() - 16; y += 16) {
                for (int x = 0; x < target.getWidth() - 16; x += 16) {
                    int[] targetBlock = new int[16 * 16], testBlock = new int[16 * 16];

                    // Get 16x16 blocks from picture
                    targetBlock = target.getRGB(x, y, 16, 16, targetBlock, 0,
                            16);
                    testBlock = test.getRGB(x, y, 16, 16, testBlock, 0, 16);

                    // If arrays aren't equal then
                    if (!Arrays.equals(targetBlock, testBlock)) {

                        double sums = rgbCompare(targetBlock, testBlock);

                        // Check if total RGB error in a macroblock exceeds
                        // allowed error % if true mark block with a rectangle,
                        // append block info to imageErrors
                        if (sums > d) {
                            imageErrors
                                    .append("Error in block at position:\tx="
                                            + x + " y=" + y + NEW_LINE);
                            imageErrors.append("RGB error for block:\t\t"
                                    + roundTwoDecimals(sums * 100) + "%"
                                    + NEW_LINE + NEW_LINE);
                            falseBlocks[x / 16][y / 16] = true;

                            result = false;
                        }
                    }
                    targetBlock = testBlock = null;
                }
            }

            if (target.getWidth() % 16 != 0) {
                // check the bottom of image
                for (int x = 0; x < target.getWidth() - 16; x += 16) {
                    int[] targetBlock = new int[16 * 16], testBlock = new int[16 * 16];

                    // Get 16x16 blocks from picture
                    targetBlock = target.getRGB(x, target.getHeight() - 16, 16,
                            16, targetBlock, 0, 16);
                    testBlock = test.getRGB(x, target.getHeight() - 16, 16, 16,
                            testBlock, 0, 16);

                    // If arrays aren't equal then
                    if (!Arrays.equals(targetBlock, testBlock)) {

                        double sums = rgbCompare(targetBlock, testBlock);

                        // Check if total RGB error in a macroblock exceeds
                        // allowed error % if true mark block with a rectangle,
                        // append block info to imageErrors
                        if (sums > d) {
                            imageErrors
                                    .append("Error in block at position:\tx="
                                            + x + " y="
                                            + (target.getHeight() - 16)
                                            + NEW_LINE);
                            imageErrors.append("RGB error for block:\t\t"
                                    + roundTwoDecimals(sums * 100) + "%"
                                    + NEW_LINE + NEW_LINE);
                            falseBlocks[x / 16][yBlocks - 1] = true;

                            result = false;
                        }
                    }
                    targetBlock = testBlock = null;
                }
            }

            if (target.getHeight() % 16 != 0) {
                // checkthe right side of the image
                for (int y = 0; y < target.getHeight() - 16; y += 16) {
                    int[] targetBlock = new int[16 * 16], testBlock = new int[16 * 16];

                    // Get 16x16 blocks from picture
                    targetBlock = target.getRGB(target.getWidth() - 16, y, 16,
                            16, targetBlock, 0, 16);
                    testBlock = test.getRGB(target.getWidth() - 16, y, 16, 16,
                            testBlock, 0, 16);

                    // If arrays aren't equal then
                    if (!Arrays.equals(targetBlock, testBlock)) {

                        double sums = rgbCompare(targetBlock, testBlock);

                        // Check if total RGB error in a macroblock exceeds
                        // allowed error % if true mark block with a rectangle,
                        // append block info to imageErrors
                        if (sums > d) {
                            imageErrors
                                    .append("Error in block at position:\tx="
                                            + (target.getWidth() - 16) + " y="
                                            + y + NEW_LINE);
                            imageErrors.append("RGB error for block:\t\t"
                                    + roundTwoDecimals(sums * 100) + "%"
                                    + NEW_LINE + NEW_LINE);
                            falseBlocks[xBlocks - 1][y / 16] = true;

                            result = false;
                        }
                    }
                    targetBlock = testBlock = null;
                }
            }

            if (target.getWidth() % 16 != 0 && target.getHeight() % 16 != 0) {
                // Check bottom right corner.
                int[] targetBlock = new int[16 * 16], testBlock = new int[16 * 16];

                // Get 16x16 blocks from picture
                targetBlock = target.getRGB(target.getWidth() - 16, target
                        .getHeight() - 16, 16, 16, targetBlock, 0, 16);
                testBlock = test.getRGB(target.getWidth() - 16, target
                        .getHeight() - 16, 16, 16, testBlock, 0, 16);

                // If arrays aren't equal then
                if (!Arrays.equals(targetBlock, testBlock)) {

                    double sums = rgbCompare(targetBlock, testBlock);

                    // Check if total RGB error in a macroblock exceeds
                    // allowed error % if true mark block with a rectangle,
                    // append block info to imageErrors
                    if (sums > d) {
                        imageErrors.append("Error in block at position:\tx="
                                + (target.getWidth() - 16) + " y="
                                + (target.getHeight() - 16) + NEW_LINE);
                        imageErrors.append("RGB error for block:\t\t"
                                + roundTwoDecimals(sums * 100) + "%" + NEW_LINE
                                + NEW_LINE);
                        falseBlocks[xBlocks - 1][yBlocks - 1] = true;

                        result = false;
                    }
                }
                targetBlock = testBlock = null;
            }

            // if errors found in file save diff file with marked
            // macroblocks and create html file for visual confirmation of
            // differences
            if (result == false) {
                if ("true".equalsIgnoreCase(System.getProperty(CURSOR_DETECT))
                        && !sizesDiffer) {
                    // check amount of error blocks
                    int errorAmount = 0, x = 0, y = 0, firstBlockX = 0, firstBlockY = 0;
                    for (int j = 0; j < yBlocks; j++) {
                        for (int i = 0; i < xBlocks; i++) {
                            if (falseBlocks[i][j] && errorAmount == 0) {
                                // save first error block position
                                errorAmount++;
                                x = i * 16;
                                y = j * 16;
                                firstBlockX = i;
                                firstBlockY = j;
                            } else if (falseBlocks[i][j] && errorAmount == 1) {
                                if (j == 0) {
                                    // stop checking no cursor detection will be
                                    // done. Same line as the first block.
                                    errorAmount++;
                                    i = xBlocks;
                                    j = yBlocks;
                                } else if (!falseBlocks[i][j - 1]
                                        && i != firstBlockX
                                        && j != (firstBlockY + 1)) {
                                    // stop checking no cursor detection will be
                                    // done
                                    errorAmount++;
                                    i = xBlocks;
                                    j = yBlocks;
                                }
                            }
                        }
                    }
                    if (errorAmount == 1) {
                        boolean cursor = checkForCursor(test, target, x, y,
                                fileId);
                        if (cursor) {
                            return true;
                        }
                    }
                }
                if (!testEdges) {
                    // Check that the comparison folder exists and create if
                    // false
                    File compareFolder = new File(directory + ERROR_DIRECTORY);
                    if (!compareFolder.exists()) {
                        compareFolder.mkdir();
                    }

                    // collect big error blocks of differences
                    List<ErrorBlock> errorAreas = collectErrorsToList(xBlocks,
                            yBlocks, falseBlocks);

                    // get both images again if different size
                    if (sizesDiffer) {
                        test = cleanTest;

                        target = ImageIO.read(new File(directory
                                + REFERENCE_DIRECTORY + File.separator + fileId
                                + ".png"));
                    }

                    // Draw lines around false ErrorBlocks before saving _diff
                    // file.
                    Graphics2D drawToPicture = test.createGraphics();
                    drawToPicture.setColor(Color.MAGENTA);

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
                        if ((error.getX() + (error.getXBlocks() * 16) + offsetX) > test
                                .getWidth()) {
                            toX = test.getWidth() - error.getX();
                        }
                        if ((error.getY() + (error.getYBlocks() * 16) + offsetY) > test
                                .getHeight()) {
                            toY = test.getHeight() - error.getY();
                        }

                        // draw error to image
                        drawToPicture.drawRect(error.getX() - offsetX, error
                                .getY()
                                - offsetY, toX, toY);

                    }
                    // release resources
                    drawToPicture.dispose();

                    // Write clean image to file
                    ImageIO.write(cleanTest, "png", new File(compareFolder
                            + File.separator + fileId + ".png"));

                    createDiffHtml(errorAreas, fileId, ImageUtil
                            .encodeImageToBase64(test), ImageUtil
                            .encodeImageToBase64(target));

                    if (debug) {
                        System.err
                                .println("Created clean image, image with marked differences and difference html.");
                    }
                } else {
                    File compareFolder = new File(directory + ERROR_DIRECTORY);
                    if (!compareFolder.exists()) {
                        compareFolder.mkdir();
                    }
                    ImageIO.write(test, "png", new File(compareFolder
                            + File.separator + fileId + "_edges.png"));
                    ImageIO.write(target, "png", new File(compareFolder
                            + File.separator + fileId + "_target.png"));
                }
                // Throw assert fail here if no debug requested
                if (debug == false && sizesDiffer == false) {
                    Assert.fail("Screenshot (" + fileId
                            + ") differs from reference image.");
                }
            }
            if (sizesDiffer) {
                // Throws an assertion error with message depending on result
                // (images only differ in size or images differ in size and
                // contain errors)
                if (result) {
                    // Check that the comparison folder exists and create if
                    // false
                    File compareFolder = new File(directory + ERROR_DIRECTORY);
                    if (!compareFolder.exists()) {
                        compareFolder.mkdir();
                    }

                    test = (ImageUtil.stringToImage(image)).getSubimage(
                            dimensions.getCanvasXPosition(), dimensions
                                    .getCanvasYPosition(), dimensions
                                    .getCanvasWidth(), dimensions
                                    .getCanvasHeight());

                    // Write clean image to file
                    ImageIO.write(test, "png", new File(compareFolder
                            + File.separator + fileId + ".png"));

                    // collect big error blocks of differences
                    List<ErrorBlock> errorAreas = new LinkedList<ErrorBlock>();

                    target = ImageIO.read(new File(directory
                            + REFERENCE_DIRECTORY + File.separator + fileId
                            + ".png"));

                    createDiffHtml(errorAreas, fileId, ImageUtil
                            .encodeImageToBase64(test), ImageUtil
                            .encodeImageToBase64(target));

                    Assert.fail("Images are of different size (" + fileId
                            + ").");
                } else {
                    Assert.fail("Images differ and are of different size ("
                            + fileId + ").");
                }
            }
        } catch (IOException e) {
            // Create an RGB image without alpha channel for reference
            BufferedImage referenceImage = new BufferedImage(dimensions
                    .getCanvasWidth(), dimensions.getCanvasHeight(),
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D g = (Graphics2D) referenceImage.getGraphics();
            g.drawImage(test, 0, 0, dimensions.getCanvasWidth(), dimensions
                    .getCanvasHeight(), null);
            g.dispose();

            try {
                File referenceFile = new File(directory + ERROR_DIRECTORY
                        + File.separator + fileId + ".png");
                if (!referenceFile.exists()) {
                    if (debug) {
                        System.err.println("Creating reference to "
                                + ERROR_DIRECTORY + ".");
                    }
                    // Write clean image to error folder.
                    ImageIO.write(referenceImage, "png", referenceFile);
                }
                result = false;
            } catch (FileNotFoundException fnfe) {
                Assert.fail("Failed to open file to write reference image.");
            } catch (IOException ioe) {
                e.printStackTrace();
                return false;
            }
            if (result == false) {
                Assert.fail("No reference found for " + fileId + " in "
                        + directory + REFERENCE_DIRECTORY);
            }
        }

        if (imageErrors.length() > 0 && debug) {
            // Write error macroblocks data to log file
            BufferedWriter out;
            try {
                out = new BufferedWriter(new FileWriter(directory
                        + ERROR_DIRECTORY + File.separator + fileId + ".log"));

                out.write("Exceptions for " + fileId + NEW_LINE + NEW_LINE);
                out.write(imageErrors.toString());
                out.flush();
                out.close();

                Assert.fail("Screenshot (" + fileId
                        + ") differs from reference image.");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Calculates the difference between pixels in the block.
     * 
     * @param targetBlock
     * @param testBlock
     * @return Difference %
     */
    private double rgbCompare(int[] targetBlock, int[] testBlock) {
        int sum = 0;
        double fullSum = 0.0;

        // build sums from all available colors Red, Green
        // and Blue
        for (int i = 0; i < targetBlock.length; i++) {
            Color targetPixel = new Color(targetBlock[i]);
            Color testPixel = new Color(testBlock[i]);
            int targetColor = (targetPixel.getRed() + targetPixel.getGreen() + targetPixel
                    .getBlue());
            int testColor = (testPixel.getRed() + testPixel.getGreen() + testPixel
                    .getBlue());
            fullSum += targetColor;
            if (targetColor > testColor) {
                sum += targetColor - testColor;
            } else if (testColor > targetColor) {
                sum += testColor - targetColor;
            }
        }
        return (sum / fullSum);
    }

    /**
     * Try to check if failure is due to blicking text cursor
     * 
     * @param test
     *            Image for this run
     * @param target
     *            Reference image
     * @param x
     *            x position of error block
     * @param y
     *            y position of error block
     * @param fileId
     *            Image file name
     * @return true if found cursor else false
     */
    private boolean checkForCursor(BufferedImage test, BufferedImage target,
            int x, int y, String fileId) {
        boolean cursor = false;
        // If at the outer edge move in one step.
        if (x == 0) {
            x = 1;
        }
        // If we would step over the edge move start point
        if ((x + 16) >= target.getWidth()) {
            x = target.getWidth() - 17;
        }
        // If at bottom move start point up.
        if ((y + 16) >= target.getHeight()) {
            y = target.getHeight() - 17;
        }
        // 
        for (int j = y; j < y + 16; j++) {
            for (int i = x; i < x + 16; i++) {
                // if found differing pixel
                if (test.getRGB(i, j) != target.getRGB(i, j)) {
                    int z = j;
                    // do while length < 30 && inside picture
                    do {
                        if ((z + 1) >= target.getHeight()) {
                            break;
                        }
                        // if pixels to left and right equal on both pictures
                        if (test.getRGB(i - 1, z) == target.getRGB(i - 1, z)
                                && test.getRGB(i + 1, z) == target.getRGB(
                                        i + 1, z)) {
                            // Continue if next pixel down still differs
                            if ((z + 1) < target.getHeight()
                                    && test.getRGB(i, z + 1) != target.getRGB(
                                            i, z + 1)) {
                                z++;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    } while (z < j + 30);
                    // if found length more than 3 and last pixel equals
                    if ((z - j) >= 5
                            && test.getRGB(i, z + 1) == target.getRGB(i, z + 1)) {
                        System.out.println("Found cursor in test "
                                + fileId.substring(0, fileId.indexOf("_")));
                        cursor = true;
                        return cursor;
                    }
                    // end search if failed to find cursor from one error
                    j = y + 20;
                    i = x + 20;
                }
            }
        }

        return cursor;
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
            String image, String ref_image) {
        try {
            String directory = System.getProperty(TEST_SCREENS_DIRECTORY);
            if (!File.separator
                    .equals(directory.charAt(directory.length() - 1))) {
                directory = directory + File.separator;
            }

            PrintWriter writer = new PrintWriter(new File(directory
                    + ERROR_DIRECTORY + File.separator + fileId + ".html"));
            // Write head
            writer.println("<html>");
            writer.println("<head>");
            writer.println("</head>");
            writer.println("<body>");

            writer
                    .println("<div id=\"diff\" onclick=\"document.getElementById('reference').style.display='block'; this.style.display='none';\" style=\"display: block; position: absolute; top: 0px; left: 0px; \"><img src=\"data:image/png;base64,"
                            + image
                            + "\"/><span style=\"position: absolute; top: 0px; left: 0px; opacity:0.4; filter: alpha(opacity=40); font-weight: bold;\">Image for this run</span></div>");
            writer
                    .println("<div id=\"reference\" onclick=\"this.style.display='none'; document.getElementById('diff').style.display='block';\" style=\"display: none; position: absolute; top: 0px; left: 0px; z-index: 999;\"><img src=\"data:image/png;base64,"
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
                writer
                        .println("<div  onmouseover=\"document.getElementById('"
                                + id
                                + "').style.display='block'\"  style=\"z-index: 66;position: absolute; top: 0px; left: 0px; clip: rect("
                                + (error.getY() - offsetY)
                                + "px,"
                                + (error.getX() + (error.getXBlocks() * 16) + 1)
                                + "px,"
                                + (error.getY() + (error.getYBlocks() * 16) + 1)
                                + "px,"
                                + (error.getX() - offsetX)
                                + "px);\"><img src=\"data:image/png;base64,"
                                + image + "\"/></div>");
                // Start "popup" div
                writer
                        .println("<div class=\"popUpDiv\" onclick=\"document.getElementById('reference').style.display='block'; document.getElementById('diff').style.display='none';\" onmouseout=\"this.style.display='none'\" id=\""
                                + id
                                + "\"  style=\"display: none; position: absolute; top: 0px; left: 0px; clip: rect("
                                + (error.getY() - offsetY)
                                + "px,"
                                + (error.getX() + (error.getXBlocks() * 16) + 1)
                                + "px,"
                                + (error.getY() + (error.getYBlocks() * 16) + 1)
                                + "px,"
                                + (error.getX() - offsetX)
                                + "px); z-index: " + (99 + add) + ";\">");
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

    /**
     * Checks that all required directories can be found and creates them if
     * necessary
     * 
     * @param directory
     */
    private void checkAndCreateDirectories(String directory) {
        // Check directories and create if needed
        File imageDir = new File(directory);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        imageDir = new File(directory + REFERENCE_DIRECTORY);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        imageDir = new File(directory + ERROR_DIRECTORY);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        imageDir = null;
    }

    /**
     * Set double down to 2 decimal places
     * 
     * @param d
     *            double set down
     * @return double with 2 decimal places
     */
    private double roundTwoDecimals(double d) {
        int ix = (int) (d * 100.0); // scale it
        return (ix) / 100.0;
    }

}
