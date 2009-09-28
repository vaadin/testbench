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
    private static final String DEBUG = "com.vaadin.testbench.screenshot.reference.debug";
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
            BrowserDimensions dimensions) {
        // Check that d value inside allowed range. if false set d to default
        // value.
        if (d < 0 || d > 1) {
            d = 0.025;
        }

        boolean result = false;

        String directory = System.getProperty(TEST_SCREENS_DIRECTORY);

        // Write error blocks to file only if debug is defined as true
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
            // Load images
            BufferedImage target = ImageIO.read(new File(directory
                    + REFERENCE_DIRECTORY + File.separator + fileId + ".png"));

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
                    System.err
                            .println("Reference image height less than screenshot.");
                }
                if (target.getWidth() > test.getWidth()) {
                    minWidth = test.getWidth();
                    System.err
                            .println("Screenshot width less than reference image.");
                } else {
                    minWidth = target.getWidth();
                    System.err
                            .println("Reference image width less than screenshot.");
                }

                // Crop both images to same size
                target = target.getSubimage(0, 0, minWidth, minHeight);
                test = test.getSubimage(0, 0, minWidth, minHeight);
            }

            // Flag result as true until proven false
            result = true;

            int xBlocks = target.getWidth() / 16;
            int yBlocks = target.getHeight() / 16;
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

                        int sum = 0;
                        double fullSum = 0.0;

                        // build sums from all available colors Red, Green
                        // and Blue
                        for (int i = 0; i < targetBlock.length; i++) {
                            Color targetPixel = new Color(targetBlock[i]);
                            Color testPixel = new Color(testBlock[i]);
                            int targetColor = (targetPixel.getRed()
                                    + targetPixel.getGreen() + targetPixel
                                    .getBlue());
                            int testColor = (testPixel.getRed()
                                    + testPixel.getGreen() + testPixel
                                    .getBlue());
                            fullSum += targetColor;
                            if (targetColor > testColor) {
                                sum += targetColor - testColor;
                            } else if (testColor > targetColor) {
                                sum += testColor - targetColor;
                            }
                        }

                        // Check if total RGB error in a macroblock exceeds
                        // allowed error % if true mark block with a rectangle,
                        // append block info to imageErrors
                        if ((sum / fullSum) > d) {
                            imageErrors
                                    .append("Error in block at position:\tx="
                                            + x + " y=" + y + NEW_LINE);
                            imageErrors.append("RGB error for block:\t\t"
                                    + roundTwoDecimals((sum / fullSum) * 100)
                                    + "%" + NEW_LINE + NEW_LINE);
                            falseBlocks[x / 16][y / 16] = true;

                            result = false;
                        }
                    }
                    targetBlock = testBlock = null;
                }
            }

            // if errors found in file save diff file with marked
            // macroblocks and create html file for visuall confirmation of
            // differences
            if (result == false) {
                // Check that the comparison folder exists and create if
                // false
                File compareFolder = new File(directory + ERROR_DIRECTORY);
                if (!compareFolder.exists()) {
                    compareFolder.mkdir();
                }

                // collect big error blocks of differences
                List<ErrorBlock> errorAreas = collectErrorsToList(xBlocks,
                        yBlocks, falseBlocks);

                // Draw lines around false ErrorBlocks before saving _diff
                // file.
                Graphics2D drawToPicture = test.createGraphics();
                drawToPicture.setColor(Color.MAGENTA);

                for (ErrorBlock error : errorAreas) {
                    drawToPicture.drawRect(error.getX(), error.getY(), error
                            .getXBlocks() * 16, error.getYBlocks() * 16);
                }
                // release resources
                drawToPicture.dispose();

                // Write image with differences marked to file
                // ImageIO.write(test, "png", new File(compareFolder
                // + File.separator + fileId + "_diff.png"));
                // Write clean image to file
                ImageIO.write((stringToImage(image)).getSubimage(dimensions
                        .getCanvasXPosition(), dimensions.getCanvasYPosition(),
                        dimensions.getCanvasWidth(), dimensions
                                .getCanvasHeight()), "png", new File(
                        compareFolder + File.separator + fileId + ".png"));

                // ImageIO.write(target, "png", new File(compareFolder
                // + File.separator + fileId + "_reference.png"));

                createDiffHtml(errorAreas, fileId, encodeImageToBase64(test),
                        encodeImageToBase64(target));

                System.err
                        .println("Created clean image, image with marked differences and difference html.");
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
                System.err.println("Creating reference to " + ERROR_DIRECTORY
                        + ".");
                // Write clean image to error folder.
                ImageIO.write(referenceImage, "png", new File(directory
                        + ERROR_DIRECTORY + File.separator + fileId + ".png"));
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
                            + image + "\"/></div>");
            writer
                    .println("<div id=\"reference\" onclick=\"this.style.display='none'; document.getElementById('diff').style.display='block';\" style=\"display: none; position: absolute; top: 0px; left: 0px; z-index: 999;\"><img src=\"data:image/png;base64,"
                            + ref_image + "\"/></div>");

            for (ErrorBlock error : blocks) {
                String id = "popUpDiv_" + error.getX() + "_" + error.getY();
                // position stars so that it's not out of screen.
                writer
                        .println("<div  onmouseover=\"document.getElementById('"
                                + id
                                + "').style.display='block'\"  style=\"z-index: 66;position: absolute; top: 0px; left: 0px; clip: rect("
                                + error.getY()
                                + "px,"
                                + (error.getX() + (error.getXBlocks() * 16) + 1)
                                + "px,"
                                + (error.getY() + (error.getYBlocks() * 16) + 1)
                                + "px,"
                                + error.getX()
                                + "px);\"><img src=\"data:image/png;base64,"
                                + image + "\"/></div>");
                // Start "popup" div
                writer
                        .println("<div class=\"popUpDiv\" onclick=\"document.getElementById('reference').style.display='block'; document.getElementById('diff').style.display='none';\" onmouseout=\"this.style.display='none'\" id=\""
                                + id
                                + "\"  style=\"display: none; position: absolute; top: 0px; left: 0px; clip: rect("
                                + error.getY()
                                + "px,"
                                + (error.getX() + (error.getXBlocks() * 16) + 1)
                                + "px,"
                                + (error.getY() + (error.getYBlocks() * 16) + 1)
                                + "px," + error.getX() + "px); z-index: 99;\">");
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
    private BufferedImage stringToImage(String imageString) {
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
    private String encodeImageToBase64(BufferedImage image) {
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
}
