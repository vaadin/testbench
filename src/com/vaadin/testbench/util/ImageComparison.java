package com.vaadin.testbench.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.apache.commons.codec.binary.Base64;

/**
 * Class with features for comparing 2 images.
 */
public class ImageComparison {

    private static final String TEST_SCREENS_DIRECTORY = "com.vaadin.testbench.screenshot.directory";
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
     *            0.001 == 0.1%)
     * @param fileId
     *            File name for this image
     * @param dimensions
     *            Browser window dimensions
     * @return true if images are the same
     */
    public boolean compareStringImage(String image, String fileId, double d,
            BrowserDimensions dimensions, boolean testEdges) {
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

        BufferedImage test = (stringToImage(image)).getSubimage(dimensions
                .getCanvasXPosition(), dimensions.getCanvasYPosition(),
                dimensions.getCanvasWidth(), dimensions.getCanvasHeight());

        try {
            // Load images if reference not given
            BufferedImage target = ImageIO.read(new File(directory
                    + REFERENCE_DIRECTORY + File.separator + fileId + ".png"));
            if (testEdges) {
                target = detectEdges(target);
                test = detectEdges(test);
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
            // macroblocks and create html file for visuall confirmation of
            // differences
            if (result == false) {
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
                        test = (stringToImage(image))
                                .getSubimage(dimensions.getCanvasXPosition(),
                                        dimensions.getCanvasYPosition(),
                                        dimensions.getCanvasWidth(), dimensions
                                                .getCanvasHeight());
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
                        drawToPicture.drawRect(error.getX() - offsetX, error
                                .getY()
                                - offsetY, error.getXBlocks() * 16 + offsetX,
                                error.getYBlocks() * 16 + offsetY);

                    }
                    // release resources
                    drawToPicture.dispose();

                    // Write clean image to file
                    ImageIO.write((stringToImage(image)).getSubimage(dimensions
                            .getCanvasXPosition(), dimensions
                            .getCanvasYPosition(), dimensions.getCanvasWidth(),
                            dimensions.getCanvasHeight()), "png", new File(
                            compareFolder + File.separator + fileId + ".png"));

                    createDiffHtml(errorAreas, fileId,
                            encodeImageToBase64(test),
                            encodeImageToBase64(target));

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

                    test = (stringToImage(image)).getSubimage(dimensions
                            .getCanvasXPosition(), dimensions
                            .getCanvasYPosition(), dimensions.getCanvasWidth(),
                            dimensions.getCanvasHeight());

                    // Write clean image to file
                    ImageIO.write(test, "png", new File(compareFolder
                            + File.separator + fileId + ".png"));

                    // collect big error blocks of differences
                    List<ErrorBlock> errorAreas = new LinkedList<ErrorBlock>();

                    target = ImageIO.read(new File(directory
                            + REFERENCE_DIRECTORY + File.separator + fileId
                            + ".png"));

                    createDiffHtml(errorAreas, fileId,
                            encodeImageToBase64(test),
                            encodeImageToBase64(target));

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

            for (ErrorBlock error : blocks) {
                int offsetX = 0, offsetY = 0;
                if (error.getX() > 0) {
                    offsetX = 1;
                }
                if (error.getY() > 0) {
                    offsetY = 1;
                }
                String id = "popUpDiv_" + error.getX() + "_" + error.getY();
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
                                + "px); z-index: 99;\">");
                writer.println("<img src=\"data:image/png;base64," + ref_image
                        + "\" />");
                // End popup div
                writer.println("</div>");
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
     * Decodes target string from base64 to byteArray that is converted to an
     * image
     * 
     * @param imageString
     *            Base64 encoded image
     * @return BufferedImage
     */
    public BufferedImage stringToImage(String imageString) {
        // string to ByteArrayInputStream
        BufferedImage bImage = null;
        Base64 b64dec = new Base64();
        try {
            byte[] output = b64dec.decode(imageString.getBytes());
            ByteArrayInputStream bais = new ByteArrayInputStream(output);
            bImage = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bImage;
    }

    /**
     * Encodes target image to a Base64 string
     * 
     * @param image
     *            BufferedImage to encode to String
     * @return Base64 encoded String of image
     */
    public String encodeImageToBase64(BufferedImage image) {
        String encodedImage = "";
        Base64 encoder = new Base64();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] encodedBytes = encoder.encode(baos.toByteArray());
            encodedImage = new String(encodedBytes);
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage;
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

    /**
     * Runs edge detection on image and returns a grayscale image with edges.
     * 
     * @param image
     *            Base64 encoded String of image.
     * @return Base64 encoded String of image. [TYPE_BYTE_GRAY]
     */
    public String detectEdges(String image) {
        return encodeImageToBase64(robertsCrossEdges(stringToImage(image)));
    }

    /**
     * Runs edge detection on image and returns a grayscale image with edges.
     * 
     * @param image
     *            BufferedImage
     * @return BufferedImage [TYPE_BYTE_GRAY]
     */
    public BufferedImage detectEdges(BufferedImage image) {
        return robertsCrossEdges(image);
    }

    /**
     * Makes a matrix convolution on pixel. Used to find edges.
     * 
     * @param kernel
     *            convolution kernel
     * @param kernWidth
     * @param kernHeight
     * @param src
     *            Source image
     * @param x
     *            X position of pixel
     * @param y
     *            Y position of pixel
     * @param rgb
     *            int[] to save new r, g and b values
     * @return new rgb values
     */
    private int[] convolvePixel(float[] kernel, int kernWidth, int kernHeight,
            BufferedImage src, int x, int y, int[] rgb) {
        if (rgb == null) {
            rgb = new int[3];
        }

        int halfWidth = kernWidth / 2;
        int halfHeight = kernHeight / 2;

        /*
         * This algorithm pretends as though the kernel is indexed from
         * -halfWidth to halfWidth horizontally and -halfHeight to halfHeight
         * vertically. This makes the center pixel indexed at row 0, column 0.
         */

        for (int component = 0; component < 3; component++) {
            float sum = 0;
            for (int i = 0; i < kernel.length; i++) {
                int row = (i / kernWidth) - halfWidth;
                int column = (i - (kernWidth * row)) - halfHeight;

                // Check range
                if (x - row < 0 || x - row > src.getWidth()) {
                    continue;
                }
                if (y - column < 0 || y - column > src.getHeight()) {
                    continue;
                }
                int srcRGB = src.getRGB(x - row, y - column);
                sum = sum + kernel[i]
                        * ((srcRGB >> (16 - 8 * component)) & 0xff);
            }
            rgb[component] = (int) sum;
        }

        return rgb;
    }

    /**
     * Runns a Roberts Cross edge detection on image.
     * 
     * @param image
     *            BufferedImage
     * @return BufferedImage [TYPE_BYTE_GRAY] with found edges.
     */
    private BufferedImage robertsCrossEdges(BufferedImage image) {
        BufferedImage edges = new BufferedImage(image.getWidth(), image
                .getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        float[] hx = new float[] { 1, 0, 0, -1 };
        float[] hy = new float[] { 0, 1, -1, 0 };

        int[] rgbX = new int[3];
        int[] rgbY = new int[3];

        for (int x = 1; x < image.getWidth() - 1; x++) {
            for (int y = 1; y < image.getHeight() - 1; y++) {
                convolvePixel(hx, 2, 2, image, x, y, rgbX);
                convolvePixel(hy, 2, 2, image, x, y, rgbY);

                int r = Math.abs(rgbX[0]) + Math.abs(rgbY[0]);
                int g = Math.abs(rgbX[1]) + Math.abs(rgbY[1]);
                int b = Math.abs(rgbX[2]) + Math.abs(rgbY[2]);

                if (r > 255) {
                    r = 255;
                }
                if (g > 255) {
                    g = 255;
                }
                if (b > 255) {
                    b = 255;
                }

                edges.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        threshold(edges);
        return edges;
    }

    /**
     * Creates a b&w image of grayscale image.
     * 
     * @param image
     */
    private void threshold(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                double lum = lum(color);
                if (lum >= 150) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }
    }

    /**
     * Gets the luminance value for given color
     * 
     * @param color
     * @return Luminance value
     */
    private double lum(Color color) {
        // return the monochrome luminance of given color
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return .299 * r + .587 * g + .114 * b;
    }

    /**
     * Does a mild blur on the given image
     * 
     * @param image
     */
    private void blur(BufferedImage image) {

        float[] matrix = { 0.111f, 0.111f, 0.111f, 0.111f, 0.111f, 0.111f,
                0.111f, 0.111f, 0.111f, };

        int[] rgb = new int[3];

        for (int x = 1; x < image.getWidth() - 1; x++) {
            for (int y = 1; y < image.getHeight() - 1; y++) {
                convolvePixel(matrix, 3, 3, image, x, y, rgb);
                image.setRGB(x, y, getRGB(rgb));
            }
        }
    }

    /**
     * Creates a single int representation of r, g & b
     * 
     * @param rgb
     *            int[] rgb
     * @return int rgb
     */
    private int getRGB(int[] rgb) {
        int r = Math.abs(rgb[0]);
        int g = Math.abs(rgb[1]);
        int b = Math.abs(rgb[2]);

        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }

        return ((r << 16) | (g << 8) | b);
    }
}
