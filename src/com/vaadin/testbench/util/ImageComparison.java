package com.vaadin.testbench.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

/**
 * Class with features for comparing 2 images.
 */
public class ImageComparison {

    private static final String TEST_REFERENCE_DIRECTORY = "com.vaadin.testbench.tester.reference";
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
            d = 0.001;
        }

        boolean result = false;

        String directory = System.getProperty(TEST_REFERENCE_DIRECTORY);

        // Write error blocks to file only if debug is defined as true
        boolean debug = false;
        if ("true".equals(System
                .getProperty("com.vaadin.testbench.tester.debug")))
            debug = true;

        if (directory == null || directory.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing reference directory definition. Use -D"
                            + TEST_REFERENCE_DIRECTORY + "=c:\\screenshot\\. ");
        }

        // collect errors that are then written to a .log file
        StringBuilder imageErrors = new StringBuilder();
        BufferedImage test = (stringToImage(image)).getSubimage(dimensions
                .getCanvasXPosition(), dimensions.getCanvasYPosition(),
                dimensions.getCanvasWidth(), dimensions.getCanvasHeight());

        try {
            // Load images
            BufferedImage target = ImageIO.read(new File(directory
                    + REFERENCE_DIRECTORY + File.separator + fileId + ".png"));

            // If images are same size, check for differences.
            if (target.getHeight() == test.getHeight()
                    && target.getWidth() == test.getWidth()) {
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
                        targetBlock = target.getRGB(x, y, 16, 16, targetBlock,
                                0, 16);
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
                            // 0.1% if true mark block with a rectangle, append
                            // block info to imageErrors
                            if ((sum / fullSum) > d) {
                                imageErrors
                                        .append("Error in block at position:\tx="
                                                + x + " y=" + y + NEW_LINE);
                                imageErrors
                                        .append("RGB error for block:\t\t"
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
                // macroblocks
                if (result == false) {
                    // Check that the comparison folder exists and create if
                    // false
                    File compareFolder = new File(directory + ERROR_DIRECTORY);
                    if (!compareFolder.exists())
                        compareFolder.mkdir();

                    // collect big error blocks for css viewing of differences
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
                                int x1 = x, xmin = x, y1 = y;
                                falseBlocks[x][y] = false;

                                while (true) {
                                    x1++;

                                    // if x1 out of bounds set x1 to xmin where
                                    // xmin == smallest error block found for
                                    // this error
                                    if (x1 == xBlocks) {
                                        x1 = xmin;
                                    }

                                    if (x1 < 0 || x1 >= xBlocks || y1 < 0
                                            || y1 >= yBlocks)
                                        break;

                                    if (falseBlocks[x1][y1]) {
                                        newBlock.addXBlock();
                                        falseBlocks[x1][y1] = false;
                                    } else if (y1 != yBlocks) {
                                        x1 = xmin;

                                        // If next row has a false block
                                        // connected to our block
                                        boolean foundConnectedBlock = false;
                                        for (int foundX = x1; foundX < x1
                                                + newBlock.getXBlocks(); foundX++) {
                                            if (foundX == xBlocks
                                                    || y1 + 1 == yBlocks)
                                                break;

                                            if (falseBlocks[foundX][y1 + 1])
                                                foundConnectedBlock = true;
                                        }

                                        if (foundConnectedBlock) {
                                            y1++;
                                            newBlock.addYBlock();

                                            // while stepping back on this
                                            // row
                                            // is false change block x
                                            // position
                                            if (x1 - 1 >= 0) {
                                                while (falseBlocks[x1 - 1][y1]) {
                                                    falseBlocks[x1 - 1][y1] = false;
                                                    newBlock.addXBlock();
                                                    x1 = x1 - 1;
                                                    newBlock.setX(newBlock
                                                            .getX() - 16);
                                                    if (x1 == 0)
                                                        break;
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
                                            for (int j = 0; j < newBlock
                                                    .getYBlocks(); j++) {
                                                for (int i = 0; i < newBlock
                                                        .getXBlocks(); i++) {
                                                    if (x1 + i < xBlocks
                                                            && y1 + j < yBlocks) {
                                                        falseBlocks[x1 + i][y1
                                                                + j] = false;
                                                    }
                                                }
                                            }
                                            break;

                                        }
                                    }
                                }
                                errorAreas.add(newBlock);
                            }
                        }
                    }

                    // Draw lines around false ErrorBlocks before saving _diff
                    // file.
                    Graphics2D drawToPicture = test.createGraphics();
                    drawToPicture.setColor(Color.MAGENTA);

                    for (ErrorBlock error : errorAreas) {
                        drawToPicture.drawRect(error.getX(), error.getY(),
                                error.getXBlocks() * 16,
                                error.getYBlocks() * 16);
                    }
                    // release resources
                    drawToPicture.dispose();

                    // Write image with differences marked to file
                    ImageIO.write(test, "png", new File(compareFolder
                            + File.separator + fileId + "_diff.png"));
                    // Write clean image to file
                    ImageIO.write((stringToImage(image)).getSubimage(dimensions
                            .getCanvasXPosition(), dimensions
                            .getCanvasYPosition(), dimensions.getCanvasWidth(),
                            dimensions.getCanvasHeight()), "png", new File(
                            compareFolder + File.separator + fileId + ".png"));

                    createDiffHtml(errorAreas, fileId + "_diff.png", ".."
                            + File.separator + REFERENCE_DIRECTORY
                            + File.separator + fileId + ".png", fileId, target
                            .getHeight(), target.getWidth());
                }
            } else {
                System.err.println("Image sizes differ.");
                return false;
            }
        } catch (IOException e) {
            System.err.println("No comparison image." + NEW_LINE
                    + "Creating reference.");
            try {
                // Check that the comparison folder exists and create if false
                File compareFolder = new File(directory + REFERENCE_DIRECTORY);
                if (!compareFolder.exists())
                    compareFolder.mkdir();
                BufferedImage referenceImage = new BufferedImage(dimensions
                        .getCanvasWidth(), dimensions.getCanvasHeight(),
                        BufferedImage.TYPE_INT_RGB);

                Graphics2D g = (Graphics2D) referenceImage.getGraphics();
                g.drawImage(test, 0, 0, dimensions.getCanvasWidth(), dimensions
                        .getCanvasHeight(), null);
                g.dispose();

                ImageIO.write(referenceImage, "png", new File(compareFolder
                        + File.separator + fileId + ".png"));
                result = true;
            } catch (FileNotFoundException fnfe) {
                System.err.println("Couldn't open file");
            } catch (IOException ioe) {
                e.printStackTrace();
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
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return result;
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
     * @param h
     *            picture height
     * @param w
     *            picture width
     */
    private void createDiffHtml(List<ErrorBlock> blocks, String diff,
            String reference, String fileId, int h, int w) {
        try {
            PrintWriter writer = new PrintWriter(new File(System
                    .getProperty(TEST_REFERENCE_DIRECTORY)
                    + ERROR_DIRECTORY + File.separator + fileId + ".html"));
            // Write head
            writer.println("<html>");
            writer.println("<head>");
            writer.println("</head>");
            writer.println("<body>");

            writer
                    .println("<div onclick=\"document.getElementById('reference').style.display='block'\" style=\"position: absolute; top: 0px; left: 0px; \"><img src=\""
                            + diff
                            + "\" height=\""
                            + h
                            + "\" width=\""
                            + w
                            + "\"/></div>");
            writer
                    .println("<div id=\"reference\" onclick=\"this.style.display='none'\" style=\"display: none; position: absolute; top: 0px; left: 0px; z-index: 999;\"><img src=\""
                            + reference
                            + "\" height=\""
                            + h
                            + "\" width=\""
                            + w + "\"/></div>");

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
                                + "px);\"><img src=\""
                                + diff + "\"/></div>");
                // Start "popup" div
                writer
                        .println("<div class=\"popUpDiv\" onmouseout=\"this.style.display='none'\" id=\""
                                + id
                                + "\"  style=\"display: none; position: absolute; top: 0px; left: 0px; clip: rect("
                                + error.getY()
                                + "px,"
                                + (error.getX() + (error.getXBlocks() * 16) + 1)
                                + "px,"
                                + (error.getY() + (error.getYBlocks() * 16) + 1)
                                + "px," + error.getX() + "px); z-index: 99;\">");
                writer.println("<img src=\"" + reference + "\" height=\"" + h
                        + "\" width=\"" + w + "\" />");
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
     * 
     * @param imageString
     * @return
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
            System.err.println("Error");
        }

        return bImage;
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
