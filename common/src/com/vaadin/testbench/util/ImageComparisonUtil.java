package com.vaadin.testbench.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.vaadin.testbench.util.ReferenceImageRepresentation.BlockRepresentation;

public class ImageComparisonUtil {

    /**
     * Generates blocks representing an image by dividing the image up in 16x16
     * pixel blocks and calculating a mean value of the color in each block.
     * 
     * @param image
     *            the image
     * @return the block representation of the image
     */
    public static int[] generateImageBlocks(BufferedImage image) {
        int xBlocks = ImageUtil.getBlocks(image.getWidth());
        int yBlocks = ImageUtil.getBlocks(image.getHeight());

        BufferedImage scaledImage = new BufferedImage(xBlocks, yBlocks,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, xBlocks, yBlocks, null);
        graphics2D.dispose();

        return scaledImage.getRGB(0, 0, xBlocks, yBlocks, null, 0, xBlocks);
    }

    /**
     * Checks whether the screen shot blocks are equal to the reference blocks
     * within a certain error tolerance.
     * 
     * @param referenceBlocks
     *            the reference blocks
     * @param shotBlocks
     *            the screen shot blocks
     * @param tolerance
     *            the tolerance (0..1 * 16 * 16 * 3 = 0..768)
     * @return true if the blocks are equal
     */
    public static boolean blocksEqual(int[] referenceBlocks, int[] shotBlocks,
            float tolerance) {
        if (shotBlocks.length != referenceBlocks.length) {
            return false;
        }
        for (int i = 0; i < referenceBlocks.length; i++) {
            int diff = Math.abs(sumIntColor(referenceBlocks[i])
                    - sumIntColor(shotBlocks[i]));
            if (diff > tolerance) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether any of the reference images in a
     * ReferenceImageRepresentation are equal to the screen shot within the
     * specified error tolerance.
     * 
     * @param reference
     *            an object holding the block data of the reference images
     * @param shotBlocks
     *            the screen shot blocks
     * @param tolerance
     *            the tolerance (0..1 * 16 * 16 * 3 = 0..768)
     * @return
     */
    public static boolean blocksEqual(ReferenceImageRepresentation reference,
            int[] shotBlocks, float tolerance) {
        for (BlockRepresentation rep : reference.getRepresentations()) {
            if (blocksEqual(rep.getBlocks(), shotBlocks, tolerance)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sums the red, green and blue channels from an int.
     * 
     * @param col
     *            the int containing the RGB channels
     * @return the sum of the RGB channels.
     */
    private static int sumIntColor(int col) {
        return ((col >> 16) & 0xFF) + ((col >> 8) & 0xFF) + (col & 0xFF);
    }

}
