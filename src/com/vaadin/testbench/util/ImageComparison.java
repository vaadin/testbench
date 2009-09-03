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
import java.util.Arrays;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

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
     *            Browser window dimensions outer & inner [w, h, w, h]
     * @return true if images are the same
     */
    public boolean compareStringImage(String image, String fileId, double d,
            int[] dimensions) throws Exception {
        // Check that d value inside allowed range. if false set d to default
        // value.
        if (d < 0 || d > 1) {
            d = 0.001;
        }

        boolean result = false;

        String directory = System.getProperty(TEST_REFERENCE_DIRECTORY);

        if (directory == null || directory.length() == 0) {
            throw new IllegalArgumentException(
                    "Missing reference directory definition. Use -D"
                            + TEST_REFERENCE_DIRECTORY + "=c:\\screenshot\\. ");
        }

        // collect errors that are then written to a .log file
        StringBuilder imageErrors = new StringBuilder();
        BufferedImage test = stringToImage(image);

        try {
            // Load images
            BufferedImage target = ImageIO.read(new File(directory
                    + REFERENCE_DIRECTORY + File.separator + fileId + ".png"));

            // If images are same size, check for differences.
            if (target.getHeight() == test.getHeight()
                    && target.getWidth() == test.getWidth()) {
                // Flag result as true until proven false
                result = true;

                int browserSides = 0;
                int startPosition = 0;

                if (dimensions[1] != dimensions[3])
                    startPosition = 32;
                if (dimensions[0] != dimensions[2])
                    browserSides = 4; // start half a block in.

                // iterate picture in macroblocks of 16x16 (x,y) (0-> m-16, 0->
                // n-16)
                for (int y = startPosition; y < dimensions[1] - 16; y += 16) {
                    for (int x = browserSides; x < target.getWidth() - 16
                            - browserSides; x += 16) {
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
                                fullSum += (new Color(targetBlock[i])).getRed();
                                if (targetBlock[i] > testBlock[i]) {
                                    sum += (new Color(targetBlock[i])).getRed()
                                            - (new Color(testBlock[i]))
                                                    .getRed();
                                } else if (targetBlock[i] < testBlock[i]) {
                                    sum += (new Color(testBlock[i])).getRed()
                                            - (new Color(targetBlock[i]))
                                                    .getRed();
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
                                Graphics2D drawToPicture = test
                                        .createGraphics();
                                drawToPicture.setColor(Color.MAGENTA);
                                drawToPicture.drawRect(x, y, 15, 15);
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

                    ImageIO.write(test, "png", new File(compareFolder
                            + File.separator + fileId + "_diff.png"));
                    ImageIO.write(stringToImage(image), "png", new File(
                            compareFolder + File.separator + fileId + ".png"));
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

                ImageIO.write(test, "png", new File(compareFolder
                        + File.separator + fileId + ".png"));
                result = true;
            } catch (FileNotFoundException fnfe) {
                System.err.println("Couldn't open file");
            }
        }

        if (imageErrors.length() > 0) {
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
     * 
     * @param imageString
     * @return
     */
    private BufferedImage stringToImage(String imageString) {
        // string to ByteArrayInputStream
        BufferedImage bImage = null;
        BASE64Decoder b64dec = new BASE64Decoder();
        try {
            byte[] output = b64dec.decodeBuffer(imageString);
            ByteArrayInputStream bais = new ByteArrayInputStream(output);
            bImage = ImageIO.read(bais);
        } catch (IOException e) {
            System.err.println("Error");
        }

        return bImage;
    }

    private String encodeArray(byte[] array) {
        BASE64Encoder enc = new BASE64Encoder();
        return enc.encode(array);
    }

    private int[] decodeArray(String block) {
        BASE64Decoder dec = new BASE64Decoder();

        try {
            byte[] byteBlock = dec.decodeBuffer(block);

            int length = byteBlock.length / 4;
            int[] intBlock = new int[length];
            for (int i = 0; i < byteBlock.length - 4; i += 4) {
                intBlock[i % 4] = (byteBlock[i] << 24)
                        + ((byteBlock[i + 1] & 0xFF) << 16)
                        + ((byteBlock[i + 2] & 0xFF) << 8)
                        + (byteBlock[i + 3] & 0xFF);
            }
            return intBlock;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new int[0];
    }

    private int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    private byte[] intToByteArray(int[] value) {
        int length = value.length * 4;
        byte[] b = new byte[length];
        int i = 0;
        for (int pixel : value) {
            b[i++] = (byte) (pixel >>> 24);
            b[i++] = (byte) (pixel >>> 16);
            b[i++] = (byte) (pixel >>> 8);
            b[i++] = (byte) pixel;
        }
        return b;
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
