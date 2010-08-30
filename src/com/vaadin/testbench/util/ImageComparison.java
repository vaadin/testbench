package com.vaadin.testbench.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.vaadin.testbench.Parameters;

/**
 * Class with features for comparing 2 images.
 */
public class ImageComparison {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private ImageData imageData = null;

    public static void main(String[] args) throws FileNotFoundException,
            IOException {
        System.setProperty(Parameters.SCREENSHOT_DIRECTORY, "C:/temp");
        ImageComparison ic = new ImageComparison();
        BrowserDimensions dimensions = new BrowserDimensions(1, 1, 1016, 618,
                0, 0);
        String base64Reference = new Base64().encodeToString(IOUtils
                .toByteArray(new FileInputStream("c:/temp/cursor-on.png")));
        ic.compareStringImage(base64Reference, "cursor-off", 0.1, dimensions,
                false);
        System.out.println("OK");
    }

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

        imageData = new ImageData(image, fileId, dimensions, d, testEdges);

        imageData.debug("Using block error tolerance: " + d);

        boolean result = false;

        imageData.generateBaseDirectory();

        checkAndCreateDirectories(imageData.getBaseDirectory());

        try {
            imageData.generateComparisonImage();
            imageData.generateReferenceImage();

            if (testEdges) {
                imageData.getEdges();
            }

            // if images are of different size crop both images to same size
            // before checking for differences
            boolean sizesDiffer = imageData.checkIfCanvasSizesDiffer();

            int imageWidth = imageData.getReferenceImage().getWidth();
            int imageHeight = imageData.getReferenceImage().getHeight();

            int xBlocks = (int) Math.floor(imageWidth / 16) + 1;
            int yBlocks = (int) Math.floor(imageHeight / 16) + 1;
            boolean[][] falseBlocks = new boolean[xBlocks][yBlocks];

            result = compareImage(falseBlocks);

            // if errors found in file save diff file with marked
            // macroblocks and create html file for visual confirmation of
            // differences
            if (result == false) {

                // Check for cursor.
                if (Parameters.isScreenshotComparisonCursorDetection()
                        && !sizesDiffer) {
                    if (isCursorCheckNeeded(xBlocks, yBlocks, falseBlocks)) {
                        boolean cursor = checkForCursor();
                        if (cursor) {
                            return true;
                        }
                    }
                }

                if (!testEdges) {
                    // Check that the comparison folder exists and create if
                    // false
                    File compareFolder = new File(imageData.getErrorDirectory());
                    if (!compareFolder.exists()) {
                        compareFolder.mkdir();
                    }

                    // collect big error blocks of differences
                    List<ErrorBlock> errorAreas = collectErrorsToList(xBlocks,
                            yBlocks, falseBlocks);

                    // get both images again if different size
                    if (sizesDiffer) {
                        imageData.generateComparisonImage();
                        imageData.generateReferenceImage();
                    }

                    // Write clean image to file
                    ImageIO.write(imageData.getComparisonImage(), "png",
                            new File(compareFolder + File.separator
                                    + imageData.getFileName()));

                    drawErrorsToImage(errorAreas);

                    createDiffHtml(errorAreas, fileId,
                            ImageUtil.encodeImageToBase64(imageData
                                    .getComparisonImage()), ImageUtil
                                    .encodeImageToBase64(imageData
                                            .getReferenceImage()));

                    imageData
                            .debug("Created clean image, image with marked differences and difference html.");
                } else {
                    File compareFolder = new File(imageData.getErrorDirectory());
                    if (!compareFolder.exists()) {
                        compareFolder.mkdir();
                    }
                    ImageIO.write(imageData.getComparisonImage(), "png",
                            new File(compareFolder + fileId + "_edges.png"));
                    ImageIO.write(imageData.getReferenceImage(), "png",
                            new File(compareFolder + fileId + "_target.png"));
                }
                // Throw assert fail here if no debug requested
                if (sizesDiffer == false) {
                    imageData.debug("Screenshot (" + fileId
                            + ") differs from reference image.");
                    debug();
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
                    File compareFolder = new File(imageData.getErrorDirectory());
                    if (!compareFolder.exists()) {
                        compareFolder.mkdir();
                    }

                    imageData.generateComparisonImage();

                    // Write clean image to file
                    ImageIO.write(imageData.getComparisonImage(), "png",
                            new File(compareFolder + File.separator + fileId
                                    + ".png"));

                    // collect big error blocks of differences
                    List<ErrorBlock> errorAreas = collectErrorsToList(xBlocks,
                            yBlocks, falseBlocks);

                    drawErrorsToImage(errorAreas);

                    imageData.generateReferenceImage();

                    createDiffHtml(errorAreas, fileId,
                            ImageUtil.encodeImageToBase64(imageData
                                    .getComparisonImage()), ImageUtil
                                    .encodeImageToBase64(imageData
                                            .getReferenceImage()));

                    imageData.debug("Images are of different size");
                    debug();
                    Assert.fail("Images are of different size (" + fileId
                            + ").");
                } else {
                    imageData.debug("Images differ and are of different size");
                    debug();
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
            g.drawImage(imageData.getComparisonImage(), 0, 0, dimensions
                    .getCanvasWidth(), dimensions.getCanvasHeight(), null);
            g.dispose();

            try {
                File referenceFile = new File(imageData.getErrorDirectory()
                        + fileId + ".png");
                if (!referenceFile.exists()) {
                    imageData.debug("Creating reference to "
                            + ImageData.ERROR_DIRECTORY + ".");
                    // Write clean image to error folder.
                    ImageIO.write(referenceImage, "png", referenceFile);
                }
                result = false;
            } catch (FileNotFoundException fnfe) {
                imageData
                        .debug("Failed to open file to write reference image.");
                debug();
                Assert.fail("Failed to open file to write reference image.");
            } catch (IOException ioe) {
                e.printStackTrace();
                return false;
            }
            if (result == false) {
                imageData.debug("No reference found for " + fileId);
                debug();
                Assert.fail("No reference found for " + fileId + " in "
                        + imageData.getReferenceDirectory());
            }
        }

        return result;
    }

    public boolean compareImages(ImageData imageData) {
        this.imageData = imageData;
        checkAndCreateDirectories(imageData.getBaseDirectory());

        return compareImage(null);
    }

    private void debug() {
        if (Parameters.isDebug() && imageData.getImageErrors().length() >= 0) {
            String fileId = imageData.getFileName().substring(0,
                    imageData.getFileName().lastIndexOf('.'));
            // Write error macroblocks data to log file
            BufferedWriter out;
            try {
                out = new BufferedWriter(new FileWriter(imageData
                        .getErrorDirectory()
                        + fileId + ".log"));

                out.write("Exceptions for " + fileId + NEW_LINE + NEW_LINE);
                out.write(imageData.getImageErrors().toString());
                out.flush();
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private boolean compareImage(boolean[][] falseBlocks) {
        boolean result = true;

        int imageWidth = imageData.getReferenceImage().getWidth();
        int imageHeight = imageData.getReferenceImage().getHeight();

        int xBlocks = (int) Math.floor(imageWidth / 16) + 1;
        int yBlocks = (int) Math.floor(imageHeight / 16) + 1;

        // iterate picture in macroblocks of 16x16 (x,y) (0-> m-16, 0->
        // n-16)
        for (int y = 0; y < imageHeight - 16; y += 16) {
            for (int x = 0; x < imageWidth - 16; x += 16) {
                if (blocksDiffer(x, y)) {
                    if (falseBlocks != null) {
                        falseBlocks[x / 16][y / 16] = true;
                    }
                    result = false;
                }
            }
        }

        // Check image bottom
        if (imageHeight % 16 != 0) {
            for (int x = 0; x < imageWidth - 16; x += 16) {
                if (blocksDiffer(x, imageHeight - 16)) {
                    if (falseBlocks != null) {
                        falseBlocks[x / 16][yBlocks - 1] = true;
                    }
                    result = false;
                }
            }
        }

        // Check right side of image
        if (imageWidth % 16 != 0) {
            for (int y = 0; y < imageHeight - 16; y += 16) {
                if (blocksDiffer(imageWidth - 16, y)) {
                    if (falseBlocks != null) {
                        falseBlocks[xBlocks - 1][y / 16] = true;
                    }
                    result = false;
                }
            }
        }

        // Check lower right corner if necessary
        if (imageWidth % 16 != 0 && imageHeight % 16 != 0) {
            if (blocksDiffer(imageWidth - 16, imageHeight - 16)) {
                if (falseBlocks != null) {
                    falseBlocks[xBlocks - 1][yBlocks - 1] = true;
                }
                result = false;
            }
        }

        return result;
    }

    private boolean blocksDiffer(int x, int y) {
        boolean result = false;

        int[] targetBlock = new int[16 * 16], testBlock = new int[16 * 16];

        // Get 16x16 blocks from picture
        targetBlock = imageData.getReferenceBlock(x, y);
        testBlock = imageData.getComparisonBlock(x, y);

        // If arrays aren't equal then
        if (!Arrays.equals(targetBlock, testBlock)) {

            double sums = rgbCompare(targetBlock, testBlock);

            // Check if total RGB error in a macroblock exceeds
            // allowed error % if true mark block with a rectangle,
            // append block info to imageErrors
            if (sums > imageData.getDifference()) {
                imageData.debug("Error in block at position:\tx=" + x + " y="
                        + y + NEW_LINE);
                imageData.debug("RGB error for block:\t\t"
                        + roundTwoDecimals(sums * 100) + "%" + NEW_LINE
                        + NEW_LINE);

                result = true;
            }
        }
        targetBlock = testBlock = null;

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

    private boolean isCursorCheckNeeded(int xBlocks, int yBlocks,
            boolean[][] falseBlocks) {

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
                    } else if (!falseBlocks[i][j - 1] && i != firstBlockX
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
            imageData.setCursorError(x, y);
            return true;
        }

        return false;
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
    private boolean checkForCursor() {
        boolean cursor = false;

        int x = imageData.getCursorX();
        int y = imageData.getCursorY();

        int width = imageData.getReferenceImage().getWidth();
        int height = imageData.getReferenceImage().getHeight();

        // If at the outer edge move in one step.
        if (x == 0) {
            x = 1;
        }
        // If we would step over the edge move start point
        if ((x + 16) >= width) {
            x = width - 17;
        }
        // If at bottom move start point up.
        if ((y + 16) >= height) {
            y = height - 17;
        }
        //
        for (int j = y; j < y + 16; j++) {
            for (int i = x; i < x + 16; i++) {
                // if found differing pixel
                if (imageData.getComparisonImage().getRGB(i, j) != imageData
                        .getReferenceImage().getRGB(i, j)) {
                    int z = j;
                    // do while length < 30 && inside picture
                    do {
                        if ((z + 1) >= height) {
                            break;
                        }
                        // if pixels to left and right equal on both pictures
                        if (imageData.getComparisonImage().getRGB(i - 1, z) == imageData
                                .getReferenceImage().getRGB(i - 1, z)
                                && imageData.getComparisonImage().getRGB(i + 1,
                                        z) == imageData.getReferenceImage()
                                        .getRGB(i + 1, z)) {
                            // Continue if next pixel down still differs
                            if ((z + 1) < height
                                    && imageData.getComparisonImage().getRGB(i,
                                            z + 1) != imageData
                                            .getReferenceImage().getRGB(i,
                                                    z + 1)) {
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
                            && imageData.getComparisonImage().getRGB(i, z + 1) == imageData
                                    .getReferenceImage().getRGB(i, z + 1)) {
                        imageData.debug("Found cursor in test "
                                + imageData.getFileName().substring(0,
                                        imageData.getFileName().indexOf("_")));
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

    private void drawErrorsToImage(List<ErrorBlock> errorAreas) {
        // Draw lines around false ErrorBlocks before saving _diff
        // file.
        Graphics2D drawToPicture = imageData.getComparisonImage()
                .createGraphics();
        drawToPicture.setColor(Color.MAGENTA);

        int width = imageData.getComparisonImage().getWidth();
        int height = imageData.getComparisonImage().getHeight();

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
            String image, String ref_image) {
        try {
            String directory = Parameters.getScreenshotDirectory();
            if (!File.separator
                    .equals(directory.charAt(directory.length() - 1))) {
                directory = directory + File.separator;
            }

            PrintWriter writer = new PrintWriter(new File(directory
                    + ImageData.ERROR_DIRECTORY + File.separator + fileId
                    + ".html"));
            // Write head
            writer.println("<html>");
            writer.println("<head>");
            writer
                    .println("<script type=\"text/javascript\">var difference = true;function switchImage(){"
                            + "if(difference){difference = false;document.getElementById('reference').style.display='block';"
                            + "document.getElementById('diff').style.display='none';}else{difference = true;"
                            + "document.getElementById('reference').style.display='none';document.getElementById('diff').style.display='block';"
                            + "}}</script>");
            writer.println("</head>");
            writer.println("<body onclick=\"switchImage()\">");

            writer
                    .println("<div id=\"diff\" style=\"display: block; position: absolute; top: 0px; left: 0px; \"><img src=\"data:image/png;base64,"
                            + image
                            + "\"/><span style=\"position: absolute; top: 0px; left: 0px; opacity:0.4; filter: alpha(opacity=40); font-weight: bold;\">Image for this run</span></div>");
            writer
                    .println("<div id=\"reference\" style=\"display: none; position: absolute; top: 0px; left: 0px; z-index: 999;\"><img src=\"data:image/png;base64,"
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
        imageDir = new File(directory + ImageData.REFERENCE_DIRECTORY);
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        imageDir = new File(directory + ImageData.ERROR_DIRECTORY);
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

    /**
     * Check that given reference image exists in reference directory.
     * 
     * @param fileId
     *            Name of reference file
     * @return true/false
     */
    public boolean checkIfReferenceExists(String fileId) {
        String directory = Parameters.getScreenshotDirectory();

        if (directory == null || directory.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing reference directory definition. Use -D"
                            + Parameters.SCREENSHOT_DIRECTORY
                            + "=c:\\screenshot\\. ");
        }

        if (!File.separator.equals(directory.charAt(directory.length() - 1))) {
            directory = directory + File.separator;
        }

        File referenceImage = new File(directory
                + ImageData.REFERENCE_DIRECTORY + File.separator + fileId
                + ".png");

        return referenceImage.exists();
    }
}
