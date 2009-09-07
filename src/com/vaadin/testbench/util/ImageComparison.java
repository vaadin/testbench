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
            BrowserDimensions dimensions) throws Exception {
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

                            // Check if total Red error in block exceeds 0.1%
                            // if true mark block with a rectangle, append block
                            // info to imageErrors
                            if ((sum / fullSum) > d) {
                                imageErrors
                                        .append("Error in block at position:\tx="
                                                + x + " y=" + y + NEW_LINE);
                                imageErrors
                                        .append("RGB error for block:\t\t"
                                                + roundTwoDecimals((sum / fullSum) * 100)
                                                + "%" + NEW_LINE + NEW_LINE);
                                falseBlocks[x / 16][y / 16] = true;
                                // Graphics2D drawToPicture = test
                                // .createGraphics();
                                // drawToPicture.setColor(Color.MAGENTA);
                                // drawToPicture.drawRect(x, y, 15, 15);
                                // // release resources
                                // drawToPicture.dispose();
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

                    // Draw lines around false blocks. before saving _diff file.
                    Graphics2D drawToPicture = test.createGraphics();
                    drawToPicture.setColor(Color.MAGENTA);

                    for (int y = 0; y < yBlocks; y++) {
                        for (int x = 0; x < xBlocks; x++) {
                            if (falseBlocks[x][y]) {

                                if (x > 0 && y > 0) {
                                    if (falseBlocks[x - 1][y] == false) {
                                        drawToPicture.drawLine(x * 16, y * 16,
                                                x * 16, y * 16 + 16);
                                    }
                                    if (falseBlocks[x][y - 1] == false) {
                                        drawToPicture.drawLine(x * 16, y * 16,
                                                x * 16 + 16, y * 16);
                                    }
                                    if (x == xBlocks
                                            || falseBlocks[x + 1][y] == false) {
                                        drawToPicture.drawLine(x * 16 + 16,
                                                y * 16, x * 16 + 16,
                                                y * 16 + 16);
                                    }

                                    if (y == yBlocks
                                            || falseBlocks[x][y + 1] == false) {
                                        drawToPicture.drawLine(x * 16,
                                                y * 16 + 16, x * 16 + 16,
                                                y * 16 + 16);
                                    }

                                } else {
                                    drawToPicture.drawLine(0, 0, 0, 16);
                                    drawToPicture.drawLine(0, 0, 16, 0);
                                    if (falseBlocks[x + 1][y] == false) {
                                        drawToPicture.drawLine(16, 0, 16, 16);
                                    }
                                    if (falseBlocks[x][y + 1] == false) {
                                        drawToPicture.drawLine(0, 16, 16, 16);
                                    }
                                }
                            }
                        }
                    }
                    // Release resources
                    drawToPicture.dispose();

                    // collect big error blocks for css viewing of differences
                    List<ErrorBlock> errorAreas = new LinkedList<ErrorBlock>();

                    for (int y = 0; y < yBlocks; y++) {
                        for (int x = 0; x < xBlocks; x++) {
                            if (falseBlocks[x][y]) {
                                ErrorBlock newBlock = new ErrorBlock();
                                newBlock.setX(x * 16);
                                newBlock.setY(y * 16);
                                int x1 = x, y1 = y;
                                falseBlocks[x][y] = false;
                                while (true) {
                                    x1++;
                                    if ((x1 + 1) > xBlocks) {
                                        x1 = x;
                                    }

                                    if (falseBlocks[x1][y1]) {
                                        newBlock.addXBlock();
                                        falseBlocks[x1][y1] = false;
                                    } else {
                                        x1 = x;

                                        if (falseBlocks[x1][y1 + 1]) {
                                            y1++;
                                            newBlock.addYBlock();
                                            for (int z = 0; z < newBlock
                                                    .getXBlocks(); z++) {
                                                falseBlocks[x1++][y1] = false;
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
                                errorAreas.add(newBlock);
                            }
                        }
                    }

                    ImageIO.write(test, "png", new File(compareFolder
                            + File.separator + fileId + "_diff.png"));
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
